package gla.ac.uk.gac.chains;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.features.AbstractFeature;
import gla.ac.uk.gac.features.FeatureExtractor;
import gla.ac.uk.gac.io.DataSourceManager;
import gla.ac.uk.gac.threading.AbstractWorker;
import gla.ac.uk.gac.threading.Worker;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;

public abstract class AbstractChain extends AbstractWorker{
	protected ArrayList<Worker> links = new ArrayList<Worker>();
	protected boolean isRunning = false;
	protected DataSourceManager dataSource;
	protected ChainPreferences preferences;
	protected OnCompleteListener completeListener;
	public AbstractChain(DataSourceManager dataSource, ChainPreferences prefs){
		this.dataSource = dataSource;
		this.setPreferences(prefs);
	}
	
	public void addLink(Worker link){
		links.add(link);
	}
	@Override
	public void execute() {
		workerRunnable.run();
	}
	@Override
	public void cancel() {
		dataSource.unregister();
		for(int i = links.size() - 1; i >= 0; i--){
			links.get(i).cancel();
		}
		isRunning = false;
	}
	@Override
	public void work() {
		dataSource.register();
		for(Worker link: links){
			link.execute();
		}
		isRunning = true;
	};
	public boolean isRunning() {
		return isRunning;
	}
	
	@Override
	public void beforeStart() {
		super.beforeStart();
		links = new ArrayList<Worker>();
	}
	@Override
	public void onFinish() {
		for(Worker w: links){
			try {
				w.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (completeListener != null){
			completeListener.onComplete();
		}
	}; 
	
	public static Instances createInstancesTemplate(AbstractFeature[] features, ArrayList<String> classList){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		for(AbstractFeature feature: features){
			for(Attribute attribute: feature.getAttributes()){
				attributes.add(attribute);
			}
		}
		Attribute classAttribute = new Attribute(FeatureExtractor.CLASS_ATTRIBUTE_NAME, classList);
		attributes.add(classAttribute);
		Instances instancesTemplate = new Instances(FeatureExtractor.INSTANCES_RELATION_NAME, attributes, 0);
		instancesTemplate.setClass(instancesTemplate.attribute(FeatureExtractor.CLASS_ATTRIBUTE_NAME));
		
		return instancesTemplate;
	}
	
	public ChainPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(ChainPreferences preferences) {
		this.preferences = preferences;
	}
	
	public void setOnCompleteListener(OnCompleteListener listener){
		completeListener = listener;
	}
}
