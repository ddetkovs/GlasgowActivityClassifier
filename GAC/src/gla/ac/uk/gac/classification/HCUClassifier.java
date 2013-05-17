package gla.ac.uk.gac.classification;

import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;

public class HCUClassifier extends NaiveBayesUpdateable {
	private static final long serialVersionUID = 4626644899961218555L;

	public HCUClassifier() {

	}
	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		double[] distr = super.distributionForInstance(instance);
		int i = 0;
		for(double guess: distr){
			if (guess > 0.97 && guess < 1){
				System.out.println("guess " + guess + ", setting class " + i + " , class now " + instance.classValue());
//				instance.setClassValue(i);
				
//				this.updateClassifier(instance);
				break;
			}
			i++;
		}
		return distr;
	}

}
