package gla.ac.uk.gac.postclassification;

import gla.ac.uk.gac.ActivityState;
import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.classification.ClassificationGuess;
import gla.ac.uk.gac.classification.ClassificationGuessListener;
import gla.ac.uk.gac.classification.ClassificationGuessProducer;
import gla.ac.uk.gac.io.raw.RawClassMessage;
import gla.ac.uk.gac.io.raw.RawDataListener;
import gla.ac.uk.gac.io.raw.RawDataProducer;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PostClassification extends AbstractWorker implements ClassificationGuessListener, ClassificationGuessProducer, RawDataProducer{
	private ArrayList<ClassificationGuessListener> listeners = new ArrayList<ClassificationGuessListener>();
	private LinkedBlockingQueue<ClassificationGuess> guessList = new LinkedBlockingQueue<ClassificationGuess>();
	private ArrayList<RawDataListener> rawListeners = new ArrayList<RawDataListener>();
//	private ClassSmoothing smoothing; 
	private boolean noDataLeft;
	public PostClassification(ChainPreferences prefs) {
//		smoothing = new ClassSmoothing(prefs);
	}	 
	
	@Override
	public void work() {
		ClassificationGuess guess;
		while(isAlive()){
			if (isNoDataLeft() && guessList.isEmpty()){
				setAlive(false);
			}else{
				try {
					guess = guessList.take();
				} catch (InterruptedException e) {
					continue;
				}
//				int out = smoothing.process(guess);

//				guess.setGuessedIndex(out);
				for(ClassificationGuessListener listener: listeners){
					listener.add(guess);
				}
				RawClassMessage message = new RawClassMessage(guess.getTimestamp(), ActivityState.Start, guess.getGuessedLabel()); 
				for (RawDataListener listener : rawListeners) {
					listener.add(message);
				}
			}
			
		}

	}
	@Override
	public void add(ClassificationGuess guess) throws IllegalStateException {
		if (guess != null){
			guessList.add(guess);
		}else{
			setNoDataLeft(true);
			workerThread.interrupt();
		}
		
	}
	@Override
	public void addRawDataListener(RawDataListener listener) {
		rawListeners.add(listener);
	}
	
	public synchronized boolean isNoDataLeft() {
		return noDataLeft;
	}

	public synchronized void setNoDataLeft(boolean noDataLeft) {
		this.noDataLeft = noDataLeft;
	}
	@Override
	public void addClassificationGuessListener(
			ClassificationGuessListener listener) {
		listeners .add(listener);
		
	}


}
