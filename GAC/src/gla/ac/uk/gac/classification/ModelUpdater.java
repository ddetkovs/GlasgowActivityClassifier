//package gla.ac.uk.gac.classification;
//
//import gla.ac.uk.gac.features.FeatureListListener;
//import gla.ac.uk.gac.threading.AbstractWorker;
//
//import java.util.concurrent.LinkedBlockingQueue;
//
//import weka.classifiers.UpdateableClassifier;
//import weka.core.Instances;
//
//
//public class ModelUpdater extends AbstractWorker implements FeatureListListener{
//	private LinkedBlockingQueue<Instances> instancesList = new LinkedBlockingQueue<Instances>();
//	private UpdateableClassifier classifier;
//	public ModelUpdater(UpdateableClassifier classifier) {
//		this.classifier = classifier;
//	}
//	@Override
//	public void add(Instances instances) throws IllegalStateException {
//		instancesList.add(instances);
//	}
//	@Override
//	public void work() {
//		Instances tempInstances;
//		while(!Thread.interrupted()){
//			try {
//				tempInstances = instancesList.take();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				return;
//			}
//			tempInstances.deleteWithMissingClass();
//			for(int i = 0; i < tempInstances.numInstances(); i++){
//				try {
//					classifier.updateClassifier(tempInstances.instance(i));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}		
//	}
//	
//}
