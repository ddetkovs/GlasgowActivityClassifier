package gla.ac.uk.gac.io.raw;


public class RawMessageFactory {
	public enum MessageTypes{
		TYPE_CLASS,
		TYPE_RECORD,
		TYPE_CLASSIFICATION_DISTRIBUTION,
		TYPE_NULL_MESSAGE
	}
	public static RawMessage fromString(String line){
		String[] args = line.split(",");
		try{
			switch (MessageTypes.valueOf(args[0])){
			case TYPE_CLASS:
				return new RawClassMessage(args);
			case TYPE_RECORD:
				return new RawRecordMessage(args);
			case TYPE_CLASSIFICATION_DISTRIBUTION:
				return new RawClassificationGuess(args);
			case TYPE_NULL_MESSAGE:
				return new RawNullMessage(args);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			
		}catch (IllegalArgumentException e) {

		}
		
		return null;
	}
}
