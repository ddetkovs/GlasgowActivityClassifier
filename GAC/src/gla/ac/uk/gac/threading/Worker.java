package gla.ac.uk.gac.threading;

public interface Worker {
	public void execute();
	public void cancel();
	public void work();
	public void beforeStart();
	public void onFinish();
	public void addWorkerListener(WorkerListener listener);
	public void join() throws InterruptedException;
}
