package gla.ac.uk.gac.classification;

import gla.ac.uk.gac.features.FeatureListListener;
import gla.ac.uk.gac.features.FeatureSet;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffSaver;



public class ModelBuilder extends AbstractWorker implements FeatureListListener, ClassificationGuessProducer{
	private Instances instances;
	private LinkedBlockingQueue<FeatureSet> instancesList = new LinkedBlockingQueue<FeatureSet>();
	private ArrayList<ClassificationGuessListener> listeners = new ArrayList<ClassificationGuessListener>();
	private Classifier classifier;
	private boolean noDataLeft;
	public ModelBuilder(Classifier classifier) {
		this.classifier = classifier;
	}
	public void writeInstancesToFile(String fileName){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		try {
			saver.setFile(new File(fileName));
			saver.setDestination(new File(fileName));   // **not** necessary in 3.5.4 and later
			saver.writeBatch();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void build(){
		
		try {
			classifier.buildClassifier(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		Instances tempInstances;
		if (isNoDataLeft() && instancesList.isEmpty()){
			setAlive(false);
		}else{
			try {
				f = instancesList.take();
			} catch (InterruptedException e) {
				return;
			}
			instances = f.getInstances();
			while(isAlive()){
				if (isNoDataLeft() && instancesList.isEmpty()){
					for(ClassificationGuessListener listener: listeners){
						listener.add(null);
					}
					setAlive(false);
				}else{
					try {
						f = instancesList.take();
					} catch (InterruptedException e) {
						continue;
					}
					tempInstances = f.getInstances();
					if (tempInstances != null){
						tempInstances.deleteWithMissingClass();
						instances.addAll(tempInstances);
					}else{
						continue;
					}
					
					if (tempInstances.size() > 0){
						ClassificationGuess guess = new ClassificationGuess(0, f, f.getInstance().classValue());
						for(ClassificationGuessListener listener: listeners){
							listener.add(guess);
						}
					}
				}
			}
		}	
	}
	public synchronized boolean isNoDataLeft() {
		return noDataLeft;
	}

	public synchronized void setNoDataLeft(boolean noDataLeft) {
		this.noDataLeft = noDataLeft;
	}
	
	@Override
	public void onFinish() {
		build();
	}

	@Override
	public void addClassificationGuessListener(
			ClassificationGuessListener listener) {
		listeners.add(listener);
		
	}

	
}
