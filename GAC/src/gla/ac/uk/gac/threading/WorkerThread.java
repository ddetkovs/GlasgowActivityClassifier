package gla.ac.uk.gac.threading;

import java.util.ArrayList;

public class WorkerThread implements Runnable {
	private Worker worker;
	private ArrayList<WorkerListener> listeners = new ArrayList<WorkerListener>();
	public WorkerThread(Worker worker) {
		this.worker = worker;
	}
	public void addWorkerListener(WorkerListener listener){
		listeners.add(listener);
	}
	
	@Override
	public void run() {
		for(WorkerListener listener: listeners){
			listener.onWorkerStart(this.worker);
		}
		worker.beforeStart();
		worker.work();
		worker.onFinish();
		for(WorkerListener listener: listeners){
			listener.onWorkerFinish(this.worker);
		}
		
	}
}
