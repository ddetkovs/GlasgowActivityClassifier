package gla.ac.uk.gac.classification;

public interface ClassificationGuessListener {
	public void add(ClassificationGuess guess) throws IllegalStateException;
}
