package gla.ac.uk.gac.io;

import gla.ac.uk.gac.io.raw.RawDataListener;
import gla.ac.uk.gac.io.raw.RawDataProducer;

import java.util.ArrayList;

public abstract class AbstractDataSource implements RawDataProducer, DataSource{
	public final int type;
	protected ArrayList<RawDataListener> outputListeners = new ArrayList<RawDataListener>(0);
	public AbstractDataSource(){
		this.type = -1;
	}
	public AbstractDataSource(int type){
		this.type = type;
	}
	@Override
	public void addRawDataListener(RawDataListener listener) {
		outputListeners.add(listener);
	}
}
