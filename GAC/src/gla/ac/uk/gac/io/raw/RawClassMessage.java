package gla.ac.uk.gac.io.raw;

import gla.ac.uk.gac.ActivityState;
import gla.ac.uk.gac.io.raw.RawMessageFactory.MessageTypes;

public class RawClassMessage extends RawMessage {
	private static final long serialVersionUID = -5262361296349176159L;
	private String className;
	private ActivityState action;
	public RawClassMessage(long timestamp, ActivityState action, String className) {
		super(MessageTypes.TYPE_CLASS, timestamp);
		this.className = className;
		this.action = action;
	}
	public RawClassMessage(String[] attrs){
		this(Long.valueOf(attrs[1]), ActivityState.valueOf(attrs[2]), attrs[3]);
	}
	public String toString(){
		return MessageTypes.TYPE_CLASS + "," + timestamp + "," + action +  ","+ className;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public ActivityState getAction() {
		return action;
	}
	public void setAction(ActivityState action) {
		this.action = action;
	}
	
}
