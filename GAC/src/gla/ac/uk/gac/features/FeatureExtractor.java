package gla.ac.uk.gac.features;

import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.segmentation.Segment;
import gla.ac.uk.gac.segmentation.SegmentListener;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class FeatureExtractor extends AbstractWorker implements SegmentListener, FeatureListProducer{ 
	public static final String CLASS_ATTRIBUTE_NAME = "class";
	public static final String INSTANCES_RELATION_NAME = "features";
	private LinkedBlockingQueue<Segment> segments = new LinkedBlockingQueue<Segment>();
	private ArrayList<FeatureListListener> outputListeners = new ArrayList<FeatureListListener>();
	private AbstractFeature[] features;
	private Instances template;
	private boolean noDataLeft;
	private CurrentClassGetter currentClassGetter = new CurrentClassGetter() {
		@Override
		public String getCurrentClass(Segment s) {
			return s.getCurrentClass();
		}
	};
	private UserContext context;

	public FeatureExtractor(UserContext context, Instances templateInstance, AbstractFeature[] features){
		this.context = context;
		template = templateInstance;
		this.features = features;
	}

	@Override
	public void add(Segment r) throws IllegalStateException {
		if(r != null){
			segments.add(r);
		}else{
			setNoDataLeft(true);
			workerThread.interrupt();
		}
	}

	@Override
	public void work() {
		Segment segment;
		Instances instances;
		Instance instance;

		while (isAlive()) {
			if (isNoDataLeft() && segments.isEmpty()){
				for (FeatureListListener listener : outputListeners) {
					listener.add(null);
				}
				setAlive(false);
			}else{
				instances = new Instances(template, 0);
				instance = new DenseInstance(instances.numAttributes());
				instances.setClass(instances.attribute(template.classAttribute().name()));
				instance.setDataset(instances);
				try {
					segment = segments.take();
				} catch (Exception e) {
					continue;
				}
				if (currentClassGetter != null){
					String currentClass = currentClassGetter.getCurrentClass(segment);
					if (currentClass!=null && instance.classAttribute().indexOfValue(currentClass) != -1){
						instance.setClassValue(currentClass);
					}
				}
				
	
				
				FeatureSet featureSet = new FeatureSet(segment, instances, instance);
				for (AbstractFeature f: features) {
					f.process(context, featureSet);
				}
				
				instances.add(instance);
				for (FeatureListListener listener : outputListeners) {
					Instances tempInstances = new Instances(instances);
					if (!instance.classIsMissing()){
						tempInstances.get(0).setClassValue(instance.classValue());
					}
					
					listener.add(new FeatureSet(segment, tempInstances, instance));
				}
			}
		}
	}

	@Override
	public void addFeatureListListener(FeatureListListener listener) {
		outputListeners.add(listener);
	}
	public synchronized boolean isNoDataLeft() {
		return noDataLeft;
	}

	public synchronized void setNoDataLeft(boolean noDataLeft) {
		this.noDataLeft = noDataLeft;
	}
	
	public void setCurrentClassGetter(CurrentClassGetter c) {
		currentClassGetter = c;
		
	}
}
