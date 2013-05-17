package gla.ac.uk.gac.io;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.UserContext;

public class OrientationInvariantSource extends AbstractDataSource {
	private UserContext context;
	private int superType;

	public OrientationInvariantSource(UserContext context, int type, int superType) {
		super(type);
		this.context = context;
		this.superType = superType;
	}
	
	@Override
	public void register() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub
	}

	@Override
	public int produceToRecord(Record r) {
		float[] superReadings = r.getSensorReadings(superType);
			if (superReadings != null){
			float[] multRes = new float[3];
			double[][] matrix = context.getRotationMatrix();
			if (matrix != null){
				for(int i = 0; i < 3; i++ ){
					for(int j = 0; j < 3; j++ ){
						multRes[i] += matrix[i][j] * superReadings[j]; 
					}
				}
			}else{
				multRes[0] = superReadings[0];
				multRes[1] = superReadings[1];
				multRes[2] = superReadings[2];
			}
			
			float[] res = new float[3];
			res[0] = (float) Math.sqrt(Math.pow(multRes[0], 2) + Math.pow(multRes[1], 2));
			res[1] = multRes[2];
			res[2] = 0;
			r.setSensorReading(type, res);
		}
		return 1;
	}

}
