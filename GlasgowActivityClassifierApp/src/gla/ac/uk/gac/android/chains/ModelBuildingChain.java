/*package gla.ac.uk.gac.android.chains;

import gla.ac.uk.gac.chains.AbstractChain;
import gla.ac.uk.gac.classification.ModelBuilder;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.AccelerometerMeanAndVariance;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.io.Gatherer;
import gla.ac.uk.gac.io.raw.RawFileInput;
import gla.ac.uk.gac.preprocessing.BlurFilter;
import gla.ac.uk.gac.preprocessing.Filter;
import gla.ac.uk.gac.preprocessing.PreProcessor;
import gla.ac.uk.gac.segmentation.Segmentation;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerThread;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;

public class ModelBuildingChain extends AbstractChain {
	private static final int SAMPLING_FREQUENCY = 50; //hz
	private static final int SAMPLING_DELAY = 1000 / SAMPLING_FREQUENCY;
	private RawFileInput rawInput;
	private Gatherer gatherer;
	private Thread workerThread;
	private PreProcessor preprocessor;
	private Segmentation segmentation;
	private FeatureExtractor featureExtractor;
	private ModelBuilder modelBuilder;
	private Classifier classifier;
	private ArrayList<String> classList;
	private AbstractFeature[] features = {new AccelerometerMeanAndVariance()};
	private Filter[] filters = {new BlurFilter()};
	private Instances instancesTemplate;
	private static final String modelPath = "NaiveBayes.model";

	public ModelBuildingChain(String fileName, String[] classList){
		super();
		this.classList = new ArrayList<String>(Arrays.asList(classList));
		rawInput = new RawFileInput(SAMPLING_DELAY, new File(fileName));
		instancesTemplate = createInstancesTemplate(features, this.classList);

		classifier = new NaiveBayesUpdateable();
		
	}

	@Override
	public void init() {
		links = new ArrayList<Worker>();
		links.add(rawInput);
		
		gatherer = new Gatherer(rawInput, 0);
		preprocessor = new PreProcessor(filters);
		segmentation = new Segmentation();
		featureExtractor = new FeatureExtractor(instancesTemplate, features);
		
		modelBuilder = new ModelBuilder(classifier);
		gatherer.addOutputListener(preprocessor);
		preprocessor.addOutputListener(segmentation);
		segmentation.addSegmentListener(featureExtractor);
		featureExtractor.addFeatureListListener(modelBuilder);

		links.add(gatherer);
		links.add(preprocessor);
		links.add(segmentation);
		links.add(featureExtractor);
		links.add(modelBuilder);
	}
	@Override
	public void execute(){
		super.execute();
		workerThread = new Thread(new WorkerThread(this));
		workerThread.start();

	}
	@Override
	public void cancel(){
		super.cancel();
		try {
			weka.core.SerializationHelper.write(modelPath, classifier);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		workerThread.interrupt();
		try {
			workerThread.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void work() {
		while(rawInput.isAlive()){
			synchronized (rawInput) {
				try {
					rawInput.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		super.cancel();
	}
}
*/