package hoang.tool;

public class ArrayTool {

	public int[] intElements;
	public double[] doubleElements;

	public void setIntElements(int[] elements) {
		intElements = new int[elements.length];
		for (int i = 0; i < elements.length; i++)
			intElements[i] = elements[i];
	}

	public void setDoubleElements(double[] elements) {
		doubleElements = new double[elements.length];
		for (int i = 0; i < elements.length; i++)
			doubleElements[i] = elements[i];
	}

	public void printToScreen(boolean typeFlag, boolean newlineFlag) {
		if (typeFlag) {// int type
			for (int i = 0; i < intElements.length; i++)
				System.out.print(intElements[i] + " ");
			if (newlineFlag)
				System.out.println("");
		} else {
			for (int i = 0; i < doubleElements.length; i++)
				System.out.print(doubleElements[i] + " ");
			if (newlineFlag)
				System.out.println("");
		}

	}

	public double getDoubleElementsEntropy(double[] elements) {
		setDoubleElements(elements);
		if (doubleElements == null)
			return -1;
		if (doubleElements.length == 1)
			return 0;
		double sum = 0;
		for (int i = 0; i < doubleElements.length; i++) {
			if (doubleElements[i] < 0)
				return -1;
			sum += doubleElements[i];
		}
		double entropy = 0;
		for (int i = 0; i < doubleElements.length; i++) {
			double p = doubleElements[i] / sum;
			entropy += p * Math.log(p);
		}
		return -entropy;
	}

	public double getIntElementsEntropy(int[] elements) {
		setIntElements(elements);
		// System.out.print("NE = " + intElements.length);
		if (intElements == null) {

			System.out.println("null Entropy = -1");
			System.exit(-1);
			return -1;
		}
		if (intElements.length == 1) {			
			return 0;
		}
		int sum = 0;
		for (int i = 0; i < intElements.length; i++) {
			// System.out.print(" " + intElements[i]);
			if (intElements[i] < 0) {
				System.out.println("negative Entropy = -1");
				System.exit(-1);
				return -1;
			}
			sum += intElements[i];
		}
		double entropy = 0;
		for (int i = 0; i < intElements.length; i++) {
			if (intElements[i] == 0)
				continue;
			double p = (double) intElements[i] / sum;
			entropy += p * Math.log(p);
		}
		return -entropy;
	}

	public double getIntElementsEntropy2(int[] elements) {
		setIntElements(elements);
		// System.out.print("NE = " + intElements.length);
		if (intElements == null) {
			System.out.println("null Entropy = -1");
			System.exit(-1);
			return -1;
		}
		if (intElements.length == 1) {			
			return 0;
		}
		int sum = 0;
		for (int i = 0; i < intElements.length; i++) {
			// System.out.print(" " + intElements[i]);
			if (intElements[i] < 0) {
				System.out.println("negative Entropy = -1");
				System.exit(-1);
				return -1;
			}
			sum += intElements[i];
		}
		double entropy = 0;
		for (int i = 0; i < intElements.length; i++) {
			if (intElements[i] == 0)
				continue;
			entropy += intElements[i] * Math.log(intElements[i]);
		}
		entropy = Math.log(sum) - entropy / sum;
		return entropy;
	}

	public double getAdjustedIntElementsEntropy(int[] elements,
			double currEntropy, int currSum, int adjustedIndex) {
		setIntElements(elements);
		// new entropy
		double entropy;
		int val = elements[adjustedIndex];
		if (val > 0) {
			double a = (double) currSum / (currSum + 1);
			double b = (val * Math.log(val) - (val + 1) * Math.log(val + 1))
					/ currSum;
			double c = a * Math.log(currSum);
			double d = Math.log(currSum + 1);
			entropy = a * (currEntropy + b) - c + d;
		} else {
			double a = (double) currSum / (currSum + 1);
			double b = (val + 1) * Math.log(val + 1) / currSum;
			double c = a * Math.log(currSum);
			double d = Math.log(currSum + 1);
			entropy = a * (currEntropy - b) - c + d;
		}
		return entropy;
	}
}
