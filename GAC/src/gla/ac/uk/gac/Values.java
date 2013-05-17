package gla.ac.uk.gac;

public class Values{
	private float[] values;
	public Values(float[] v){
		values = v;
	}
	public synchronized float[] getValues() {
		return values;
	}
	public synchronized void setValues(float[] values) {
		this.values = values;
	}
	public synchronized float get(int index){
		return values[index];
	}
	public int size(){
		return values.length;
	}
	public String toCSV(){
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < values.length-1; i++){
			b.append(values[i]);
			b.append(",");
		}
		b.append(values[values.length-1]);
		return b.toString();
	}
	public String toString(){
		String res = "";
		for(int i = 0; i < values.length; i++){
			res += String.format("axis=%d, value=%f; ", i, values[i]);
		}
		return res;
	}
	
}