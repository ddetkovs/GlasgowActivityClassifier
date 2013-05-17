package gla.ac.uk.gac.classification;

import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.features.FeatureListListener;
import gla.ac.uk.gac.features.FeatureSet;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class Classification extends AbstractWorker implements FeatureListListener, ClassificationGuessProducer{
	private Classifier classifier;
	private LinkedBlockingQueue<FeatureSet> instancesList = new LinkedBlockingQueue<FeatureSet>();
	private ArrayList<ClassificationGuessListener> listeners = new ArrayList<ClassificationGuessListener>();
	private boolean noDataLeft;

	public Classification(Classifier classifier){
		this.classifier = classifier;
	}
	@Override
	public void add(FeatureSet f) throws IllegalStateException {
		if (f != null){
			instancesList.add(f);
		}else{
			setNoDataLeft(true);
			workerThread.interrupt();
		}
	}
	@Override
	public void work() {
		FeatureSet f;
		Instances instances;
		double[] result = null;
		ClassificationGuess guess;
		while(isAlive()){
			if (isNoDataLeft() && instancesList.isEmpty()){
				setAlive(false);
				for(ClassificationGuessListener listener:listeners){
					listener.add(null);
				}
			}else{
				try {
					f = instancesList.take();
					
				} catch (InterruptedException e) {
					continue;
				}
				instances = f.getInstances();
				try {
					for(int i = 0; i < instances.numInstances(); i++){
						result = classifier.distributionForInstance(instances.get(i));
						guess = new ClassificationGuess(Utils.getTimestamp(), f, result);
						
						for(ClassificationGuessListener listener:listeners){
							listener.add(guess);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	@Override
	public void addClassificationGuessListener(
			ClassificationGuessListener listener) {
		listeners.add(listener);
	}
	
	public synchronized boolean isNoDataLeft() {
		return noDataLeft;
	}

	public synchronized void setNoDataLeft(boolean noDataLeft) {
		this.noDataLeft = noDataLeft;
	}

}
