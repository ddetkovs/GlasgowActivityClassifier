package gla.ac.uk.gac.io;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.Utils;
import gla.ac.uk.gac.io.raw.RawClassMessage;
import gla.ac.uk.gac.io.raw.RawDataListener;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.io.raw.RawMessageFactory;
import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;
import gla.ac.uk.gac.threading.Worker;
import gla.ac.uk.gac.threading.WorkerListener;
import gla.ac.uk.gac.threading.WorkerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ObserverInput extends AbstractDataSource implements Worker {
	public static final int SERVERPORT = 8095;

	private ServerSocket serverSocket;
	private Thread workerThread;
	private String currentClass = null;
	private Socket client = null;
	private PrintWriter printer = null;
	private String line = null;
	private BufferedReader in = null;
	private WorkerThread workerRunnable = new WorkerThread(this);
	public ObserverInput(int type) {
		super(type);
	}

	public synchronized void setCurrentClass(String className) {
		currentClass = className;
	}

	public synchronized String getCurrentClass() {
		return currentClass;
	}

	@Override
	public void work() {

		RawMessage record;
		while (!serverSocket.isClosed()) {
			client = null;
			in = null;
			printer = null;

			try {
				client = serverSocket.accept();
			} catch (IOException e) {
				if (serverSocket.isClosed())
					return;
				continue;
			}

			try {
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				printer = new PrintWriter(client.getOutputStream(), true);
			} catch (IOException e) {
				close();
				continue;
			}

			try {
				while (!printer.checkError() && (line = in.readLine()) != null) {
					printer.println("received " + line);
					record = RawMessageFactory.fromString(line);
					record.setTimestamp(Utils.getTimestamp());
					if (record.getRecordType() == MessageTypes.TYPE_CLASS) {
						RawClassMessage classMessage = (RawClassMessage) record;
						switch (classMessage.getAction()) {
						case Start:
							setCurrentClass(classMessage.getClassName());
							break;
						case Stop:
							setCurrentClass(null);
							break;
						}
					}
					if (outputListeners.size() > 0) {
						for (RawDataListener listener : outputListeners) {
							listener.add(record);
						}
					}
				}
			} catch (IOException e) {
			}

			setCurrentClass(null);
			close();
		}
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
	public int produceToRecord(Record r) {
		r.setCurrentClass(getCurrentClass());
		return 1;
	}

	@Override
	public void execute() {
		try {
			serverSocket = new ServerSocket(SERVERPORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		workerThread = new Thread(workerRunnable);
		workerThread.start();
	}
	public void close(){
		try {
			if (in != null)
				in.close();
			if (printer != null) {
				printer.close();
			}
			if (client != null) {
				client.close();
			}
		} catch (IOException e1) {
		}
	}
	@Override
	public void cancel() {
		try {
			close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		workerRunnable.addWorkerListener(listener);
	}

	@Override
	public void join() throws InterruptedException {
		workerThread.join();
	}
}
