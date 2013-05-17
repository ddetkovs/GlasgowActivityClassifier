/*package gla.ac.uk.gac.features;

import java.util.HashMap;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import edu.hawaii.jmotif.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.sax.alphabet.NormalAlphabet;
import edu.hawaii.jmotif.timeseries.TSException;
import edu.hawaii.jmotif.timeseries.TSUtils;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.segmentation.Segment;

public class AccelerometerSAX extends AbstractFeature {
	private final double EARTH_GRAVITY = 9.80665;
	private final double normalizationConst = 16.985616052;//29.41995;//20; //sqrt(19.6133^2*2 + (19.6133-EARTH_GRAVITY)^2)
	private static final String MAGNITUDE_PREFIX = "MagnitudeSAX";
	private static final String POLAR_PREFIX = "PolarSAX";
	private static final String AZIMUTH_PREFIX = "AzimuthSAX";
	private static final Alphabet alphabet = new NormalAlphabet();
	private final int paaSize = 10;
	private final int alphabetSize = 14;
	private final int combinationSize = 4;
	
	private double staticAzimuthAngle;
	private double staticPolarAngle;
	
	private double[] cuts = {
			//a
			-0.8,
			//b
			-0.6,
			//c
			-0.4,
			//d
			-0.3,
			//e
			-0.2,
			-0.1,
			0,
			//f
			0.1,
			0.2,
			0.3,
			//g
			0.4,
			//h
			0.6,
			//i
			0.8
			//j		
	};
	


	public AccelerometerSAX() {
		attributes = new Attribute[(int) (Math.pow(alphabetSize,combinationSize)*3)];
		HashMap<String, Attribute> attributesHash = new HashMap<String, Attribute>();;
		char[] combination = new char[combinationSize];
		for(int i = 0; i < Math.pow(alphabetSize, combinationSize); i++){
			int k = i;
			for(int j = 0; j < combinationSize; j++){
				combination[j] = (char) ('a' + k % alphabetSize);
				k /= alphabetSize;
			}
			String s = new String(combination);
			String attrString = MAGNITUDE_PREFIX + s;
			attributesHash.put(attrString, new Attribute(attrString));
			attrString = POLAR_PREFIX  + s;
			attributesHash.put(attrString, new Attribute(attrString));
			attrString = AZIMUTH_PREFIX + s;
			attributesHash.put(attrString, new Attribute(attrString));
		}
		attributes = attributesHash.values().toArray(attributes);
	}

	@Override
	public void process(Segment s, Instance instance, Instances instances) {
		Record[] segment = s.getSegment();
		double[][] accelerometerReadings = new double[3][segment.length];
		long[] timestamps = new long[segment.length];
		int i = 0;
		double meanPolar = 0;
		double meanAzimuth = 0;
		if (segment.length > 1){
			int count = 0;
			for (Record r : segment) {
				float[] values = r
						.getSensorReadings(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION);
				if (values!=null){
					meanPolar += values[1];
					meanAzimuth += values[2];
					count++;
				}
			}
			meanPolar /= count;
			meanAzimuth /= count;
			
			double standardDeviationPolar = 0;
			double standardDeviationAzimuth = 0;
			for (Record r : segment) {
				float[] values = r
						.getSensorReadings(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION);
				if (values!=null){
					standardDeviationPolar += Math.pow(values[1] - meanPolar, 2);
					standardDeviationAzimuth += Math.pow(values[2] - meanAzimuth, 2);
				}
			}
			standardDeviationPolar /= count - 1;
			standardDeviationAzimuth /= count - 1;
			standardDeviationPolar = Math.sqrt(standardDeviationPolar);
			standardDeviationAzimuth = Math.sqrt(standardDeviationAzimuth);
			
			if (standardDeviationAzimuth < Math.PI/72 && standardDeviationPolar < Math.PI/72 ){
				staticPolarAngle = meanPolar;
				staticAzimuthAngle = meanAzimuth;
				System.out.println("static polar " + staticPolarAngle);
				System.out.println("static azimuth " + staticAzimuthAngle);
			}
		}
		for (Record r : segment) {
			float[] values = r
					.getSensorReadings(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION);
			if (values != null) {
				accelerometerReadings[0][i] = (values[0] * 1.5 / normalizationConst) - 0.9;// / normalizationConst; 
				accelerometerReadings[1][i] = (values[1] - staticPolarAngle) / Math.PI; // 0..PI => 0..2; -0.5 == 1.5
				accelerometerReadings[2][i] = (values[2] - staticAzimuthAngle) / Math.PI; // -PI..PI => -1..1; -1.5 == 0.5
				
			} else {
				accelerometerReadings[0][i] = 0;
				accelerometerReadings[1][i] = 0;
				accelerometerReadings[2][i] = 0;
			}
			timestamps[i] = r.getTimestamp();
			i++;
			
			
		}
		
//		System.out.println(accelerometerReadings[0][0] + " " + accelerometerReadings[1][0] + "  " + accelerometerReadings[2][0]);
		try {

			double[] magnitudePAA = TSUtils.paa(accelerometerReadings[0], paaSize);
			double[] polarPAA = TSUtils.paa(accelerometerReadings[1], paaSize);
			double[] azimuthPAA = TSUtils.paa(accelerometerReadings[2], paaSize);
			String magnitudeSAX = new String(TSUtils.ts2String(magnitudePAA, cuts));// alphabet.getCuts(alphabetSize)));
			String polarSAX = new String(TSUtils.ts2String(polarPAA, cuts));
			String azimuthSAX = new String(TSUtils.ts2String(azimuthPAA, cuts));
//			System.out.println(" " + magnitudeSAX + " " + polarSAX +" " + azimuthSAX);
			if (!instance.classIsMissing()) {
				System.out.println(instance.stringValue(instance
						.classAttribute()) + " " + magnitudeSAX + " " + polarSAX +" " + azimuthSAX);
				
			}
			for (int k = 0; k < magnitudeSAX.length()-combinationSize; k+=combinationSize) {
				String attributeName = MAGNITUDE_PREFIX + magnitudeSAX.substring(k, k + combinationSize);
				Attribute attr = instances.attribute(attributeName);
				if (instance.isMissing(attr)){
					instance.setValue(attr, 1);
				} else {
					instance.setValue(attr, instance.value(attr) + 1);
				}
				
				attributeName = POLAR_PREFIX + polarSAX.substring(k, k + combinationSize);
				attr = instances.attribute(attributeName);
				if (instance.isMissing(attr)) {
					instance.setValue(attr, 1);
				} else {
					instance.setValue(attr, instance.value(attr) + 1);
				}
				
				attributeName = AZIMUTH_PREFIX + azimuthSAX.substring(k, k + combinationSize);
				attr = instances.attribute(attributeName);
				if (instance.isMissing(attr)) {
					instance.setValue(attr, 1);
				} else {
					instance.setValue(attr, instance.value(attr) + 1);
				}
//				instance.setValue(attributes[alphabetSize], Math.sqrt(Math.pow(meanAzimuth,2) + Math.pow(meanPolar,2)));
			}
			
			

		} catch (TSException e) {
			e.printStackTrace();
		}
	}

}
*/