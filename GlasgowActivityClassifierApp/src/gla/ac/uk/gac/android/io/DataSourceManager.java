package gla.ac.uk.gac.android.io;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.io.AbstractDataSource;
import gla.ac.uk.gac.io.DataSource;
import gla.ac.uk.gac.io.DataSourceTypes;
import gla.ac.uk.gac.io.ObserverInput;
import gla.ac.uk.gac.io.SphericalCoordinatesDataSource;
import gla.ac.uk.gac.io.raw.RawDataListener;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class DataSourceManager extends AbstractDataSource implements gla.ac.uk.gac.io.DataSourceManager{
	public static final int[] ALL_SENSOR_TYPES = {
		Sensor.TYPE_AMBIENT_TEMPERATURE, Sensor.TYPE_GRAVITY,
		Sensor.TYPE_GYROSCOPE, Sensor.TYPE_LIGHT,
		Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_MAGNETIC_FIELD,
		Sensor.TYPE_PRESSURE, Sensor.TYPE_PROXIMITY,
		Sensor.TYPE_RELATIVE_HUMIDITY, Sensor.TYPE_ROTATION_VECTOR,
		Sensor.TYPE_ACCELEROMETER
	};

	private SensorManager mSensorManager;
	private AbstractDataSource[] dataProducers;
	private final int[] sensors;
	private final int[] delays;
	public DataSourceManager(Context context, PhoneChainPreferences prefs) {
		super();
		sensors = prefs.getSensors();
		delays = prefs.getDelays();
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		dataProducers = new AbstractDataSource[sensors.length];
		for(int i = 0; i < sensors.length; i++){
			dataProducers[i] = createDataProducer(sensors[i], delays[i]);
		}
	}
	private AbstractDataSource createDataProducer(int type, int delay){
		switch(type){
			case DataSourceTypes.TYPE_MICROPHONE:
				return null;
			case DataSourceTypes.TYPE_OBSERVER:
				return new ObserverInput(DataSourceTypes.TYPE_OBSERVER);
			case DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER:
				return new SphericalCoordinatesDataSource(DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER,
						DataSourceTypes.TYPE_ACCELEROMETER);
			case DataSourceTypes.TYPE_ACCELEROMETER:
			case DataSourceTypes.TYPE_AMBIENT_TEMPERATURE:
			case DataSourceTypes.TYPE_GRAVITY:
			case DataSourceTypes.TYPE_GYROSCOPE:
			case DataSourceTypes.TYPE_LIGHT:
			case DataSourceTypes.TYPE_LINEAR_ACCELERATION:
			case DataSourceTypes.TYPE_MAGNETIC_FIELD:
			case DataSourceTypes.TYPE_PRESSURE:
			case DataSourceTypes.TYPE_PROXIMITY:
			case DataSourceTypes.TYPE_RELATIVE_HUMIDITY:
			case DataSourceTypes.TYPE_ROTATION_VECTOR:
				return new SensorDataSource(mSensorManager, type, delay);
			
		}
		return null;
	}

	@Override
	public void addRawDataListener(RawDataListener listener) {
		for(AbstractDataSource source: dataProducers){
			source.addRawDataListener(listener);
		}
		
	}
	
	@Override
	public void register() {
		for(AbstractDataSource source: dataProducers){
			source.register();
		}
	}

	@Override
	public void unregister() {
		for(AbstractDataSource source: dataProducers){
			source.unregister();
		}
	}

	@Override
	public int produceToRecord(Record r) {
		for(AbstractDataSource source: dataProducers){
			source.produceToRecord(r);
		}
		
		
		return 1;
	}
	@Override
	public void addVirtualDataSource(DataSource s) {
		
	}
}
