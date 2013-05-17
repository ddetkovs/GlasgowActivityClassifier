package gla.ac.uk.gac.features;

import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.segmentation.Segment;
import weka.core.Attribute;
import weka.core.Instance;

public class Mean extends AbstractFeature {
	private static final String PREFIX = "MEAN_";
	protected int dataSourceType;
	protected int[] dimensions;
	public Mean(int dataSourceType, int[] dimensions) {
		this.dataSourceType = dataSourceType;
		this.dimensions = dimensions;
		attributes = new Attribute[dimensions.length];
		for(int i = 0; i < dimensions.length; i++){
			attributes[i] = new Attribute(PREFIX + dataSourceType + "_" + dimensions[i]);
		}
	}

	@Override
	public void process(UserContext context, FeatureSet f) {
		Segment segment = f.getSegment();
		Instance instance = f.getInstance();
		double[] mean = segment.getMean(dataSourceType);
		for(int i = 0; i < attributes.length; i++ ){
			instance.setValue(attributes[i], mean[dimensions[i]]);
		}
	}
}
