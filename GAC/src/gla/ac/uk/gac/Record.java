package gla.ac.uk.gac;

import java.util.Hashtable;
import java.util.Set;


public class Record{

	private long timestamp = Utils.getTimestamp();
	private String currentClass = null;
	private Hashtable<String, Hashtable<Integer, Float>> properties = new Hashtable<String, Hashtable<Integer, Float>>();
	private Hashtable<Integer, Values> sensorReadings = new Hashtable<Integer, Values>();

	public Record(){
	}

	public Record(long timestamp){
		this.timestamp = timestamp;
	}

	public Record(Record r){
		sensorReadings = r.getSensorReadings();
		this.timestamp = r.getTimestamp();
	}

	public synchronized Hashtable<Integer, Values> getSensorReadings(){
		return sensorReadings;
	}

	public synchronized long getTimestamp() {
		return timestamp;
	}
	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public synchronized void setSensorReading(int type, float[] value){
		sensorReadings.put(type, new Values(value));
	}
	public Set<Integer> getTypes(){
		return sensorReadings.keySet();
	}
	public synchronized float[] getSensorReadings(int type){
		if (sensorReadings.containsKey(type)){
			return sensorReadings.get(type).getValues();
		}else{
			return null;
		}
	}
	public boolean hasSensor(int type){
		return sensorReadings.containsKey(type);
	}
	public boolean hasProperty(String property){
		return properties.containsKey(property);
	}
	public synchronized Hashtable<Integer, Float> getProperty(String property){
		return properties.get(property);
	}
	public synchronized void setProperty(String property, Hashtable<Integer, Float> value){
		properties.put(property, value);
	}

	public synchronized String getCurrentClass() {
		return currentClass;
	}

	public synchronized void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}
	public synchronized void copyTo(Record r){
		for(Integer type: sensorReadings.keySet()){
			r.setSensorReading(type, getSensorReadings(type));
		}
		for(String property: properties.keySet()){
			r.setProperty(property, properties.get(property));
		}
		r.setCurrentClass(currentClass);
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		float[] temp;
		builder.append("time = ");
		builder.append(timestamp);
		builder.append("\n");
		for(Integer type: sensorReadings.keySet()){
			builder.append(type);
			builder.append(" = ");
			temp = getSensorReadings(type);
			if (temp != null){
				for(int j = 0; j < temp.length; j++){
					builder.append(temp[j]);
					builder.append("; ");
				}
			} else {
				builder.append("null;");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
