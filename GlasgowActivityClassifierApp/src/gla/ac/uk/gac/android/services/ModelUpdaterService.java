/*package gla.ac.uk.gac.android.services;

import gla.ac.uk.gac.android.PhoneChainPreferences;
import gla.ac.uk.gac.android.R;
import gla.ac.uk.gac.android.chains.DataGatheringChain;
import gla.ac.uk.gac.android.chains.ModelUpdaterChain;
import gla.ac.uk.gac.android.io.DataSourceManager;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Binder;
import android.os.IBinder;

public class ModelUpdaterService extends AbstractService {

	private PhoneChainPreferences preferences;
	public class LocalBinder extends Binder {
        public ModelUpdaterService getService() {
            return ModelUpdaterService.this;
        }
    }
	public void setChain(ModelUpdaterChain chain) {
		super.setChain(chain);
	}
	public ModelUpdaterChain getChain() {
		return (ModelUpdaterChain)chain;
	}
	private final LocalBinder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public void startChain(){
		ModelUpdaterChain modelUpdaterChain;
		DataSourceManager dataSourceManager = new DataSourceManager(this, preferences);
		
		try {
			modelUpdaterChain  = new ModelUpdaterChain(dataSourceManager, preferences);
		} catch (NotFoundException e) {
			return;
		} catch (Exception e) {
			return;
		}
		setChain(modelUpdaterChain);
		super.startChain();
	}
	public void setDataSourceManagerSettings(PhoneChainPreferences prefs){
		preferences = prefs;
	}
}
*/