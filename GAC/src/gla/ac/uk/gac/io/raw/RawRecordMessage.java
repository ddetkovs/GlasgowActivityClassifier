package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

public class RawRecordMessage extends RawMessage {
	private static final long serialVersionUID = 195407326702872400L;
	private float[] values;
	private int type;
	private int accuracy;
	public RawRecordMessage(int type, long timestamp, int accuracy, float[] values) {
		super(MessageTypes.TYPE_RECORD, timestamp);
		this.type = type;
		this.values = values;
		this.setAccuracy(accuracy);
	}
	public RawRecordMessage(String[] attrs){
		super(MessageTypes.TYPE_RECORD, Long.valueOf(attrs[1]));
		
		int dimensionality = Integer.valueOf(attrs[4]);
		type = Integer.valueOf(attrs[2]);
		setAccuracy(Integer.valueOf(attrs[3]));
		values = new float[dimensionality];
		for(int i = 0; i < dimensionality; i++){
			values[i] = Float.valueOf(attrs[i + 5]);
		}
	}
	public String toString(){
		int i;
		StringBuilder builder;
		builder = new StringBuilder();
		builder.append(MessageTypes.TYPE_RECORD);
		builder.append(",");
		builder.append(timestamp);
		builder.append(",");
		builder.append(type);
		builder.append(",");
		builder.append(getAccuracy());
		builder.append(",");
		builder.append(values.length); // dimensionality
		builder.append(",");
		for(i = 0; i < values.length - 1; i++){
			builder.append(values[i]);
			builder.append(",");
		}
		builder.append(values[i]);
		return builder.toString();
	}
	public float[] getValues() {
		return values;
	}
	public void setValues(float[] values) {
		this.values = values;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
}
