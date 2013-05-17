package gla.ac.uk.gac.features;

import gla.ac.uk.gac.segmentation.Segment;
import weka.core.Instance;
import weka.core.Instances;

public class FeatureSet {
	private Segment segment;
	private Instances instances;
	private Instance instance;
	public FeatureSet(Segment segment, Instances i, Instance instance) {
		this.instance = instance;
		this.segment = segment;
		instances = i;
	}
	public Segment getSegment() {
		return segment;
	}
	public Instances getInstances() {
		return instances;
	}
	public Instance getInstance() {
		return instance;
	}
}
