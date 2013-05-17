package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.ChainPreferences;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryFileOutput extends RawFileOutput {
	public BinaryFileOutput(ChainPreferences prefs) {
		super(prefs);
	}

	@Override
	public void work(){
		RawMessage record;
		ObjectOutputStream out;
		if (file == null){
			return;
		}
		try {
			file.createNewFile();
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if (dataSources != null){
			try {
				out.writeObject(dataSources);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while(isAlive()){
			try {
				record = records.take();
				out.writeObject(record);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
