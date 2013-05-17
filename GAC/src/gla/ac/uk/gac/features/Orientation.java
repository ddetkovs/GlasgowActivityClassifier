package gla.ac.uk.gac.features;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.io.DataSourceTypes;
import weka.core.Attribute;
import weka.core.Instance;

public class Orientation extends AbstractFeature {
	private static final String PREFIX = "ORIENTATION_";
	private static final double THRESHOLD = Math.PI/36; 
	public Orientation() {
		attributes = new Attribute[]{
			new Attribute(PREFIX + "POLAR"),
			new Attribute(PREFIX + "AZIMUTH")
		};
	}
	@Override
	public void process(UserContext context, FeatureSet f) {
		Record[] segment = f.getSegment().getSegment();
		if (segment.length > 1){
			double[] stdDeviation = f.getSegment().getStdDeviation(DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER);
			if (stdDeviation[1] < THRESHOLD && stdDeviation[2] < THRESHOLD){
				double[] mean = f.getSegment().getMean(DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER);
				context.setGravityMagnitude(mean[0]);
				context.setAzimuthRotation(mean[1]);
				context.setPolarRotation(mean[2]);
			}
		}
		
		Instance i = f.getInstance();
		i.setValue(attributes[0], context.getPolarRotation());
		i.setValue(attributes[1], context.getAzimuthRotation());
		
	}

}
