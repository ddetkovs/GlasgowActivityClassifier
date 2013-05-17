package gla.ac.uk.gac.threading;

public interface WorkerListener {
	public void onWorkerFinish(Worker w);
	public void onWorkerStart(Worker w);
}
