package gla.ac.uk.gac.io;

import gla.ac.uk.gac.Record;

public class SphericalCoordinatesDataSource extends AbstractDataSource {
	private final int superType; 
	public SphericalCoordinatesDataSource(int type, int superType) {
		super(type);
		this.superType = superType;
	}
	@Override
	public void register() {
	}

	@Override
	public void unregister() {
	}

	@Override
	public int produceToRecord(Record r) {
		float[] values = new float[4];
		float[] superValues = r.getSensorReadings(superType);
		if (superValues == null){
			return 1;
		}
		
		values[0] = (float) Math.sqrt(superValues[0]*superValues[0] + superValues[1]*superValues[1] + superValues[2]*superValues[2]);
		if (values[0] != 0){
//			values[1] = (float) Math.acos(superValues[2]/values[0]);
			values[2] = (float) Math.asin(-superValues[1]/values[0]);
		}else{
			values[1] = 0;
		}
		
		values[1] = (float) Math.atan2(superValues[1], superValues[2]);
//		values[2] = (float) Math.atan2(superValues[1], superValues[0]);
		values[3] = (float) Math.sqrt(Math.pow(values[1],2) + Math.pow(values[1],2));
		r.setSensorReading(type, values);
		return 1;
	}

}
