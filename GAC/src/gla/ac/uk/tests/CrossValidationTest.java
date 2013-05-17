package gla.ac.uk.tests;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.chains.CrossValidationChain;
import gla.ac.uk.gac.io.raw.RawFileInput;

public class CrossValidationTest {
	public static void findWindowSize(){
		ChainPreferences prefs = new ChainPreferences();
		prefs.setRawInputPath("./data/training/good/").setRawInputFileName("total_with_dimon.data");
		int currentBestSize = 0;
		double currentBestPrecision = 0;
		int currentBestNumberOfRecords = 0;
		double nextPrecision = 0;
		int downHillCounter = 0;
		int maxDownHill = 7;
		for(int i = 100; i < 2050; i += 50){
			prefs.setSegmentationWindowSizeMs(i);
			RawFileInput input = new RawFileInput(prefs);
			CrossValidationChain crossValidationChain = new CrossValidationChain(input, prefs);
			crossValidationChain.execute();
			nextPrecision = crossValidationChain.getPrecision();
			System.out.println(i + "," + nextPrecision);
			if (currentBestPrecision < nextPrecision){
				currentBestPrecision = nextPrecision;
				currentBestNumberOfRecords = prefs.getSegmentationWindowSize();
				currentBestSize = i;
				downHillCounter = 0;
			}else{
				downHillCounter++;
				if (downHillCounter > maxDownHill){
//					break;
				}
			}
			
		}
		
		System.out.println("BEST SIZE " + currentBestSize);
		System.out.println("BEST SIZE " + currentBestNumberOfRecords);
		System.out.println("BEST PRECISION " + currentBestPrecision);
		
		
	}
	
	public static void test(){
		ChainPreferences prefs = new ChainPreferences();
		prefs.setRawInputPath("./data/training/good/").setRawInputFileName("total_with_dimon.data").setSegmentationWindowSizeMs(1500);
		
		RawFileInput input = new RawFileInput(prefs);
		CrossValidationChain crossValidationChain = new CrossValidationChain(input, prefs);
		crossValidationChain.execute();
		crossValidationChain.printEvaluationString();
		crossValidationChain.saveInstances("./data/");

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
		
	}

}
