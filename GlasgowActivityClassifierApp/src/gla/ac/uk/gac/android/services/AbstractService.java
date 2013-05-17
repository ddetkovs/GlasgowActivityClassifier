package gla.ac.uk.gac.android.services;

import gla.ac.uk.gac.chains.AbstractChain;
import android.app.Service;

public abstract class AbstractService extends Service {
	
	protected AbstractChain chain;
	public AbstractChain getChain() {
		return chain;
	}
	public void setChain(AbstractChain chain) {
		if(this.chain != null && this.chain.isRunning()){
			this.chain.cancel();
		}
		this.chain = chain;
	}
	public void startChain(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				chain.execute();
			}
		});
		
	}
	public void stopChain(){
		chain.cancel();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(this.chain != null && this.chain.isRunning()){
			chain.cancel();
		}
	}
	public boolean isRunning() {
		return chain != null && chain.isRunning();
	}
}
