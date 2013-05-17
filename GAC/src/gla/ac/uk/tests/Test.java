package gla.ac.uk.tests;

import gla.ac.uk.gac.ChainPreferences;
import gla.ac.uk.gac.chains.ClassificationChain;
import gla.ac.uk.gac.chains.ModelBuildingChain;
import gla.ac.uk.gac.io.raw.RawFileInput;

public class Test {
	public static void main(String[] args) {
		for(int i = 10; i < 120; i += 5){
//		int i = 95;
			System.out.println(String.format("%30d", i));
			System.out.println();
			ChainPreferences prefs = new ChainPreferences().setSegmentationWindowSize(i);
			prefs.setRawInputPath("./data/training/good/").setRawInputFileName("dinja3.data");
			
			RawFileInput input = new RawFileInput(prefs);
			ModelBuildingChain modelBuildingChain = new ModelBuildingChain(input, prefs);
			modelBuildingChain.execute();
			System.out.println("lala");
			prefs.setRawInputPath("./data/training/good/").setRawInputFileName("dinja4.data");
			input = new RawFileInput(prefs);
			ClassificationChain classificationChain = new ClassificationChain(input, prefs);
			classificationChain.execute();
		}
	}
}
