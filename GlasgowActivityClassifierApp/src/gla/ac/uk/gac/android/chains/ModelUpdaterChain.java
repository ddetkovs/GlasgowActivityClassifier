/*package gla.ac.uk.gac.android.chains;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.chains.AbstractChain;
import gla.ac.uk.gac.classification.Classification;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.io.DataSource;
import gla.ac.uk.gac.io.Gatherer;
import gla.ac.uk.gac.io.ObserverOutput;
import gla.ac.uk.gac.postclassification.PostClassification;
import gla.ac.uk.gac.preprocessing.BlurFilter;
import gla.ac.uk.gac.preprocessing.Filter;
import gla.ac.uk.gac.preprocessing.PreProcessor;
import gla.ac.uk.gac.segmentation.Segmentation;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerListener;

import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;

public class ModelUpdaterChain extends AbstractChain implements WorkerListener {
	private Gatherer gatherer;
	private PreProcessor preprocessor;
	private Segmentation segmentation;
	private FeatureExtractor featureExtractor;
	private ModelUpdater modelUpdater;
	private Classification classification;
	private PostClassification postClassification;
	private Classifier classifier;
	private ObserverOutput observerOutput;

	private ArrayList<String> classList;

	private AbstractFeature[] features = { };
	private Filter[] filters;
	private Instances instancesTemplate;

	public ModelUpdaterChain(DataSource source, ChainPreferences prefs)
			throws Exception {
		super(source, prefs);
		classList = new ArrayList<String>(Arrays.asList(prefs.getClassList()));
		instancesTemplate = createInstancesTemplate(features, classList);
		filters = new Filter[] { new BlurFilter(prefs) };
//		classifier = (Classifier) weka.core.SerializationHelper.read(prefs
//				.getModelInputPath()+"NaiveBayes.model");

		if (classifier == null) {
			classifier = new NaiveBayesUpdateable();
			classifier.buildClassifier(instancesTemplate);
		}
	}

	@Override
	public void beforeStart() {
		super.beforeStart();

		gatherer = new Gatherer(dataSource, preferences);
		preprocessor = new PreProcessor(filters);
		// preprocessor.addFilter(new Magnitude("magnitude", null));
		segmentation = new Segmentation(preferences);
		featureExtractor = new FeatureExtractor(instancesTemplate, features);
		modelUpdater = new ModelUpdater((UpdateableClassifier) classifier);
		classification = new Classification(classifier);
		postClassification = new PostClassification(preferences);
		observerOutput = new ObserverOutput();

		gatherer.addOutputListener(preprocessor);
		preprocessor.addOutputListener(segmentation);
		segmentation.addSegmentListener(featureExtractor);
		featureExtractor.addFeatureListListener(classification);
		featureExtractor.addFeatureListListener(modelUpdater);
		classification.addClassificationGuessListener(postClassification);
		modelUpdater.addWorkerListener(this);
		postClassification.addRawDataListener(observerOutput);

		links.add(gatherer);
		links.add(preprocessor);
		links.add(segmentation);
		links.add(featureExtractor);
		links.add(classification);
		links.add(modelUpdater);
		links.add(postClassification);
		links.add(observerOutput);
	}

	@Override
	public void onWorkerFinish(Worker w) {
		if (w == modelUpdater) {
			try {
				weka.core.SerializationHelper.write(
						preferences.getModelOutputPath()+ "NaiveBayes.model", classifier);
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
*/