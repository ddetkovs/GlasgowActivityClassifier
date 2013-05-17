package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

import java.io.Serializable;


public abstract class RawMessage implements Comparable<RawMessage>, Serializable{
	
	private static final long serialVersionUID = 1878397909112748685L;
	protected long timestamp;
	protected MessageTypes messageType;
	
	public RawMessage(MessageTypes messageType, long timestamp){
		this.timestamp = timestamp;
		this.messageType = messageType;
	}
	public MessageTypes getRecordType() {
		return messageType;
	}
	public void setRecordType(MessageTypes messageType) {
		this.messageType = messageType;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public abstract String toString();
	
	@Override
	public int compareTo(RawMessage another) {
		return timestamp < another.getTimestamp()? -1 : timestamp == another.getTimestamp() ? 0 : 1;
	}

}
