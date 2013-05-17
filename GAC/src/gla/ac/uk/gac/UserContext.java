package gla.ac.uk.gac;

public class UserContext {
	private double gravityMagnitude;
	private double polarRotation;
	private double azimuthRotation;
	private String currentClass;
	private double[][] rotationMatrix;
	public synchronized double getPolarRotation() {
		return polarRotation;
	}
	public synchronized void setRotation(double polarRotation, double azimuthRotation){
		this.polarRotation = polarRotation;
		this.azimuthRotation = azimuthRotation;
		setRotationMatrix(calculateRotationMatrix());
	}
	public synchronized void setPolarRotation(double polarRotation) {
		this.polarRotation = polarRotation;
	}
	public synchronized double getAzimuthRotation() {
		return azimuthRotation;
	}
	public synchronized void setAzimuthRotation(double azimuthRotation) {
		this.azimuthRotation = azimuthRotation;
	}
	public synchronized String getCurrentClass() {
		return currentClass;
	}
	public synchronized void setCurrentClass(String currentClass) {
		this.currentClass = currentClass;
	}
	public double getGravityMagnitude() {
		return gravityMagnitude;
	}
	public void setGravityMagnitude(double gravityMagnitude) {
		this.gravityMagnitude = gravityMagnitude;
	}
	public double[][] getRotationMatrix() {
		return rotationMatrix;
	}
	public void setRotationMatrix(double[][] rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}
	public double[][] calculateRotationMatrix(){
		double sinx = Math.sin(polarRotation);
		double siny = Math.sin(azimuthRotation);
		double cosx = Math.cos(polarRotation);
		double cosy = Math.cos(azimuthRotation);
		double[][] matrix = new double[][]{
			new double[]{cosy, sinx*siny, cosx*siny},
			new double[]{0, cosx, -sinx},
			new double[]{-siny, sinx*cosy, cosx*cosy}
		};
		return matrix;
	}
}
