package gla.ac.uk.gac.postclassification;

import gla.ac.uk.gac.classification.ClassificationGuess;
import gla.ac.uk.gac.classification.ClassificationGuessListener;
import gla.ac.uk.gac.segmentation.SegmentListener;

import java.util.HashMap;

public class ClassificationSplitter implements ClassificationGuessListener{
//	private ArrayList<SegmentListener> listeners = new ArrayList<SegmentListener>();
	private HashMap<String, SegmentListener> listeners = new HashMap<String, SegmentListener>();
	@Override
	public void add(ClassificationGuess guess) throws IllegalStateException {
		if (guess == null){
			for(String key: listeners.keySet()){
				listeners.get(key).add(null);
			}
			return;
		}
		if (listeners.containsKey(guess.getGuessedLabel())){
			listeners.get(guess.getGuessedLabel()).add(guess.getSegment());
		}
	}
	
	public void addSplit(String classLabel, SegmentListener listener) {
		listeners.put(classLabel, listener);
	}

}
