package gla.ac.uk.activityobserver;

import gla.ac.uk.gac.io.ObserverOutput;
import gla.ac.uk.gac.io.raw.RawClassificationGuess;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.io.raw.RawMessageFactory;
import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerListener;
import gla.ac.uk.gac.threading.WorkerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Verifier extends Activity implements Worker{
	public static final int PORT = ObserverOutput.SERVERPORT;
	private static final int MAX_DISPLAY = 20;
	private LinearLayout verifier_record_list;
	private String address;
	private Thread workerThread;
	private LinkedList<RawMessage> records = new LinkedList<RawMessage>();
	private Handler handler = new Handler();
	private TextView record_display;
	private Socket sock = null;
	private BufferedReader reader = null;
	private String[] activityStrings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent =  getIntent();
		address = intent.getStringExtra("address");
		setContentView(R.layout.verifier_layout);
		activityStrings = getResources().getStringArray(R.array.activity_names);
		verifier_record_list = (LinearLayout )findViewById(R.id.verifier_record_list);
		record_display = (TextView) findViewById(R.id.record_display);
	}
	@Override
	protected void onResume() {
		super.onResume();
		execute();
	}
	@Override
	protected void onPause() {
		super.onPause();
		cancel();
	}
	@Override
	public void execute() {
		workerThread = new Thread(new WorkerThread(this));
		workerThread.start();
	}

	@Override
	public void cancel() {
		if (reader != null){
			try {
				reader.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void showToast(final String text){
		final Activity that = this;
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(that, text, Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	public void addRecord(final RawMessage r){
		records.add(r);
		if (records.size() > MAX_DISPLAY){
			records.remove();
		}
		StringBuilder builder = new StringBuilder();
		for(RawMessage record: records){
			if(record.getRecordType() == MessageTypes.TYPE_CLASSIFICATION_DISTRIBUTION){
				int i = ((RawClassificationGuess) record).getMaxProbabilityNumber();
				builder.append(activityStrings[i] + "\n");
			}else{
				builder.append(record.toString() + "\n");
			}
		}
		final String displayString = builder.toString();
		handler.post(new Runnable() {
			@Override
			public void run() {
				record_display.setText(displayString);
			}
		});
	}
	@Override
	public void work() {
		
		String line = null;
		
		RawMessage record;
		try {
			sock = new Socket(address, PORT);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			line = reader.readLine();
			while(line != null){
				record = RawMessageFactory.fromString(line);
				addRecord(record);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			showToast("Disconnected");
		} finally{
			try {
				if (reader != null){
					reader.close();
					sock.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
			
	}
	@Override
	public void beforeStart() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addWorkerListener(WorkerListener listener) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void join() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

}
