package gla.ac.uk.gac.chains;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.classification.CrossValidation;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.CurrentClassGetter;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.io.DataSourceManager;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.io.Gatherer;
import gla.ac.uk.gac.io.OrientationInvariantSource;
import gla.ac.uk.gac.io.SphericalCoordinatesDataSource;
import gla.ac.uk.gac.postclassification.ClassificationSplitter;
import gla.ac.uk.gac.preprocessing.BlurFilter;
import gla.ac.uk.gac.preprocessing.Filter;
import gla.ac.uk.gac.preprocessing.PreProcessor;
import gla.ac.uk.gac.segmentation.Segment;
import gla.ac.uk.gac.segmentation.Segmentation;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerListener;

import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.KStar;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import wlsvm.WLSVM;

public class CrossValidationChain extends AbstractChain implements WorkerListener {
	private UserContext context;
	private Filter[] filters;
	private ArrayList<String> simpleClassList;
	private ArrayList<String> movingClassList;
	private ArrayList<String> nonMovingClassList;
	private Instances simpleInstancesTemplate;
	private Instances movingInstancesTemplate;
	private Instances nonMovingTemplate;
	private AbstractFeature[] simpleFeatures = ModelBuildingChain.simpleFeatures;
	private AbstractFeature[] movingFeatures = ModelBuildingChain.movingFeatures;
	private AbstractFeature[] nonMovingFeatures = ModelBuildingChain.nonMovingFeatures;
	private Classifier simpleClassifier;
	private Classifier movingClassifier;
	private Classifier nonMovingClassifier;
	private CrossValidation simpleCrossValidation;
	private CrossValidation movingCrossValidation;
	private CrossValidation nonMovingCrossValidation;

	public CrossValidationChain(DataSourceManager dataSource, ChainPreferences prefs){
		super(dataSource, prefs);
		context = new UserContext();
		dataSource.addVirtualDataSource(new SphericalCoordinatesDataSource(
				DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER,
				DataSourceTypes.TYPE_ACCELEROMETER)
		);
		dataSource.addVirtualDataSource(new OrientationInvariantSource(context, 
				DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER,
				DataSourceTypes.TYPE_ACCELEROMETER)
		);
		dataSource.addVirtualDataSource(new OrientationInvariantSource(context, 
				DataSourceTypes.TYPE_ORIENTATION_INVARIANT_GYROSCOPE,
				DataSourceTypes.TYPE_GYROSCOPE)
		);
		filters = new Filter[]{new BlurFilter(prefs)};
		simpleClassList = new ArrayList<String>(Arrays.asList(prefs.getSimpleClasses()));
		movingClassList = new ArrayList<String>(Arrays.asList(prefs.getClassType("moving")));
		nonMovingClassList = new ArrayList<String>(Arrays.asList(prefs.getClassType("nonmoving")));
		simpleInstancesTemplate = createInstancesTemplate(simpleFeatures, simpleClassList);
		movingInstancesTemplate = createInstancesTemplate(movingFeatures, movingClassList);
		nonMovingTemplate = createInstancesTemplate(nonMovingFeatures, nonMovingClassList);
		simpleClassifier = new RandomForest();
		
		movingClassifier = new RandomForest();
		nonMovingClassifier = new RandomForest();
	}
	
	
	@Override
	public void beforeStart() {
		super.beforeStart();
		Gatherer gatherer = new Gatherer(dataSource, preferences);
		PreProcessor preprocessor = new PreProcessor(filters);
		Segmentation segmentation = new Segmentation(preferences);
		FeatureExtractor featureExtractor = new FeatureExtractor(context, simpleInstancesTemplate, simpleFeatures);
		simpleCrossValidation = new CrossValidation(simpleClassifier);
		ClassificationSplitter splitter = new ClassificationSplitter();
		
		FeatureExtractor movingExtractor = new FeatureExtractor(context, movingInstancesTemplate, movingFeatures);
		FeatureExtractor nonMovingExtractor = new FeatureExtractor(context, nonMovingTemplate, nonMovingFeatures);
		movingCrossValidation = new CrossValidation(movingClassifier);
		nonMovingCrossValidation = new CrossValidation(nonMovingClassifier);
		
		CostMatrix movingCostMatrix = new CostMatrix(movingClassList.size());
		movingCostMatrix.setCell(0, 1, 0.1);
		movingCostMatrix.setCell(1, 0, 0.1);
		movingCostMatrix.setCell(2, 3, 0.1);
		movingCostMatrix.setCell(3, 2, 0.1);
		
		
		
		gatherer.addOutputListener(preprocessor);
		preprocessor.addOutputListener(segmentation);
		segmentation.addSegmentListener(featureExtractor);
		featureExtractor.addFeatureListListener(simpleCrossValidation);
		featureExtractor.setCurrentClassGetter(new CurrentClassGetter() {
			@Override
			public String getCurrentClass(Segment s) {
				String c = s.getCurrentClass();
				if (nonMovingClassList.contains(c)){
					return "nonmoving";
				}else if(movingClassList.contains(c)){
					return "moving";
				}
				return null;
			}
		});
		simpleCrossValidation.addClassificationGuessListener(splitter);
		simpleCrossValidation.addWorkerListener(this);
		splitter.addSplit("moving", movingExtractor);
		splitter.addSplit("nonmoving", nonMovingExtractor);
		movingExtractor.addFeatureListListener(movingCrossValidation);
		nonMovingExtractor.addFeatureListListener(nonMovingCrossValidation);
		movingCrossValidation.addWorkerListener(this);
		nonMovingCrossValidation.addWorkerListener(this);
		
		movingCrossValidation.setCostMatrix(movingCostMatrix);
		links.add(gatherer);
		links.add(preprocessor);
		links.add(segmentation);
		links.add(featureExtractor);
		links.add(simpleCrossValidation);
		links.add(movingExtractor);
		links.add(nonMovingExtractor);
		links.add(movingCrossValidation);
		links.add(nonMovingCrossValidation);
	}
	public double getPrecision(){
		return simpleCrossValidation.getEvaluation().weightedPrecision() * 
				(movingCrossValidation.getEvaluation().weightedPrecision() + nonMovingCrossValidation.getEvaluation().weightedPrecision())/2;
	}

	
	@Override
	public void onWorkerFinish(Worker w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWorkerStart(Worker w) {
		// TODO Auto-generated method stub
	}

	public void saveInstances(String path){
		simpleCrossValidation.writeInstancesToFile(path + "simple.arff");
		movingCrossValidation.writeInstancesToFile(path + "moving.arff");
		nonMovingCrossValidation.writeInstancesToFile(path + "nonmoving.arff");
	}
	public void printEvaluationString() {
		simpleCrossValidation.printEvaluationString(); 
		movingCrossValidation.printEvaluationString();
		nonMovingCrossValidation.printEvaluationString();
		
		System.out.println(String.format("OVERALL PRECISION %.2f", getPrecision()));
		
	}
	

}
