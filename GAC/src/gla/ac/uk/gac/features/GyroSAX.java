/*package gla.ac.uk.gac.features;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import edu.hawaii.jmotif.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.sax.alphabet.NormalAlphabet;
import edu.hawaii.jmotif.timeseries.TSException;
import edu.hawaii.jmotif.timeseries.TSUtils;
import edu.hawaii.jmotif.timeseries.Timeseries;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.segmentation.Segment;

public class GyroSAX extends AbstractFeature {
//	private final double EARTH_GRAVITY = 9.80665;
	private final double normalizationConst = Math.sqrt(Math.pow(8.726646, 2) * 3) / 2;
//	private final double EARTH_GRAVITY_CORRECTION = EARTH_GRAVITY * normalizationConst;
	private static final String prefix = "GyroSAX";
	private static final Alphabet alphabet = new NormalAlphabet();
	private final int paaSize = 15;
	private final int alphabetSize = 12;
	private final int combinationSize = 1;
	private double[] cuts = {-1.0,
			-0.8,
			-0.656720537979,
			-0.409191894269,
			-0.197687794972,
			0.0,
			0.197687794972,
			0.409191894269,
			0.656720537979,
			0.8,
			1.0};

	private long getNumberFromString(String s) {
		char[] array = s.toCharArray();
		for (int i = 0; i < array.length; i++) {
			array[i] += '0' - 'a';
		}
		return Long.valueOf(new String(array), alphabetSize);

	}

	public GyroSAX() {
		attributes = new Attribute[alphabetSize];
		char letter = 'a';

		// char[] combination = new char[combinationSize];
		// for(int i = 0; i < combinationSize; i++){
		// combination[i] = 'a';
		// }
		//
		// for(int i = 0; i < combinationSize; i++){
		// combination[i] = 'a';
		// }

		for (int i = 0; i < alphabetSize; i++, letter++) {
			attributes[i] = new Attribute(prefix + letter);
		}

//		attributes[alphabetSize] = new Attribute(prefix + "Number");

	}


	@Override
	public void process(Segment s, Instance instance, Instances instances) {
		Record[] segment = s.getSegment();
		double[][] accelerometerReadings = new double[3][segment.length];
		long[] timestamps = new long[segment.length];
		int i = 0;
		for (Record r : segment) {
			float[] values = r
					.getSensorReadings(DataSourceTypes.TYPE_SPHERICAL_GYROSCOPE);
			if (values != null) {
				accelerometerReadings[0][i] = values[0] / normalizationConst; 
				accelerometerReadings[1][i] = values[1];
				accelerometerReadings[2][i] = values[2];
				
			} else {
				accelerometerReadings[0][i] = 0;
				accelerometerReadings[1][i] = 0;
				accelerometerReadings[2][i] = 0;
			}
			timestamps[i] = r.getTimestamp();
			i++;

		}
		Timeseries seriesA;
		// try {
		// seriesA = new Timeseries(accelerometerReadings[0], timestamps);
		// } catch (TSException e) {
		// e.printStackTrace();
		// return;
		// }
		// try {
		// String timeseriesAsax3 = SAXFactory.ts2string(seriesA, 3, alphabet,
		// 3);
		// System.out.println(timeseriesAsax3);
		// } catch (TSException e) {
		// e.printStackTrace();
		// return;
		// }

		// try {
		// double[] cuts = alphabet.getCuts(alphabetSize);
		// for(double k: cuts){
		// // System.out.print(k + " ");
		// }
		// // System.out.println();
		// } catch (TSException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		try {

			double[] paa = TSUtils.paa(accelerometerReadings[0], paaSize);
			String sax = new String(TSUtils.ts2String(paa, cuts));// alphabet.getCuts(alphabetSize)));

			StringBuilder builder = new StringBuilder();
			for (double p : paa) {
				builder.append(p);
				builder.append(", ");
			}

			if (!instance.classIsMissing()) {
				System.out.println(instance.stringValue(instance
						.classAttribute()) + " " + sax);
				System.out.println(builder.toString());
			}
			for (int k = 0; k < sax.length(); k++) {
				String attributeName = prefix + sax.charAt(k);
				Attribute attr = instances.attribute(attributeName);
				if (instance.isMissing(attr)) {
					instance.setValue(attr, 1);
				} else {
					instance.setValue(attr, instance.value(attr) + 1);
				}
			}
//			System.out.println(getNumberFromString(sax));
//			instance.setValue(attributes[alphabetSize],	getNumberFromString(sax));

		} catch (TSException e) {
			e.printStackTrace();
		}
	}

}
*/