package gla.ac.uk.gac.chains;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.classification.ModelBuilder;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.CurrentClassGetter;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.features.Mean;
import gla.ac.uk.gac.features.Orientation;
import gla.ac.uk.gac.features.Variance;
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
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import wlsvm.WLSVM;

public class ModelBuildingChain extends AbstractChain implements WorkerListener {
	private Gatherer gatherer;
	private PreProcessor preprocessor;
	private Segmentation segmentation;
	private FeatureExtractor featureExtractor;
	private ModelBuilder simpleModelBuilder;
	private ArrayList<String> simpleClassList;
	private Filter[] filters;
	private Instances movingInstancesTemplate;
	private Instances simpleInstancesTemplate;
	public static AbstractFeature[] simpleFeatures = {
//			new OrientationInvariantMean(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0,1,2}),
			new Mean(DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER, new int[]{0}),
			new Variance(DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER, new int[]{0})
	};
	public static AbstractFeature[] movingFeatures = {
			new Mean(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER, new int[]{0,1}),
			new Variance(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER, new int[]{0,1}),
			new Mean(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_GYROSCOPE, new int[]{0,1}),
			new Variance(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_GYROSCOPE, new int[]{0,1}),
			new Variance(DataSourceTypes.TYPE_PRESSURE, new int[]{0}),
			
//			new Mean(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0}),
//			new Variance(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0}),
//			new Mean(DataSourceTypes.TYPE_SPHERICAL_GYROSCOPE, new int[]{0}),
//			new Variance(DataSourceTypes.TYPE_SPHERICAL_GYROSCOPE, new int[]{0})
//			new SAXMap(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_GYROSCOPE, new int[]{0,1}),
			
		
	};
	public static AbstractFeature[] nonMovingFeatures = {
			new Orientation()
	};
	private ArrayList<String> nonMovingClassList;
	private ArrayList<String> movingClassList;
	private Instances nonMovingTemplate;
	private Classifier simpleClassifier;
	
	private Classifier movingClassifier;
	private Classifier nonMovingClassifier;
	private ModelBuilder movingBuilder;
	private ModelBuilder nonMovingBuilder;
	private UserContext context;
	public ModelBuildingChain(DataSourceManager dataSource, ChainPreferences prefs){
		super(dataSource, prefs);
//		movingFeatures = new AbstractFeature[]{new SAXMap(prefs, DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER, new int[]{0,1})};
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

		simpleClassifier = new J48();
		
		movingClassifier = new NaiveBayes();
		nonMovingClassifier = new WLSVM();
		
		
		
	}

	@Override
	public void beforeStart() {
		super.beforeStart();
		gatherer = new Gatherer(dataSource, preferences);
		preprocessor = new PreProcessor(filters);
		segmentation = new Segmentation(preferences);
		featureExtractor = new FeatureExtractor(context, simpleInstancesTemplate, simpleFeatures);
		simpleModelBuilder = new ModelBuilder(simpleClassifier);
		ClassificationSplitter splitter = new ClassificationSplitter();
		
		FeatureExtractor movingExtractor = new FeatureExtractor(context, movingInstancesTemplate, movingFeatures);
		FeatureExtractor nonMovingExtractor = new FeatureExtractor(context, nonMovingTemplate, nonMovingFeatures);
		movingBuilder = new ModelBuilder(movingClassifier);
		nonMovingBuilder = new ModelBuilder(nonMovingClassifier);
		
		gatherer.addOutputListener(preprocessor);
		preprocessor.addOutputListener(segmentation);
		segmentation.addSegmentListener(featureExtractor);
		featureExtractor.addFeatureListListener(simpleModelBuilder);
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
		simpleModelBuilder.addClassificationGuessListener(splitter);
		simpleModelBuilder.addWorkerListener(this);
		splitter.addSplit("moving", movingExtractor);
		splitter.addSplit("nonmoving", nonMovingExtractor);
		movingExtractor.addFeatureListListener(movingBuilder);
		nonMovingExtractor.addFeatureListListener(nonMovingBuilder);
		movingBuilder.addWorkerListener(this);
		nonMovingBuilder.addWorkerListener(this);
		
		links.add(gatherer);
		links.add(preprocessor);
		links.add(segmentation);
		links.add(featureExtractor);
		links.add(simpleModelBuilder);
		links.add(movingExtractor);
		links.add(nonMovingExtractor);
		links.add(movingBuilder);
		links.add(nonMovingBuilder);
		
	}

	@Override
	public void onWorkerFinish(Worker w) {
		if (w == simpleModelBuilder) {
			simpleModelBuilder.writeInstancesToFile("./data/simple.arff");
			try {
//				System.out.println(simpleClassifier);
				weka.core.SerializationHelper.write(
						preferences.getModelOutputPath()+"simple", simpleClassifier);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}else if (w == movingBuilder) {
			movingBuilder.writeInstancesToFile("./data/moving.arff");
			try {
//				System.out.println(movingClassifier);
				weka.core.SerializationHelper.write(
						preferences.getModelOutputPath()+"moving", movingClassifier);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (w == nonMovingBuilder) {
			nonMovingBuilder.writeInstancesToFile("./data/nonmoving.arff");
			try {
//				System.out.println(nonMovingClassifier);
				weka.core.SerializationHelper.write(
						preferences.getModelOutputPath()+"nonmoving", nonMovingClassifier);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onWorkerStart(Worker w) {
		// TODO Auto-generated method stub
	}
}
