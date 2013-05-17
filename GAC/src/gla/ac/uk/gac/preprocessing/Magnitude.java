package gla.ac.uk.gac.preprocessing;

import gla.ac.uk.gac.Record;

import java.util.Hashtable;

public class Magnitude implements Filter {

	public Magnitude() {
	}

	@Override
	public void process(Record r) {
		float temp;
		float[] sensorReading;
		Hashtable<Integer, Float> magnitudes = new Hashtable<Integer, Float>();
		for(int type: r.getTypes()){
			temp = 0;
			sensorReading = r.getSensorReadings(type);
			for(int j = 0; j < sensorReading.length; j++){
				temp += sensorReading[j] * sensorReading[j];
			}
			magnitudes.put(type, (float) Math.sqrt((double)temp));
		}

//		r.setProperty(name, magnitudes);
	}

}
