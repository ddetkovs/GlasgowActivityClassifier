package gla.ac.uk.gac.io;

import gla.ac.uk.gac.io.raw.RawDataListener;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ObserverOutput extends AbstractWorker implements RawDataListener{
	public static final int SERVERPORT = 8096;
	private LinkedBlockingQueue<RawMessage> records = new LinkedBlockingQueue<RawMessage>();
	private ServerSocket serverSocket = null;
	private boolean isConnected = false;
	private Socket clientSock = null;
	public synchronized boolean isConnected() {
		return isConnected;
	}

	public synchronized void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	@Override
	public void cancel() {
		setConnected(false);
		setAlive(false);
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.cancel();
	}

	@Override
	public void work() {
		PrintWriter out = null;
		RawMessage record;
		try {
			serverSocket = new ServerSocket(SERVERPORT);
		} catch (IOException e) {
			setAlive(false);
			return;
		}
		while(isAlive()) {
			try {
				clientSock = serverSocket.accept();
				out = new PrintWriter(clientSock.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
				try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				setAlive(false);
				return;
			}
			setConnected(true);
			
			while (!out.checkError() && isAlive()) {
				try {
					record = records.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
					out.close();
					setConnected(false);
					setAlive(false);
					return;
				}
				out.println(record.toString());
			}
			out.close();
			setConnected(false);
		}
	}

	@Override
	public void add(RawMessage record) {
		if (isConnected()){
			records.add(record);
		}
	}

}
