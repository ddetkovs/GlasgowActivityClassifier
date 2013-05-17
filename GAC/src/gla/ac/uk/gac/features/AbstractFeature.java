package gla.ac.uk.gac.features;

import gla.ac.uk.gac.UserContext;
import weka.core.Attribute;

public abstract class AbstractFeature {
	protected Attribute[] attributes;
	
	public Attribute[] getAttributes(){
		return attributes;
	}
	public abstract void process(UserContext context, FeatureSet f);
//	{
//		instance.setValue(instances.attribute(getName()), RESULT);
//	}
	
}
