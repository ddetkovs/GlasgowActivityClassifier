package gla.ac.uk.tests;

import gla.ac.uk.gac.ActivityState;
import gla.ac.uk.gac.io.raw.RawClassMessage;
import gla.ac.uk.gac.io.raw.RawMessage;
import gla.ac.uk.gac.io.raw.RawMessageFactory;
import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;

public class RemoveUnnecessaryLines {
	public static void main(String[] args){
		String currentClass = null;
		String filename = args[0];
//		String outputFilename = args[1];
		PriorityBlockingQueue<RawMessage> buffer = new PriorityBlockingQueue<RawMessage>();
		Scanner scanner;
		RawMessage record = null;
		String line;
		try {
			scanner = new Scanner(new File(filename));
		} catch (IOException e) {
			return;
		}

		while(scanner.hasNextLine()){
			line = scanner.nextLine();
			record = RawMessageFactory.fromString(line);
			if (record != null){
				if (record.getRecordType() == MessageTypes.TYPE_CLASS){
					RawClassMessage r = ((RawClassMessage) record);
					if (r.getAction() == ActivityState.Start){
						currentClass = r.getClassName();
					}else{
						currentClass = null;
					}
					buffer.add(record);	
				}else{
					if (currentClass != null){
						buffer.add(record);
					}
				}
				
			}
		}
		for(RawMessage message: buffer){
			System.out.println(message);
		}
		
	}
}
