/*package gla.ac.uk.gac.postclassification;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.classification.ClassificationGuess;

import java.util.ArrayDeque;

public class ClassSmoothing {
	public static final int SIZE = 5; 
	private ArrayDeque<ClassificationGuess> deque = new ArrayDeque<ClassificationGuess>();
	private int remainingCapacity = SIZE;
	
	private double[] guessSum;
	public ClassSmoothing(ChainPreferences prefs) {
		guessSum = new double[prefs.getClassList().length];
	}
	public int process(ClassificationGuess guess){
		
		deque.add(guess);
		double[] distribution = guess.getClassDistribution();
		for(int i = 0; i < distribution.length; i++){
			guessSum[i] += distribution[i];
		}
		
		if (remainingCapacity == 0){
			ClassificationGuess removedGuess = deque.remove();
			distribution = removedGuess.getClassDistribution();
			for(int i = 0; i < distribution.length; i++){
				guessSum[i] -= distribution[i];
			}
		}else{
			remainingCapacity--;
		}
		
		int maxIndex = 0;
		double maxProb = guessSum[0];
		for(int i = 0; i < guessSum.length; i++){
			if(guessSum[i] > maxProb){
				maxIndex = i;
				maxProb = guessSum[i]; 
			}
		}
		return maxIndex;
	}
}
*/