package gla.ac.uk.activityobserver;

import gla.ac.uk.gac.ActivityState;
import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.io.ObserverInput;
import gla.ac.uk.gac.io.raw.RawClassMessage;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerListener;
import gla.ac.uk.gac.threading.WorkerThread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.Toast;

public class Observer extends Activity implements Worker, OnClickListener {
	private static final int PORT = ObserverInput.SERVERPORT;

	private Handler handler = new Handler();
	private SharedPreferences preferences;
	private Thread workerThread;
	private String address;
	private String[] activityStrings;
	private ActivityControl[] activityControls;
	private TableLayout activityList;
	private LinkedBlockingQueue<RawMessage> records = new LinkedBlockingQueue<RawMessage>();
	private Socket sock = null;
	private PrintWriter printer = null;
	private BufferedReader in = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		address = intent.getStringExtra("address");
		setContentView(R.layout.gathering_activity);

		activityStrings = getResources().getStringArray(R.array.activity_names);
		activityControls = new ActivityControl[activityStrings.length];
		activityList = (TableLayout) this.findViewById(R.id.activityList);
		for (int i = 0; i < activityStrings.length; i++) {
			activityControls[i] = new ActivityControl(this);
			activityControls[i].setActivityName(activityStrings[i]);
			activityList.addView(activityControls[i]);
			activityControls[i].setOnClickListener(this);
			activityControls[i].setEnabled(true);
		}
		
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
		close();
		workerThread.interrupt();
		try {
			workerThread.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showToast(final String text) {
		final Activity that = this;
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(that, text, Toast.LENGTH_SHORT).show();
			}
		});

	}

	public void close() {
		try {
			if (in != null)
				in.close();
			if (printer != null) {
				printer.close();
			}
			if (sock != null) {
				sock.close();
			}
		} catch (IOException e2) {
		}
	}

	@Override
	public void work() {
		RawMessage record;

		final Context that = this;
		try {
			sock = new Socket(address, PORT);
		} catch (UnknownHostException e1) {
			showToast("UNKNOWN HOST");
			finish();
			return;
		} catch (IOException e1) {
			showToast("DISCONNECTED");
			finish();
			return;
		}

		try {
			printer = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
		} catch (IOException e1) {
			close();
			finish();
			return;

		}

		while (!printer.checkError()) {
			try {
				record = records.take();
			} catch (InterruptedException e) {
				close();
				finish();
				return;
			}
			printer.println(record.toString());

			
			try {
				final String line = in.readLine();
				if (line != null){
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(that, line, Toast.LENGTH_SHORT).show();
						}
					});
				}else{
					close();
					finish();
					return;
				}
			} catch (IOException e) {
				close();
				finish();
				return;
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		ActivityControl control = (ActivityControl) v;
		if (control.getActivityState() == ActivityState.Start) {
			for (int i = 0; i < activityStrings.length; i++) {
				if (activityControls[i] != control) {
					activityControls[i].setEnabled(false);
				}
			}
		} else {
			for (int i = 0; i < activityStrings.length; i++) {
				if (activityControls[i] != control) {
					activityControls[i].setEnabled(true);
				}
			}
		}
		records.add(new RawClassMessage(Utils.getTimestamp(), control
				.getActivityState(), control.getActivityName()));
	}

	public void stopButtonClick(View v) {
		cancel();
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
