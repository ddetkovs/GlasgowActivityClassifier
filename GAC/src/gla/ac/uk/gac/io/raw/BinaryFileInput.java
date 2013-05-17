package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.ChainPreferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class BinaryFileInput extends RawFileInput {

	
	public BinaryFileInput(ChainPreferences prefs) {
		super(prefs);
	}

	@Override
	public void work() {
		ObjectInputStream reader;
		RawMessage record;
		try {
			reader = new ObjectInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while(true){
			try{
				record = (RawMessage)reader.readObject();
			} catch (OptionalDataException e) {
				e.printStackTrace();
				continue;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

			buffer.add(record);
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		synchronized(this){
			this.notifyAll();
		}
	}
}
