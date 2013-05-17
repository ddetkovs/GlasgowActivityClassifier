package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

public class RawNullMessage extends RawMessage {
	private static final long serialVersionUID = 495956434538378342L;

	public RawNullMessage(long timestamp) {
		super(MessageTypes.TYPE_NULL_MESSAGE, timestamp);
	}
	public RawNullMessage(String [] args) {
		super(MessageTypes.TYPE_NULL_MESSAGE, Long.valueOf(args[1]));
	}
	@Override
	public String toString() {
		return MessageTypes.TYPE_CLASS + "," + timestamp;
	}

}
