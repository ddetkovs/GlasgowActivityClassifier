package gla.ac.uk.gac.android.chains;

import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.chains.AbstractChain;
import gla.ac.uk.gac.io.AbstractDataSource;
import gla.ac.uk.gac.io.DataSourceManager;
import gla.ac.uk.gac.io.raw.RawFileOutput;
import gla.ac.uk.gac.io.raw.RawMessage;

public class DataGatheringChain extends AbstractChain {
	private RawFileOutput fileOutput;

	public DataGatheringChain(DataSourceManager source, PhoneChainPreferences prefs) {
		super(source, prefs);
		fileOutput = new RawFileOutput(prefs);
	}
	@Override
	public void beforeStart() {
		super.beforeStart();
		((AbstractDataSource)dataSource).addRawDataListener(fileOutput);
		links.add(fileOutput);
	}
	public void writeToRawOutput(RawMessage message){
		fileOutput.add(message);
	}
}
