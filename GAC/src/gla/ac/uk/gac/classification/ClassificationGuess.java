package gla.ac.uk.gac.classification;

import gla.ac.uk.gac.features.FeatureSet;
import weka.core.Instance;
import weka.core.Instances;

public class ClassificationGuess extends FeatureSet{
	private double[] classDistribution = {};
	private String guessedLabel;
	private double guessedProb;
	private int guessedIndex;
	private long timestamp;
	public ClassificationGuess(long timestamp, FeatureSet f, double[] distribution) {
		super(f.getSegment(), f.getInstances(), f.getInstance());
		this.timestamp = timestamp;
		classDistribution = distribution;
		guessedIndex = getMaxProbabilityNumber();
		guessedProb = classDistribution[guessedIndex];
		setGuessedLabel(f.getInstances().classAttribute().value(guessedIndex));
	}
	public ClassificationGuess(long timestamp, FeatureSet f, double gindex) {
		super(f.getSegment(), f.getInstances(), f.getInstance());
		this.timestamp = timestamp;
		guessedIndex = (int) gindex;
		guessedLabel = getInstances().classAttribute().value((int) gindex);
	}
	protected int getMaxProbabilityNumber(){
		double max = 0;
		int maxI = 0;
		for(int i = 0; i < classDistribution.length; i++){
			if (max < classDistribution[i]){
				maxI = i;
				max = classDistribution[i];
			}
		}
		return maxI;
		
	}

	public String getGuessedLabel() {
		return guessedLabel;
	}

	public long getTimestamp() {
		return timestamp;
	}

	
	public Instance getFirstInstance(){
		return getInstance();
	}
	public String getKnownLabel(){
		if (getFirstInstance().classIsMissing()){
			return null;
		}
		return getFirstInstance().stringValue(getFirstInstance().classIndex());
	}

	public void setGuessedLabel(String guessedLabel) {
		this.guessedLabel = guessedLabel;
	}

	public void setGuessedIndex(int index) {
		guessedIndex = index;
		guessedProb = classDistribution[guessedIndex];
		setGuessedLabel(getInstances().classAttribute().value(guessedIndex));		
		
		
	}
	public int getGuessedIndex() {
		return guessedIndex;
	}
	public int getKnownIndex() {
		return (int) getFirstInstance().classValue();
	}

	public double[] getClassDistribution() {
		return classDistribution;
	}

	public void setClassDistribution(double[] classDistribution) {
		this.classDistribution = classDistribution;
	}
	
	
	
	
}
