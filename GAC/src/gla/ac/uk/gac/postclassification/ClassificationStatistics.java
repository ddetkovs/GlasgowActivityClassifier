package gla.ac.uk.gac.postclassification;

import java.util.HashMap;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.classification.ClassificationGuess;
import gla.ac.uk.gac.classification.ClassificationGuessListener;

public class ClassificationStatistics implements ClassificationGuessListener {
	private HashMap<String, Integer> totalGuesses = new HashMap<String, Integer>();
	private HashMap<String, Integer> correctGuesses = new HashMap<String, Integer>();
	private HashMap<String, Integer> totalKnown = new HashMap<String, Integer>();
	
	private int[][] confusionMatrix;
	private final String[] classList;
	public ClassificationStatistics(String[] cl) {
		classList = cl;
		int length = classList.length;
		confusionMatrix = new int[length][length];
	}
	@Override
	public void add(ClassificationGuess guess) throws IllegalStateException {
		if (guess != null && !guess.getFirstInstance().classIsMissing()){
			addToStats(guess);
		}
	}
	
	public synchronized void addToStats(ClassificationGuess guess){
		confusionMatrix[guess.getKnownIndex()][guess.getGuessedIndex()]++;
		
//		String known = guess.getKnownLabel();
//		if (totalKnown.containsKey(known)){
//			totalKnown.put(known, totalKnown.get(known)+1);
//		}else{
//			totalKnown.put(known, 1);
//			totalGuesses.put(known, 0);
//			correctGuesses.put(known, 0);
//		}
//		
//		String guessLabel = guess.getGuessedLabel();
//		if(totalGuesses.containsKey(guessLabel)){
//			totalGuesses.put(guessLabel, totalGuesses.get(guessLabel)+1);
//		}else{
//			totalKnown.put(known, 1);
//			totalGuesses.put(known, 1);
//			correctGuesses.put(known, 0);
//		}
//		
//		
//		if (guessLabel.equalsIgnoreCase(known)){
//			correctGuesses.put(guessLabel, correctGuesses.get(guessLabel)+1);
//		}
	}
	
//	public synchronized double getPrecision(String label){
//		if (totalGuesses.get(label) == 0){
//			return 0;
//		}
//		return correctGuesses.get(label)/(double)totalGuesses.get(label);
//	}
//	public synchronized double getRecall(String label){
//		return correctGuesses.get(label)/(double)totalKnown.get(label);
//	}
//	
//	public double getMeanPrecision(){
//		double sum = 0; 
//		
//		for(String key: totalKnown.keySet()){
//			sum += getPrecision(key);
//		}
//		int size = totalKnown.keySet().size();
//		return sum/size;
//	}
//	
//	public double getMeanRecall(){
//		double sum = 0; 
//		
//		for(String key: totalKnown.keySet()){
//			sum += getRecall(key);
//		}
//		int size = totalKnown.keySet().size();
//		return sum/size;
//	}
//	
	public String toString(){
		StringBuilder builder = new StringBuilder();
//		for(String key: totalKnown.keySet()){
//			builder.append(key);
//			builder.append("\t\t");
//			builder.append(String.format("%.5f", getPrecision(key)));
//			builder.append("\t\t");
//			builder.append(String.format("%.5f", getRecall(key)));
//			builder.append("\n");
//		}
//		builder.append("________________________\n");
//		builder.append("Mean Precision:		");
//		builder.append(String.format("%.5f", getMeanPrecision()));
//		builder.append("\n");
//		builder.append("Mean Recall:		");
//		builder.append(String.format("%.5f", getMeanRecall()));
//		
//		builder.append("\n\n");
//		
		String formatString ="%15s";
		builder.append(String.format(formatString, "known\\guessed"));
		int length = classList.length;
		int[] verticalTotal = new int[length];
		int[] horizontalTotal = new int[length];
		for(int j = 0; j < length; j++){
			builder.append(String.format(formatString, classList[j]));
			for(int i = 0; i < length; i++){
				verticalTotal[j] += confusionMatrix[i][j];
			}
			for(int i = 0; i < length; i++){
				horizontalTotal[j] += confusionMatrix[j][i];
			}
		}
		builder.append("\n");
		
		// i horizontal - known, j vertical - guessed;
		for(int i = 0; i < length; i++){
			builder.append(String.format(formatString, classList[i]));
			for(int j = 0; j < length; j++){
//				String percent = String.format("%.2f,%.2f", 100.0*confusionMatrix[i][j]/verticalTotal[j], 100.0*confusionMatrix[i][j]/horizontalTotal[j]);
				String percent = "";
				if (i == j){
					builder.append(String.format(formatString, "( " + percent + " " + confusionMatrix[i][j] + " )"));
				}else{
					builder.append(String.format(formatString, percent + " " + confusionMatrix[i][j]));
				}
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	
}
