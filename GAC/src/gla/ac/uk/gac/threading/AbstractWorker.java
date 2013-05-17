package gla.ac.uk.gac.threading;


public abstract class AbstractWorker implements Worker {
	protected WorkerThread workerRunnable = new WorkerThread(this);
	protected Thread workerThread;
	private boolean isAlive;
	
	public synchronized boolean isAlive() {
		return isAlive;
	}

	public synchronized void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	@Override
	public void execute() {
		workerThread = new Thread(workerRunnable);
		workerThread.start();
		setAlive(true);
	}

	@Override
	public void cancel() {
		setAlive(false);
		workerThread.interrupt();
		try {
			workerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void beforeStart() {
		
	}
	@Override
	public void onFinish() {
		
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
