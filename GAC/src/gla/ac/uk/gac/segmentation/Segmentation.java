package gla.ac.uk.gac.segmentation;


import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.Record;
import gla.ac.uk.gac.RecordListener;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Segmentation extends AbstractWorker implements RecordListener, SegmentProducer{
	private final int windowSize;
	private final float overlapPercentage;
	private final int overlapSize;
	private final int newMaterialSize;
	
	private List<Record> data = Collections.synchronizedList(new LinkedList<Record>());
	private ArrayList<SegmentListener> outputListeners = new ArrayList<SegmentListener>();
	private Object lock = new Object();
	private boolean noDataLeft;
	private int recordCount;
	
	public Segmentation(ChainPreferences preferences){
		windowSize = preferences.getSegmentationWindowSize();
		overlapPercentage = preferences.getSegmentationOverlapPercentage();
		overlapSize = (int)(windowSize * overlapPercentage);
		newMaterialSize = windowSize - overlapSize;
	}
	@Override
	public void add(Record r) throws IllegalStateException {
		if (r == null){
			setNoDataLeft(true);
		}else{
			data.add(r);
			incrementCount();
		}
		synchronized(lock){
			lock.notify();
		}
	}
	@Override
	public void addSegmentListener(SegmentListener listener) {
		outputListeners.add(listener);
	}
	private synchronized int getRecordCount() {
		return recordCount;
	}
	private synchronized void incrementCount(){
		this.recordCount++;
	}
	private synchronized void subtractRecordCount(int sub){
		this.recordCount -= sub;
	}
	@Override
	public void work() {
		Segment segment;
		Record[] window = new Record[windowSize];
		Record[] outputWindow = new Record[windowSize];
		while(getRecordCount() < windowSize && isAlive() && !isNoDataLeft()){
			synchronized(lock){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (!isAlive()){
			return;
		}
		if (getRecordCount() < windowSize && isNoDataLeft()){
			window = null;
			setAlive(false);
		}else{
			for(int i = 0; i < windowSize; i++){
				window[i] = data.remove(0);
			}
			subtractRecordCount(windowSize);
			System.arraycopy(window, 0, outputWindow, 0, windowSize);
		}
		
		segment = new Segment(outputWindow);
		for(int i = 0; i < outputListeners.size(); i++){
			outputListeners.get(i).add(segment);
		}
		while(isAlive()){
			if(getRecordCount() > newMaterialSize){
				System.arraycopy(window, newMaterialSize, window, 0, overlapSize);
				for(int i = overlapSize; i < windowSize; i++){
					window[i] = data.remove(0);
				}
				if(window != null){
					subtractRecordCount(newMaterialSize);
					outputWindow = new Record[windowSize];
					System.arraycopy(window, 0, outputWindow, 0, windowSize);
				}
				segment = new Segment(outputWindow);
				for(int i = 0; i < outputListeners.size(); i++){
					outputListeners.get(i).add(segment);
				}
			}else if (isNoDataLeft()){
				for(int i = 0; i < outputListeners.size(); i++){
					outputListeners.get(i).add(null);
				}
				setAlive(false);
			}
			if(!isNoDataLeft()){
				synchronized(lock){
					try {
						lock.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
