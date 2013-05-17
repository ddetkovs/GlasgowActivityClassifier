package gla.ac.uk.gac.io;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.RecordListener;
import gla.ac.uk.gac.RecordProducer;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;

public class Gatherer extends AbstractWorker implements RecordProducer{
	private DataSource dataSource;
	private ArrayList<RecordListener> outputListeners = new ArrayList<RecordListener>();
	private final int period;
	
	public Gatherer(DataSource dataSource, ChainPreferences prefs){
		this.dataSource = dataSource;
		period = prefs.getGathererSamplingDelay();
	}
	
	public void addOutputListener(RecordListener listener) {
		outputListeners.add(listener);
	}
	
	@Override
	public void work() {
		Record record;
		while (isAlive()) {
			record = new Record();
			if (dataSource.produceToRecord(record) < 0){
				record = null;
				setAlive(false);
			}else{
				float[] gyro;
				float[] acc;
//				if ((acc = record.getSensorReadings(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_ACCELEROMETER)) != null && 
//						(gyro = record.getSensorReadings(DataSourceTypes.TYPE_ORIENTATION_INVARIANT_GYROSCOPE)) != null){
//					System.out.println(String.format("%s,%d,%f,%f,%f,%f", record.getCurrentClass(), record.getTimestamp(), acc[0], acc[1],gyro[0], gyro[1]));
//				}
//				if ((acc = record.getSensorReadings(DataSourceTypes.TYPE_ACCELEROMETER)) != null && 
//						(gyro = record.getSensorReadings(DataSourceTypes.TYPE_GYROSCOPE)) != null){
//					System.out.println(String.format("%s,%d,%f,%f,%f,%f", record.getCurrentClass(), record.getTimestamp(), acc[0], acc[1],gyro[0], gyro[1]));
//				}
			}
			
			for (RecordListener listener : outputListeners) {
				try {
					listener.add(record);
				} catch (IllegalStateException e) {
				}
			}
			if (period > 0 && isAlive()){
				try {
					Thread.sleep(period);
				} catch (Exception e) {
					return;
				}
			}
		}
	}
	
}
