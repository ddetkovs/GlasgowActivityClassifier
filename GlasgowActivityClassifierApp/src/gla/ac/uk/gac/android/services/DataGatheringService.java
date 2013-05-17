package gla.ac.uk.gac.android.services;

import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.android.chains.DataGatheringChain;
import gla.ac.uk.gac.android.io.DataSourceManager;
import gla.ac.uk.gac.io.raw.RawMessage;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DataGatheringService extends AbstractService {
	private PhoneChainPreferences preferences;
	public class LocalBinder extends Binder {
        public DataGatheringService getService() {
            return DataGatheringService.this;
        }
    }
	public void setChain(DataGatheringChain chain) {
		super.setChain(chain);
	}
	public DataGatheringChain getChain() {
		return (DataGatheringChain)chain;
	}
	private final LocalBinder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public void startChain(){
		DataSourceManager dataSourceManager = new DataSourceManager(this, preferences);
		DataGatheringChain dataGatheringChain = new DataGatheringChain(dataSourceManager, preferences);
		setChain(dataGatheringChain);
		super.startChain();
	}
	
	public void setDataSourceManagerSettings(PhoneChainPreferences prefs){
		preferences = prefs;
	}
	public void writeToRawOutput(RawMessage message){
		getChain().writeToRawOutput(message);
	}

}
