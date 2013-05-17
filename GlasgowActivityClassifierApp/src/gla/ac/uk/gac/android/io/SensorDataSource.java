package gla.ac.uk.gac.android.io;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.io.AbstractDataSource;
import gla.ac.uk.gac.io.raw.RawDataListener;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.io.raw.RawRecordMessage;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorDataSource extends AbstractDataSource implements SensorEventListener{
	public static final String NAME_PREFIX = "SENSOR_";
	public final String name;
	public final int delay;
	private final float maxRange;
	
	private float[] previousEvent;
	private Sensor sensor;
	private SensorManager manager;
	public SensorDataSource(SensorManager manager, int type, int delay){
		super(type);
		this.manager = manager;
		this.delay = delay;
		name = NAME_PREFIX + type;
		sensor = manager.getDefaultSensor(type);
		maxRange = sensor.getMaximumRange();
		Log.i("lala", type + " maxRange "  + maxRange);
		Log.i("lala", type + " power "  + sensor.getPower());
	}
	@Override
	public int produceToRecord(Record r){
		float[] event = getPreviousEvent();
		if (event != null)
			r.setSensorReading(type, event);
		return 1;
	}
	@Override
	public void register(){
		if(sensor != null){
			manager.registerListener(this, sensor, delay);
		}
	}
	@Override
	public void unregister(){
		if(sensor != null){
			manager.unregisterListener(this);
		}
	}

	public synchronized float[] getPreviousEvent() {
		return previousEvent;
	}

	public synchronized void setPreviousEvent(float[] previousEvent) {
		this.previousEvent = previousEvent;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		for(float axis: event.values){
			if (Math.abs(axis) > maxRange){
				return;
			}
		}
		float[] values = new float[event.values.length];
		System.arraycopy(event.values, 0, values, 0, values.length);
		setPreviousEvent(values);
		
		if (!outputListeners.isEmpty()){
			RawMessage record = new RawRecordMessage(type, Utils.getTimestamp(), event.accuracy, values);
//			RawMessage record2 = new RawRecordMessage(type, SystemClock.elapsedRealtime(), event.accuracy, values);
			for(RawDataListener listener: outputListeners){
				listener.add(record);
//				listener.add(record2);
			}
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
