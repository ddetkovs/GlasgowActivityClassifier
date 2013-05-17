package gla.ac.uk.gac;

public class Utils {
	public interface TimestampProvider{
		public long provideTime();
	}
	public static TimestampProvider timestampProvider = new TimestampProvider() {
		@Override
		public long provideTime() {
			return System.nanoTime() / 1000000;
		}
	};
	
	public static long getTimestamp(){
		return getTimestampProvider().provideTime();
		
	}

	public static TimestampProvider getTimestampProvider() {
		return timestampProvider;
	}

	public static void setTimestampProvider(TimestampProvider timestampProvider) {
		Utils.timestampProvider = timestampProvider;
	}
}
