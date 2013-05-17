package gla.ac.uk.gac.io;

import gla.ac.uk.gac.Record;

public interface DataSource  {
	public void register();
	public void unregister();
	public int produceToRecord(Record r); 
}
