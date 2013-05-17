package gla.ac.uk.activityobserver;

import gla.ac.uk.gac.ActivityState;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class ActivityControl extends TableRow implements OnClickListener{
	
	private TextView activityName;
	private Button controlButton;
	private OnClickListener listener;
	private ActivityState currentState;
	public ActivityControl(Context context) {
		super(context);
		init();
	}
	
	public ActivityControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.activity_control, this, true);
		activityName = (TextView)findViewById(R.id.activityName);
		controlButton = (Button)findViewById(R.id.activityControlButton);
		currentState = ActivityState.Stop;
		controlButton.setOnClickListener(this);		
	}
	
	public void setActivityName(String name){
		activityName.setText(name);
	}
	
	public String getActivityName(){
		return activityName.getText().toString();
	}
	
	public void setActivityState(ActivityState state){
		currentState = state;
		switch(state){
		case Start:
			controlButton.setText(R.string.stop);
			break;
		case Stop:
			controlButton.setText(R.string.start);
			break;
		}
	}
	public ActivityState getActivityState(){
		return currentState;
	}
	public void setOnClickListener(OnClickListener l){
		listener = l;
	}
	
	@Override
	public void onClick(View v) {
		if (listener != null && v == controlButton){
			if (currentState == ActivityState.Stop){
				setActivityState(ActivityState.Start);
//				controlButton.setText(ActivityState.Stop.toString());
			}else{
				setActivityState(ActivityState.Stop);
//				controlButton.setText(ActivityState.Start.toString());
			}
			listener.onClick(this);
		}
	}
	
	public String getActivityString(){
		return currentState + " " + getActivityName();
	}
	
	public void setEnabled(boolean e){
		controlButton.setEnabled(e);
	}
	
	
}
