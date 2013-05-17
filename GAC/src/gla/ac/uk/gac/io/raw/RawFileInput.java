package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.io.DataSource;
import gla.ac.uk.gac.io.DataSourceManager;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.io.SphericalCoordinatesDataSource;
import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;


public class RawFileInput extends AbstractWorker implements DataSourceManager{
	private static HashMap<Integer, Float> sourceMaxRange = new HashMap<Integer, Float>();
	static{
		sourceMaxRange.put(9, 9.80665f);
		sourceMaxRange.put(4, 8.726646f);
		sourceMaxRange.put(10, 8.726646f);
		sourceMaxRange.put(2, 2000.0f);
		sourceMaxRange.put(6, 1260.0f);
		sourceMaxRange.put(11, 8.726646f);
		sourceMaxRange.put(1, 19.6133f);
	}
	private static HashMap<Integer, Float> sourceMinValue = new HashMap<Integer, Float>();
	static{
		sourceMinValue.put(6, 900.0f);
	}
	
	protected ArrayList<RawDataListener> outputListeners = new ArrayList<RawDataListener>();
	protected Record tempRecord = new Record(0);
	protected File file;
	protected int delay;
	protected Thread workerThread;
	protected PriorityBlockingQueue<RawMessage> buffer = new PriorityBlockingQueue<RawMessage>();
	protected ArrayList<DataSource> virtualSources = new ArrayList<DataSource>();
	
//	{
//			new SphericalCoordinatesDataSource(DataSourceTypes.TYPE_SPHERICAL_ACCELERATION, DataSourceTypes.TYPE_ACCELEROMETER),
//			new SphericalCoordinatesDataSource(DataSourceTypes.TYPE_SPHERICAL_GYROSCOPE, DataSourceTypes.TYPE_GYROSCOPE),
//			};
//	
	public RawFileInput(ChainPreferences prefs){
		delay = prefs.getSamplingDelayMs();
		file = new File(prefs.getRawInputPath() + prefs.getRawInputFileName());
		
	}
	public void setFile(File file) {
		this.file = file;
	}
	protected long getPreviousTimestamp() {
		return tempRecord.getTimestamp();
	}
	protected void setPreviousTimestamp(long previousTimestamp) {
		tempRecord.setTimestamp(previousTimestamp);
	}
	protected String getCurrentClass() {
		return tempRecord.getCurrentClass();
	}
	protected void setCurrentClass(String currentClass) {
		tempRecord.setCurrentClass(currentClass);
	}
	protected void setSensorReading(int type, float[] values){
		tempRecord.setSensorReading(type, values);
	}
	protected float[] getSensorReading(int type){
		return tempRecord.getSensorReadings(type);
	}
	protected void setDataSources(int[] dataSources){
		for(int type: dataSources){
			if (!tempRecord.hasSensor(type)){
				tempRecord.setSensorReading(type, null);
			}
		}
	}
	protected void copyTo(Record r){
		tempRecord.copyTo(r);
	}
	protected void convertRaw(RawMessage rawRecord){
		switch(rawRecord.getRecordType()){
		case TYPE_CLASS:
			RawClassMessage classMessage = (RawClassMessage) rawRecord;
			
			switch(classMessage.getAction()){
			case Start:
				setCurrentClass(classMessage.getClassName());
				break;
			case Stop:
				setCurrentClass(null);
				break;
			}
			return;
		case TYPE_RECORD:
			RawRecordMessage recordMessage = (RawRecordMessage) rawRecord;
			float[] vals = recordMessage.getValues();
			float maxRange = sourceMaxRange.get(recordMessage.getType());
			
			
			for(float val: vals){
				if (Math.abs(val) > maxRange){
					return;
				}
			}
			if (sourceMinValue.containsKey(recordMessage.getType())){
				float minValue = sourceMinValue.get(recordMessage.getType());
				if (recordMessage.getType() == DataSourceTypes.TYPE_PRESSURE){
					if (sourceMinValue.get(recordMessage.getType()) < minValue){
						return;
					}
				}
			}
			
			setSensorReading(recordMessage.getType(), vals);
			return;
		}

	}
	@Override
	public void work() {
		Scanner scanner;
		RawMessage record = null;
		String line;
		try {
			scanner = new Scanner(file);
		} catch (IOException e) {
			e.printStackTrace();
			setAlive(false);
			synchronized(this){
				this.notifyAll();
			}
			return;
		}

		while(scanner.hasNextLine() && isAlive()){
			line = scanner.nextLine();
			record = RawMessageFactory.fromString(line);
			if (record != null){
				buffer.add(record);
			}
		}
		setAlive(false);
		if (record == null){
			buffer.add(new RawNullMessage(Utils.getTimestamp()));
		}else{
			buffer.add(new RawNullMessage(record.getTimestamp()+1));
		}
		
		synchronized(this){
			this.notifyAll();
		}
	}
	@Override
	public int produceToRecord(Record r) {
		RawMessage rawRecord = null;
		if (buffer.size() > 0 || isAlive()){
			try {
				rawRecord = buffer.take();
				while(rawRecord.getRecordType() != MessageTypes.TYPE_NULL_MESSAGE && 
						rawRecord.getTimestamp() - getPreviousTimestamp() < delay &&
						(buffer.size() > 0 || isAlive())){

					convertRaw(rawRecord);
					rawRecord = buffer.take();
				}
				copyTo(r);
				convertRaw(rawRecord);
				setPreviousTimestamp(rawRecord.getTimestamp());
				
				for(DataSource virtual: virtualSources){
					virtual.produceToRecord(r);
				}
				return 1;
			} catch (InterruptedException e) {
			}
		} 
		return -1;
	}

	@Override
	public void register() {
		execute();
	}
	@Override
	public void unregister() {
		cancel();
	}
	@Override
	public void addVirtualDataSource(DataSource s) {
		virtualSources.add(s);
	}

}
