/*package gla.ac.uk.gac.features;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.segmentation.Segment;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class AccelerometerMeanAndVariance extends AbstractFeature {
	private static final String NAME = "AccelerometerMeanAndVariance";
	private static final String MEAN_PREFIX = "AccelerometerMean";
	private static final String VARIANCE_PREFIX = "AccelerometerVariance";
	private static final String GRAVITY_MEAN_PREFIX = "GravityMean";
	private double staticAzimuthAngle;
	private double staticPolarAngle;
	public AccelerometerMeanAndVariance() {
		attributes = new Attribute[]{

			new Attribute("AccelerometerMagnitudeMean"),
			new Attribute("AccelerometerMagnitudeVariance"),
			new Attribute("AccelerometerPolarMean"),
			new Attribute("AccelerometerPolarVariance"),
			new Attribute("AccelerometerAzimuthMean"),
			new Attribute("AccelerometerAzimuthVariance"),
			
		};
	}
	@Override
	public void process(Segment s, Instance instance, Instances instances) {
		Record[] segment = s.getSegment();
		double [][] meanAndVariance = new double[3][2];
		int totalCount = 0;
		if (segment.length > 1){
			double meanPolar = 0;
			double meanAzimuth = 0;
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
//				System.out.println("static polar " + staticPolarAngle);
//				System.out.println("static azimuth " + staticAzimuthAngle);
			}
		}
		
		
		for(int i = 0; i < segment.length; i++){
			float[] reading = segment[i].getSensorReadings(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION);
			if (reading != null){
				totalCount++;
				meanAndVariance[0][0] += reading[0];
				meanAndVariance[0][1] += reading[0] * reading[0];
				double polar = reading[1] - staticPolarAngle;
				meanAndVariance[1][0] += polar;
				meanAndVariance[1][1] += Math.pow(polar, 2);
				double azimuth = reading[2] - staticAzimuthAngle;
				meanAndVariance[2][0] += azimuth;
				meanAndVariance[2][1] += Math.pow(azimuth, 2);
			}	
		}

		for (int j = 0; j < 3; j++){
			for(int i = 0; i < 2; i++){
				meanAndVariance[j][i] = meanAndVariance[j][i] / totalCount;
				System.out.println(instance.classValue() + "  " + meanAndVariance[j][i]);
				instance.setValue(instances.attribute(attributes[j*i].name()), meanAndVariance[j][i]);
			}
		}
		return;
		
	}


}
*/