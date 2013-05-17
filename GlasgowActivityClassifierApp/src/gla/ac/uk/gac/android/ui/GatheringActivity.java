package gla.ac.uk.gac.android.ui;

import gla.ac.uk.gac.ActivityState;
import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.android.R;
import gla.ac.uk.gac.android.services.DataGatheringService;
import gla.ac.uk.gac.android.services.DataGatheringService.LocalBinder;
import gla.ac.uk.gac.io.raw.RawClassMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;

public class GatheringActivity extends Activity implements ServiceConnection, OnClickListener{
	private final String filePath = Environment.getExternalStorageDirectory() + File.separator + "GAC" + File.separator;
	private DataGatheringService classificationService;
	private TableLayout activityList;
	private Button stopButton;
	private String[] activityStrings;
	private ActivityControl[] activityControls;
	private WakeLock mWakeLock;
	private PhoneChainPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gathering_activity);
		preferences = new PhoneChainPreferences();
        preferences.setRawOutputPath(filePath);
		ArrayList<String> classTypes = new ArrayList<String>();
        for(String c:preferences.getSimpleClasses()){
        	if (preferences.getClassType(c)!=null)
        		classTypes.addAll(Arrays.asList(preferences.getClassType(c)));
		}
        
        activityStrings = classTypes.toArray(new String[0]);//getResources().getStringArray(R.array.activity_names);
		activityControls = new ActivityControl[activityStrings.length];
		stopButton = (Button)findViewById(R.id.stopButton);
		activityList = (TableLayout) this.findViewById(R.id.activityList);
		for(int i = 0; i < activityStrings.length; i++){
			activityControls[i] = new ActivityControl(this);
			activityControls[i].setActivityName(activityStrings[i]);
			activityList.addView(activityControls[i]);
			activityControls[i].setOnClickListener(this);
		}
		Intent serviceIntent = new Intent(this, DataGatheringService.class);
		startService(serviceIntent);
		bindService(serviceIntent, this, 0);
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        
        
		
			

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
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
			preferences.setRawOutputFileName(System.currentTimeMillis()/1000 +".data");
			classificationService.setDataSourceManagerSettings(preferences);
			classificationService.startChain();
		}
		setControlsEnabled(true);

	}

	public void setControlsEnabled(boolean b){
		if (b){
			for(int i = 0; i < activityStrings.length; i++){
				activityControls[i].setEnabled(b);
			}
		}else{
			for(int i = 0; i < activityStrings.length; i++){
				activityControls[i].setEnabled(b);
				activityControls[i].setActivityState(ActivityState.Stop);
			}
		}


	}
	@Override
	public void onClick(View v) {
		ActivityControl currentControl = (ActivityControl) v;
		if (currentControl.getActivityState() == ActivityState.Start){
			for(int i = 0; i < activityStrings.length; i++){
				if (activityControls[i] != currentControl){
					activityControls[i].setEnabled(false);
				}
			}
			classificationService.writeToRawOutput(new RawClassMessage(Utils.getTimestamp() + preferences.getGatheringUserErrorTime(),
					currentControl.getActivityState(),
					currentControl.getActivityName()));
					
		} else {
			for(int i = 0; i < activityStrings.length; i++){
				if (activityControls[i] != currentControl){
					activityControls[i].setEnabled(true);
				}
			}
			classificationService.writeToRawOutput(new RawClassMessage(Utils.getTimestamp() - preferences.getGatheringUserErrorTime(),
					currentControl.getActivityState(),
					currentControl.getActivityName()));
		}


	}
	public void stopButtonClick(View v){
		if(classificationService.isRunning()){
			classificationService.stopChain();
			stopButton.setText(R.string.start);
			setControlsEnabled(false);
		}else{
			classificationService.startChain();
			stopButton.setText(R.string.stop);
			setControlsEnabled(true);
		}
	}
}
