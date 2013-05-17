package gla.ac.uk.gac.segmentation;

import java.util.Arrays;
import java.util.HashMap;

import gla.ac.uk.gac.Record;

public class Segment {
	private Record[] segment;
	

	private HashMap<Integer, double[]> mean = new HashMap<Integer, double[]>();
	private HashMap<Integer, double[]> variance = new HashMap<Integer, double[]>();
	private HashMap<Integer, double[]> stdDeviation = new HashMap<Integer, double[]>();
	
	public Segment(Record[] segment) {
		this.segment = segment;
	}
	
	public double[] getMean(int type) {
		if (!mean.containsKey(type)){
			int count;
			float[] values;
			double[] res = null; 
			count = 0;
			for(Record r: segment){
				values = r.getSensorReadings(type);
				if (values != null){
					if (res == null){
						res = new double[values.length];
					}
					for(int i = 0; i < values.length; i++){
						res[i] += values[i];
					}
					count++;
				}
			}
			if (count == 0){
				res = new double[3];
				Arrays.fill(res, 0);
				mean.put(type, res);
				return res;
			}else if(count == 1){
				mean.put(type, res);
				return res;
			}
			
			for(int i = 0; i < res.length; i++){
				res[i] /= count;
			}
			mean.put(type, res);
			return res;
		}
		return mean.get(type);
	}
	public double[] getOrientationInvariantMean(int type, double polarRotation, double azimuthRotation){
		double[] res = getMean(type);
		res[1] -= polarRotation;
		res[2] -= azimuthRotation;
		return res;
	}
	public double[] getVariance(int type) {
		if (!variance.containsKey(type)){
			int count;
			float[] values;
			double[] mean = getMean(type);
			double[] res = new double[mean.length];
			count = 0;
			for(Record r: segment){
				values = r.getSensorReadings(type);
				if (values != null){
					for(int i = 0; i < values.length; i++){
						res[i] += Math.pow(mean[i] - values[i], 2);
					}
					count++;
				}
			}
			if (count == 0){
				Arrays.fill(res, 0);
				variance.put(type, res);
				return res;
			}else if (count == 1){
				variance.put(type, res);
				return res;
			}
			
			for(int i = 0; i < res.length; i++){
				res[i] /= count - 1;
			}
			
			variance.put(type, res);
		}
			
		return variance.get(type);
	}
	public double[] getOrientationInvariantVariance(int type, double polarRotation, double azimuthRotation){
		double[] res = getMean(type);
		res[1] -= polarRotation;
		res[2] -= azimuthRotation;
		return res;
	}
	public double[] getStdDeviation(int type) {
		if (!stdDeviation.containsKey(type)){
			double[] res = getVariance(type);
			for(int i = 0; i < res.length; i++){
				res[i] = Math.sqrt(res[i]);
			}
			stdDeviation.put(type, res);
			return res;
		}
		return stdDeviation.get(type);
	}	
	
	public String getCurrentClass(){
		if (segment[0].getCurrentClass() != null &&
				segment[segment.length - 1].getCurrentClass() != null &&
				segment[0].getCurrentClass().equals(segment[segment.length - 1].getCurrentClass())){
			return segment[0].getCurrentClass(); 
		}else{
			return null;
		}
	}
	public Record[] getSegment() {
		return segment;
	}
	
}
