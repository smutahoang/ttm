package hoang.tool;

public class PreditionElement {
	public String name;
	public String idString;
	public Integer idNumber;
	public double[] predictionWeight;
	public String trueClass;
	public String predictedClass;
	public Integer trueClassNumber;
	public Integer predictedClassNumber;

	public PreditionElement(String nName, String nidString, Integer nidNumber,
			double[] predWeight, String nTrueClass, String nPredictedClass,
			Integer nTrueClassNumber, Integer nPredictedClassNumber) {
		name = nName;
		idString = nidString;
		idNumber = nidNumber;
		if (predWeight != null) {
			predictionWeight = new double[predWeight.length];
			for (int i = 0; i < predWeight.length; i++) {
				predictionWeight[i] = predWeight[i];
			}
		}
		trueClass = nTrueClass;
		predictedClass = nPredictedClass;
		trueClassNumber = nTrueClassNumber;
		predictedClassNumber = nPredictedClassNumber;
	}

	public String getName() {
		return name;
	}

	public String getIDString() {
		return idString;
	}

	public int getIDNumber() {
		return idNumber;
	}

	public double getPredictionWeight(int k) {
		return predictionWeight[k];
	}
}
