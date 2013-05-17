/*package gla.ac.uk.gac.features;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.segmentation.Segment;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class PressureDeviation extends AbstractFeature {

	public PressureDeviation() {
		attributes = new Attribute[]{
				new Attribute("PressureDeviation")
		};
	}


	@Override
	public void process(Segment s, Instance instance, Instances instances) {
		Record[] segment = s.getSegment();
		int count = 0;
		double sum = 0;
		ArrayList<Float> pressures = new ArrayList<Float>();
		for(Record r: segment){
			float[] values = r.getSensorReadings(DataSourceTypes.TYPE_PRESSURE);
			if (values != null){
				sum += values[0];
				count ++;
				pressures.add(values[0]);
			}
		}
		if (count == 0){
			return;
		}
		
		double mean = sum / count;
		sum = 0;
		for(double pressure: pressures){
			sum += Math.pow(pressure - mean, 2);
		}
		sum /= count - 1;
		sum = Math.sqrt(sum);
		instance.setValue(attributes[0], sum);
//		if ((int)instance.classValue() > 6){
//			System.out.println(instance.classValue() + " " + sum);
//		}

	}

}
*/