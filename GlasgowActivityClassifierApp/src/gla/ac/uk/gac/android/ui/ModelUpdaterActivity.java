/*package gla.ac.uk.gac.android.ui;

import java.io.File;

import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.android.R;
import gla.ac.uk.gac.android.services.ModelUpdaterService;
import gla.ac.uk.gac.android.services.ModelUpdaterService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;

public class ModelUpdaterActivity extends Activity implements ServiceConnection{
	private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "GAC" + File.separator;
	private ModelUpdaterService classificationService;
	private Button stopButton;
	private WakeLock mWakeLock;
    private WifiLock mWifiLock;
	private PhoneChainPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gathering_activity);
		stopButton = (Button)findViewById(R.id.stopButton);
		Intent serviceIntent = new Intent(this, ModelUpdaterService.class);
		startService(serviceIntent);
		bindService(serviceIntent, this, 0);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        
        final WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL , "MyWifiLock");
        if(!mWifiLock.isHeld()){
        	mWifiLock.acquire();
        }
        preferences = new PhoneChainPreferences();
        preferences
        	.setModelInputPath(filePath + "model" + File.separator)
        	.setModelOutputPath(filePath + "model" + File.separator)
        	.setGathererSamplingDelay(preferences.getSamplingDelayMs());
        

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
		mWifiLock.release();
	};
	@Override
	public void onBackPressed() {
		if (!classificationService.isRunning()){
			super.onBackPressed();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		classificationService = null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LocalBinder binder = (LocalBinder)service;
		classificationService = binder.getService();
		if (!classificationService.isRunning()){
			classificationService.setDataSourceManagerSettings(preferences);
			classificationService.startChain();
		}
	}

	public void stopButtonClick(View v){
		if(classificationService.isRunning()){
			classificationService.stopChain();
			stopButton.setText(R.string.start);
		}else{
			classificationService.startChain();
			stopButton.setText(R.string.stop);
		}
	}
}
*/