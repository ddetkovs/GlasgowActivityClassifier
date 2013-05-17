package gla.ac.uk.tests;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.chains.ClassificationChain;
import gla.ac.uk.gac.io.raw.RawFileInput;

public class ClassificationTest {

	public static void run1(){
		ChainPreferences prefs = new ChainPreferences();
		prefs.setRawInputPath("./data/training/good/").setRawInputFileName("total_with_back.data").setSegmentationWindowSize(50);

		RawFileInput input = new RawFileInput(prefs);
		ClassificationChain chain = new ClassificationChain(input, prefs);
		chain.execute();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		run1();
//		run2();
	}

}
