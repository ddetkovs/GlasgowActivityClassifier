package gla.ac.uk.gac.preprocessing;

import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.RecordListener;
import gla.ac.uk.gac.RecordProducer;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;


public class PreProcessor extends AbstractWorker implements RecordListener, RecordProducer{
	private LinkedBlockingQueue<Record> data = new LinkedBlockingQueue<Record>();
	private ArrayList<Filter> filters = new ArrayList<Filter>();
	private ArrayList<RecordListener> outputListeners = new ArrayList<RecordListener>();
	private boolean noDataLeft;
	public PreProcessor(Filter[] filters) {
		if (filters != null){
			this.filters.addAll(Arrays.asList(filters));
		}
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	@Override
	public void addOutputListener(RecordListener listener) {
		outputListeners.add(listener);
	}

	@Override
	public void add(Record r) throws IllegalStateException {
		if (r != null){
			data.add(r);
			
		}else{
			setNoDataLeft(true);
			workerThread.interrupt();
		}
	}
	@Override
	public void work() {
		Record record;
		while (isAlive()) {
			if (isNoDataLeft() && data.isEmpty()){
				record = null;
				setAlive(false);
			}else{
				try {
					record = data.take();
				}catch(InterruptedException e) {
					continue;
				}
				for (Filter filter : filters) {
					filter.process(record);
				}
			}

			for (RecordListener listener : outputListeners) {
				try {
					listener.add(record);
				} catch (Exception e) {
					return;
				}
			}

		}
		
	}

	public synchronized boolean isNoDataLeft() {
		return noDataLeft;
	}

	public synchronized void setNoDataLeft(boolean noDataLeft) {
		this.noDataLeft = noDataLeft;
	}
}
