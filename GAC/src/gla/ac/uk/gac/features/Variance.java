package gla.ac.uk.gac.features;

import gla.ac.uk.gac.UserContext;
import gla.ac.uk.gac.segmentation.Segment;
import weka.core.Attribute;
import weka.core.Instance;

public class Variance extends AbstractFeature {
	public String PREFIX = "VARIANCE_";
	private int dataSourceType;
	private int[] dimensions;
	public Variance(int dataSourceType, int[] dimensions) {
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
		double[] variance = segment.getVariance(dataSourceType);
		for(int i = 0; i < attributes.length; i++ ){
			instance.setValue(attributes[i], variance[dimensions[i]]);
		}
	}

}
