package gla.ac.uk.gac;

import java.util.HashMap;

public class ChainPreferences {
	private int samplingFrequency = 50;
	private int samplingDelayMs = 1000 / 50;
	private int gathererSamplingDelay = 0;
	private int segmentationWindowSizeMs = 1000;
	private int segmentationWindowSize = getSegmentationWindowSizeMs() / samplingDelayMs;
	private float segmentationOverlapPercentage = 0.7f;
	private String modelInputPath = "NaiveBayes.model";
	private String modelOutputPath = "NaiveBayes.model";
	private String rawInputFileName = "";
	private String rawOutputFileName = "";
	private String rawInputPath = "";
	private String rawOutputPath = "";

	private String[] simpleClasses = {"moving", "nonmoving", "not_in_pocket"};
	private HashMap<String, String[]> classTypes = new HashMap<String, String[]>();
	
	public ChainPreferences() {
		getClassTypes().put("moving", new String[]{
			"slow_walking", "fast_walking",
			"slow_running", "fast_running",
//			"walking", "running",
			"upstairs",	"downstairs" 
		});
		getClassTypes().put("nonmoving", new String[]{
			"sitting", "standing", "lift"
		});
	}
	
/*	private String[] classList = { "sitting", "standing", 
			
//			"walking",
//			"running",
			
			
			};

	*/
	
	private float[] convolutionKernel = { 0.0020628583751271068f,
			 0.04033059461420685f, 0.24006771153151615f, 0.43507767095829963f,
			 0.24006771153151615f, 0.04033059461420685f, 0.0020628583751271068f }; 
		
//		{ 0.016004083921703945f,
//			0.05139344326792439f, 0.11825507390946054f, 0.1949696557227417f,
//			0.23032943298089034f, 0.1949696557227417f, 0.11825507390946054f,
//			0.05139344326792439f, 0.016004083921703945f };

	 
/*
	public String[] getClassList() {
		return classList;
	}

	public ChainPreferences setClassList(String[] classList) {
		this.classList = classList;
		return this;
	}
*/
	public String getModelInputPath() {
		return modelInputPath;
	}

	public ChainPreferences setModelInputPath(String modelPath) {
		this.modelInputPath = modelPath;
		return this;
	}

	public String getModelOutputPath() {
		return modelOutputPath;
	}

	public ChainPreferences setModelOutputPath(String modelPath) {
		this.modelOutputPath = modelPath;
		return this;
	}

	public int getGathererSamplingDelay() {
		return gathererSamplingDelay;
	}

	public ChainPreferences setGathererSamplingDelay(int gathererSamplingDelay) {
		this.gathererSamplingDelay = gathererSamplingDelay;
		return this;
	}

	public int getSamplingFrequency() {
		return samplingFrequency;
	}

	public ChainPreferences setSamplingFrequency(int samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
		samplingDelayMs = 1000 / samplingFrequency;
		segmentationWindowSize = segmentationWindowSizeMs / samplingDelayMs; 
		return this;
	}

	public int getSamplingDelayMs() {
		return samplingDelayMs;
	}

	public ChainPreferences setSamplingDelayMs(int samplingDelayMs) {
		this.samplingDelayMs = samplingDelayMs;
		samplingFrequency = 1000 / this.samplingDelayMs;
		return this;
	}

	public float[] getConvolutionKernel() {
		return convolutionKernel;
	}

	public ChainPreferences setConvolutionKernel(float[] convolutionKernel) {
		this.convolutionKernel = convolutionKernel;
		return this;
	}


	public float getSegmentationOverlapPercentage() {
		return segmentationOverlapPercentage;
	}

	public ChainPreferences setSegmentationOverlapPercentage(
			float segmentationOverlapPercentage) {
		this.segmentationOverlapPercentage = segmentationOverlapPercentage;
		return this;
	}

	public String getRawInputFileName() {
		return rawInputFileName;
	}

	public ChainPreferences setRawInputFileName(String rawInputPath) {
		this.rawInputFileName = rawInputPath;
		return this;
	}

	public String getRawOutputFileName() {
		return rawOutputFileName;
	}

	public ChainPreferences setRawOutputFileName(String rawOutputPath) {
		this.rawOutputFileName = rawOutputPath;
		return this;
	}

	public String getRawInputPath() {
		return rawInputPath;
	}

	public ChainPreferences setRawInputPath(String rawInputPath) {
		this.rawInputPath = rawInputPath;
		return this;
	}

	public String getRawOutputPath() {
		return rawOutputPath;
	}

	public ChainPreferences setRawOutputPath(String rawOutputPath) {
		this.rawOutputPath = rawOutputPath;
		return this;
	}

	public int getSegmentationWindowSizeMs() {
		return segmentationWindowSizeMs;
	}

	public ChainPreferences setSegmentationWindowSizeMs(int segmentationWindowSizeMs) {
		this.segmentationWindowSizeMs = segmentationWindowSizeMs;
		segmentationWindowSize = segmentationWindowSizeMs / samplingDelayMs;
		return this;
	}

	public int getSegmentationWindowSize() {
		return segmentationWindowSize;
	}

	public ChainPreferences setSegmentationWindowSize(int segmentationWindowSize) {
		this.segmentationWindowSize = segmentationWindowSize;
		segmentationWindowSizeMs = samplingDelayMs * segmentationWindowSize;
		return this;
	}

	public String[] getSimpleClasses() {
		return simpleClasses;
	}

	public void setSimpleClasses(String[] simpleClasses) {
		this.simpleClasses = simpleClasses;
	}

	public HashMap<String, String[]> getClassTypes() {
		return classTypes;
	}

	public void setClassTypes(HashMap<String, String[]> classTypes) {
		this.classTypes = classTypes;
	}
	public String[] getClassType(String type) {
		return classTypes.get(type);
	}
}
