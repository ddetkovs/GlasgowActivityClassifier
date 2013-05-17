package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.threading.AbstractWorker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.PriorityBlockingQueue;

public class RawFileOutput extends AbstractWorker implements RawDataListener{
	protected PriorityBlockingQueue<RawMessage> records = new PriorityBlockingQueue<RawMessage>();
	protected RawMessage dataSources;
	protected File file;
	
	public RawFileOutput(ChainPreferences prefs){
		new File(prefs.getRawOutputPath()).mkdirs();
		file = new File(prefs.getRawOutputPath() + prefs.getRawOutputFileName());
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void add(RawMessage record) {
		records.add(record);
	}
	
	@Override
	public void work() {
		RawMessage record;
		PrintWriter out;
		
		if (file == null){
			return;
		}

		file.delete();
		try {
			file.createNewFile();
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (dataSources != null){
			out.println(dataSources);
			
		}
		while(isAlive()){
			try {
				record = records.take();
				out.println(record);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		out.close();
	}
}
