package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

public class RawClassificationGuess extends RawMessage {
	private static final long serialVersionUID = 3723819695589197588L;
	private double[] classificationDistribution;
	
	public RawClassificationGuess(long timestamp,
			double[] classificationDistribution) {
		super(MessageTypes.TYPE_CLASSIFICATION_DISTRIBUTION, timestamp);
		
		this.classificationDistribution = classificationDistribution;
	}
	
	public RawClassificationGuess(String[] attrs){
		super(MessageTypes.TYPE_CLASSIFICATION_DISTRIBUTION, Long.valueOf(attrs[1]));
		int dimensionality = Integer.valueOf(attrs[2]);
		classificationDistribution = new double[dimensionality];
		
		for(int i = 0; i < dimensionality; i++){
			classificationDistribution[i] = Double.valueOf(attrs[i + 3]);
		}
	}
	
	public String toString(){
		int i;
		StringBuilder builder;
		builder = new StringBuilder();
		builder.append(MessageTypes.TYPE_CLASSIFICATION_DISTRIBUTION);
		builder.append(",");
		builder.append(timestamp);
		builder.append(",");
		builder.append(classificationDistribution.length);
		builder.append(",");
		for(i = 0; i < classificationDistribution.length - 1; i++){
			builder.append(classificationDistribution[i]);
			builder.append(",");
		}
		builder.append(classificationDistribution[i]);
		return builder.toString();
	}
	public int getMaxProbabilityNumber(){
		double max = 0;
		int maxI = 0;
		for(int i = 0; i < classificationDistribution.length; i++){
			if (max < classificationDistribution[i]){
				maxI = i;
				max = classificationDistribution[i];
			}
		}
		return maxI;
		
	}
	
}
