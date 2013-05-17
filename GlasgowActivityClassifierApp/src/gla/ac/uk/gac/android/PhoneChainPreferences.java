package gla.ac.uk.gac.android;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.io.DataSourceTypes;
import android.hardware.SensorManager;

public class PhoneChainPreferences extends ChainPreferences {
	private int gatheringUserErrorTime = 3000; //3 sec
	private int[] sensors = { DataSourceTypes.TYPE_GRAVITY,
			DataSourceTypes.TYPE_GYROSCOPE,
			DataSourceTypes.TYPE_LINEAR_ACCELERATION,
			DataSourceTypes.TYPE_MAGNETIC_FIELD, DataSourceTypes.TYPE_PRESSURE,
			DataSourceTypes.TYPE_ROTATION_VECTOR,
			DataSourceTypes.TYPE_ACCELEROMETER,
			DataSourceTypes.TYPE_OBSERVER,
//			DataSourceTypes.TYPE_SPHERICAL_ACCELEROMETER,
			DataSourceTypes.TYPE_LIGHT};

	private int[] delays = { SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_FASTEST,
			0,
//			0,
			SensorManager.SENSOR_DELAY_FASTEST};

	public int[] getSensors() {
		return sensors;
	}

	public PhoneChainPreferences setSensors(int[] sensors) {
		this.sensors = sensors;
		return this;
	}

	public int[] getDelays() {
		return delays;
	}

	public PhoneChainPreferences setDelays(int[] delays) {
		this.delays = delays;
		return this;
	}

	public int getGatheringUserErrorTime() {
		return gatheringUserErrorTime;
	}

	public PhoneChainPreferences setGatheringUserErrorTime(int gatheringUserErrorTime) {
		this.gatheringUserErrorTime = gatheringUserErrorTime;
		return this;
	}

}
