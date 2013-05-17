package gla.ac.uk.gac.preprocessing;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.Record;

import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.Iterator;

public class BlurFilter implements Filter {

	private class SensorBlur {
		private ArrayDeque<float[]> deque = new ArrayDeque<float[]>(size + 2);
		private int remainingCapacity = size;
		private float[] lastValue = null;
		public float[] process(float[] record) {
			if (record != null){
				lastValue = record;
			}
			deque.add(record);
			if(lastValue != null){
				if (remainingCapacity == 0){
					deque.remove();
				}else{
					remainingCapacity--;
				}
				Iterator<float[]> iterator = deque.iterator();
				
				float[] value = lastValue;
				float[] tempValue;
				float[] result = new float[deque.getFirst().length];
				int dequeSize = deque.size();
				for(int i = 0; i < size; i++ ){
					if(i < dequeSize){
						tempValue = iterator.next();
						if (tempValue != null){
							value = tempValue;
						}
					}
					
					for (int j = 0; j < value.length; j++) {
						result[j] += value[j] * convolutionKernel[i];
					}
				}
				
				return result;
			}else{
				return null;
			}
			
		}
		
	}
	
	private final float[] convolutionKernel;
	private final int size;
	private Hashtable<Integer, SensorBlur> values = new Hashtable<Integer, SensorBlur>();
	
	public BlurFilter(ChainPreferences prefs) {
		convolutionKernel = prefs.getConvolutionKernel();
		size = convolutionKernel.length;
	}

	@Override
	public void process(Record r) {
		for(Integer key: r.getTypes()){
			if (!values.containsKey(key)){
				values.put(key, new SensorBlur());
			}
			float[] blurRes = values.get(key).process(r.getSensorReadings(key));
			r.setSensorReading(key, blurRes);
		}		
	}
}