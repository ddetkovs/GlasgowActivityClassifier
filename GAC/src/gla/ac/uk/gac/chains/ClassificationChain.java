package gla.ac.uk.gac.chains;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.classification.Classification;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.CurrentClassGetter;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.features.Mean;
import gla.ac.uk.gac.features.Orientation;
import gla.ac.uk.gac.features.OrientationInvariantMean;
import gla.ac.uk.gac.features.SAXMap;
import gla.ac.uk.gac.features.Variance;
import gla.ac.uk.gac.io.DataSource;
import gla.ac.uk.gac.io.DataSourceManager;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.io.Gatherer;
import gla.ac.uk.gac.io.OrientationInvariantSource;
import gla.ac.uk.gac.io.SphericalCoordinatesDataSource;
import gla.ac.uk.gac.postclassification.ClassificationSplitter;
import gla.ac.uk.gac.postclassification.ClassificationStatistics;
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
import weka.core.Instances;

public class ClassificationChain extends AbstractChain implements WorkerListener{

	private Gatherer gatherer;
	private PreProcessor preprocessor;
	private Segmentation segmentation;
	private FeatureExtractor featureExtractor;
	private Classification simpleClassification;
	private Filter[] filters;
	private ArrayList<String> simpleClassList;
	private ArrayList<String> movingClassList;
	private ArrayList<String> nonMovingClassList;
	private Instances simpleInstancesTemplate;
	private Instances movingInstancesTemplate;
	private Instances nonMovingTemplate;
//	private AbstractFeature[] simpleFeatures = {
////			new OrientationInvariantMean(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0,1,2}),
//			new Mean(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0}),
//			new Variance(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, new int[]{0})
//	};
	
	private AbstractFeature[] simpleFeatures = ModelBuildingChain.simpleFeatures;
	private AbstractFeature[] movingFeatures = ModelBuildingChain.movingFeatures;
	private AbstractFeature[] nonMovingFeatures = ModelBuildingChain.nonMovingFeatures;
	private Classifier simpleClassifier;
	private Classifier nonMovingClassifier;
	private Classifier movingClassifier;
	private Classification movingClassification;
	private Classification nonMovingClassification;
	private ClassificationStatistics simpleStats;
	private ClassificationStatistics movingStats;
	private ClassificationStatistics nonMovingStats;
	private UserContext context;
	
	public ClassificationChain(DataSourceManager source, ChainPreferences prefs){
		super(source, prefs);
		context = new UserContext();
		dataSource.addVirtualDataSource(new SphericalCoordinatesDataSource(
				DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER,
				DataSourceTypes.TYPE_ACCELEROMETER)
		);
		dataSource.addVirtualDataSource(new OrientationInvariantSource(context, 
				DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER,
				DataSourceTypes.TYPE_ACCELEROMETER)
		);
		
		filters = new Filter[]{new BlurFilter(prefs)};
		simpleClassList = new ArrayList<String>(Arrays.asList(prefs.getSimpleClasses()));
		movingClassList = new ArrayList<String>(Arrays.asList(prefs.getClassType("moving")));
		nonMovingClassList = new ArrayList<String>(Arrays.asList(prefs.getClassType("nonmoving")));
		simpleInstancesTemplate = createInstancesTemplate(simpleFeatures, simpleClassList);
		movingInstancesTemplate = createInstancesTemplate(movingFeatures, movingClassList);
		nonMovingTemplate = createInstancesTemplate(nonMovingFeatures, nonMovingClassList);
		
		try {
			simpleClassifier = (Classifier) weka.core.SerializationHelper.read(prefs.getModelInputPath()+"simple");
			movingClassifier = (Classifier) weka.core.SerializationHelper.read(prefs.getModelInputPath()+"moving");
			nonMovingClassifier = (Classifier) weka.core.SerializationHelper.read(prefs.getModelInputPath()+"nonmoving");
		} catch (Exception e) {
			return;
		}
		
	}
	@Override
	public void beforeStart() {
		super.beforeStart();

		gatherer = new Gatherer(dataSource, preferences);
		preprocessor = new PreProcessor(filters);
//		preprocessor.addFilter(new Magnitude("magnitude", null));
		segmentation = new Segmentation(preferences);
		featureExtractor = new FeatureExtractor(context, simpleInstancesTemplate, simpleFeatures);
		simpleClassification = new Classification(simpleClassifier);
		ClassificationSplitter splitter = new ClassificationSplitter();
		FeatureExtractor movingExtractor = new FeatureExtractor(context, movingInstancesTemplate, movingFeatures);
		FeatureExtractor nonMovingExtractor = new FeatureExtractor(context, nonMovingTemplate, nonMovingFeatures);
		movingClassification = new Classification(movingClassifier);
		nonMovingClassification = new Classification(nonMovingClassifier);
		simpleStats = new ClassificationStatistics(preferences.getSimpleClasses());
		movingStats = new ClassificationStatistics(preferences.getClassType("moving"));
		nonMovingStats = new ClassificationStatistics(preferences.getClassType("nonmoving"));
//		postClassification = new PostClassification(preferences);
//		stats = new ClassificationStatistics(preferences);
		
		gatherer.addOutputListener(preprocessor);
		preprocessor.addOutputListener(segmentation);
		segmentation.addSegmentListener(featureExtractor);
		featureExtractor.addFeatureListListener(simpleClassification);
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
		
		simpleClassification.addClassificationGuessListener(splitter);
		simpleClassification.addClassificationGuessListener(simpleStats);
		simpleClassification.addWorkerListener(this);
		splitter.addSplit("moving", movingExtractor);
		splitter.addSplit("nonmoving", nonMovingExtractor);
		movingExtractor.addFeatureListListener(movingClassification);
		nonMovingExtractor.addFeatureListListener(nonMovingClassification);
		movingClassification.addClassificationGuessListener(movingStats);
		movingClassification.addWorkerListener(this);
		nonMovingClassification.addClassificationGuessListener(nonMovingStats);
		nonMovingClassification.addWorkerListener(this);
//		postClassification.addClassificationGuessListener(stats);
//		postClassification.addWorkerListener(this);

		links.add(gatherer);
		links.add(preprocessor);
		links.add(segmentation);
		links.add(featureExtractor);
		links.add(simpleClassification);
		links.add(movingExtractor);
		links.add(nonMovingExtractor);
		links.add(movingClassification);
		links.add(nonMovingClassification);
//		links.add(postClassification);
	}
	@Override
	public void onWorkerFinish(Worker w) {
		if (w == simpleClassification){
			System.out.println(simpleStats);
		}else if(w == movingClassification){
			System.out.println(movingStats);
		}else if(w == nonMovingClassification){
			System.out.println(nonMovingStats);
		}
	}
	@Override
	public void onWorkerStart(Worker w) {
		// TODO Auto-generated method stub
		
	}
	
}
