package gla.ac.uk.gac.classification;

import gla.ac.uk.gac.features.FeatureListListener;
import gla.ac.uk.gac.features.FeatureSet;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffSaver;



public class CrossValidation extends AbstractWorker implements FeatureListListener, ClassificationGuessProducer{
	private Instances instances;
	private LinkedBlockingQueue<FeatureSet> instancesList = new LinkedBlockingQueue<FeatureSet>();
	private ArrayList<ClassificationGuessListener> listeners = new ArrayList<ClassificationGuessListener>();
	private Classifier classifier;
	private boolean noDataLeft;
	private Evaluation evaluation;
	private CostMatrix costMatrix;
	public CrossValidation(Classifier classifier) {
		this.classifier = classifier;
//		weka.classifiers.evaluation.
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
			evaluation = new Evaluation(instances, costMatrix);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			evaluation.crossValidateModel(classifier, instances, 10, new Random());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	public Evaluation getEvaluation(){
		return evaluation;
	}
	public void printEvaluationString(){
		try {
			System.out.println(evaluation.toCumulativeMarginDistributionString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(evaluation.toClassDetailsString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println(evaluation.toMatrixString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(evaluation.toSummaryString());
		
	
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
	
	public void setCostMatrix(CostMatrix c){
		costMatrix = c;
	}

	
}
