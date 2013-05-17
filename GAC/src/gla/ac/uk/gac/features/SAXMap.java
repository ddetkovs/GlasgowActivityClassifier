package gla.ac.uk.gac.features;

import java.util.HashMap;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import edu.hawaii.jmotif.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.sax.alphabet.NormalAlphabet;
import edu.hawaii.jmotif.timeseries.TSException;
import edu.hawaii.jmotif.timeseries.TSUtils;
import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.UserContext;

public class SAXMap extends AbstractFeature {
	private static final String PREFIX = "SAXMAP_";
	private final String prefix;
	private static final Alphabet alphabet = new 
			NormalAlphabet();
	private final int paaSize;
	private final int alphabetSize = 7;
	private final int combinationSize = 1;
	private int dataSourceType;
	private int[] dimensions;
	private ChainPreferences preferences;
	
	public SAXMap(ChainPreferences prefs, int dataSourceType, int[] dimensions) {
		preferences = prefs;
		paaSize = preferences.getSegmentationWindowSize() / 3;
		System.out.println(String.format("%20d", paaSize));
		this.dimensions = dimensions;
		attributes = new Attribute[(int) (Math.pow(alphabetSize,combinationSize)*dimensions.length)];
		HashMap<String, Attribute> attributesHash = new HashMap<String, Attribute>();
		char[] combination = new char[combinationSize];
		
		this.dataSourceType = dataSourceType;
		prefix = PREFIX + "_" + dataSourceType;
		for(int i = 0; i < Math.pow(alphabetSize, combinationSize); i++){
			int k = i;
			for(int j = 0; j < combinationSize; j++){
				combination[j] = (char) ('a' + k % alphabetSize);
				k /= alphabetSize;
			}
			String s = new String(combination);
			
			for(int j = 0; j < dimensions.length; j ++){
				String attrString = prefix + "_" + s +"_" + dimensions[j];
				attributesHash.put(attrString, new Attribute(attrString));
			}
		}
		attributes = attributesHash.values().toArray(attributes);
	}
	@Override
	public void process(UserContext context, FeatureSet f) {
		Instance instance = f.getInstance();
		Instances instances = f.getInstances();
		Record[] segment = f.getSegment().getSegment();
		double[][] s = new double[dimensions.length][segment.length];
		long[] timestamps = new long[segment.length];
		for(int i = 0; i < segment.length; i++){
			float[] readings = segment[i].getSensorReadings(dataSourceType);
			double[] mean = f.getSegment().getMean(dataSourceType);
			double[] stdDev = f.getSegment().getStdDeviation(dataSourceType);
			if (readings != null){
				
				for(int j = 0; j < dimensions.length; j++){
					s[j][i] = (readings[dimensions[j]] - mean[j]);
 				}
			}else{
				for(int j = 0; j < dimensions.length; j++){
					s[j][i] = -mean[j];
 				}
			}
			timestamps[i] = segment[i].getTimestamp();
		}
		
		try {
			for(int j = 0; j < dimensions.length; j++){
				double[] paa = TSUtils.paa(s[j], paaSize);

				String sax = new String(TSUtils.ts2String(paa, alphabet.getCuts(alphabetSize)));
				for (int k = 0; k < sax.length()-combinationSize; k+=combinationSize) {
					String attributeName = prefix + "_" + sax.substring(k, k + combinationSize) + "_" + dimensions[j];
					Attribute attr = instances.attribute(attributeName);
					if (instance.isMissing(attr)){
						instance.setValue(attr, 1);
					} else {
						instance.setValue(attr, instance.value(attr) + 1);
					}
				}
			}
		} catch (TSException e) {
			e.printStackTrace();
			return;
		}
		

	}

}
