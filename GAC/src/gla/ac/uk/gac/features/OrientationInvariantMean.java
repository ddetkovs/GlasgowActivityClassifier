package gla.ac.uk.gac.features;

import weka.core.Instance;
import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.segmentation.Segment;

public class OrientationInvariantMean extends Mean {
	public OrientationInvariantMean(int dataSourceType, int[] dimensions) {
		super(dataSourceType, dimensions);
	}
	@Override
	public void process(UserContext context, FeatureSet f) {
		Segment segment = f.getSegment();
		Instance instance = f.getInstance();
		double[] mean = segment.getOrientationInvariantMean(dataSourceType, context.getPolarRotation(), context.getAzimuthRotation());
		for(int i = 0; i < attributes.length; i++ ){
			instance.setValue(attributes[i], mean[dimensions[i]]);
		}
	}

}
