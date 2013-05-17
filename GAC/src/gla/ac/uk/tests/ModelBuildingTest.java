package gla.ac.uk.tests;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.chains.ModelBuildingChain;
import gla.ac.uk.gac.io.raw.RawFileInput;

public class ModelBuildingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChainPreferences prefs = new ChainPreferences().setSegmentationWindowSize(50);
		prefs.setRawInputPath("./data/training/good/").setRawInputFileName("dinja3.data");
		
		RawFileInput input = new RawFileInput(prefs);
		ModelBuildingChain chain = new ModelBuildingChain(input, prefs);
		chain.execute();
	}

}
