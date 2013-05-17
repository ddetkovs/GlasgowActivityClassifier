package gla.ac.uk.gac.features;

import edu.hawaii.jmotif.sax.SAXFactory;
import edu.hawaii.jmotif.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.sax.alphabet.NormalAlphabet;
import edu.hawaii.jmotif.timeseries.TSException;
import edu.hawaii.jmotif.timeseries.Timeseries;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.UserContext;
import weka.core.Attribute;
import weka.core.Instance;

public class SAX extends AbstractFeature {
	public String PREFIX = "SAX_";
	private int dataSourceType;
	private int alphabetSize = 10;
	private int paaSize = 20;
	private Alphabet alphabet = new NormalAlphabet();
	public SAX(int dataSourceType) {
		this.dataSourceType = dataSourceType;
		attributes = new Attribute[paaSize*3];
		int k = 0;
		for(int j = 0 ; j < 3; j++){
			for(int i = 0; i < paaSize; i++){
				attributes[k] = new Attribute(PREFIX + dataSourceType + "_" + i +"_" + j);
				k++;
			}
			
		}
	}

	@Override
	public void process(UserContext context, FeatureSet f) {
		Instance instance = f.getInstance();
		Record[] segment = f.getSegment().getSegment();
		double[][] s = new double[3][segment.length];
		long[] timestamps = new long[segment.length];
		for(int i = 0; i < segment.length; i++){
			float[] readings = segment[i].getSensorReadings(dataSourceType);
			if (readings != null){
				s[0][i] = readings[0];
				s[1][i] = readings[1];
				s[2][i] = readings[2];
			}else{
				s[0][i] = 0;
				s[1][i] = 0;
				s[2][i] = 0;
			}
			timestamps[i] = segment[i].getTimestamp();
		}
		
		
		try {
			for(int j = 0; j < 3; j++){
				String sax = SAXFactory.ts2string(new Timeseries(s[j], timestamps), paaSize, alphabet, alphabetSize);
//				String sax = SAXFactory.ts2saxZnormByCuts(s[j], segment.length, paaSize, alphabet.getCuts(alphabetSize)).get("").getSubstring();
				for(int i = 0; i < paaSize; i++){
					instance.setValue(attributes[i*j], sax.charAt(i)-'a');
				}
			}
		} catch (TSException e) {
			e.printStackTrace();
			return;
		}
		
		
	}

}
