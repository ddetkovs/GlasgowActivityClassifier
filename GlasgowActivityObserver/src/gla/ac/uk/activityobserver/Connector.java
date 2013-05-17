package gla.ac.uk.activityobserver;

import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.Utils.TimestampProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;

public class Connector extends Activity {
	private SharedPreferences preferences;
	private EditText addressField; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_connect_layout);
		
		preferences = getPreferences(MODE_PRIVATE);
		addressField = (EditText)findViewById(R.id.address);
		addressField.setText(preferences.getString("address", ""));
		
		Utils.setTimestampProvider(new TimestampProvider() {
			@Override
			public long provideTime() {
				return SystemClock.elapsedRealtime();
			}
		});
	}
	public void putAddressToSettings(String address){
		Editor editor = preferences.edit();
		editor.putString("address", address);
		editor.commit();
	}
	
	public void connectObserver(View v){
		Intent observerIntent = new Intent(this, Observer.class);
		String address = addressField.getText().toString();
		putAddressToSettings(address);
		observerIntent.putExtra("address", address);
		startActivity(observerIntent);
	}
	public void connectVerifier(View v){
		Intent verifierIntent = new Intent(this, Verifier.class);
		String address = addressField.getText().toString();
		
		putAddressToSettings(address);
		verifierIntent.putExtra("address", address);
		startActivity(verifierIntent);
	}
}
