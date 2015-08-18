package hoang.tool;

import java.util.Arrays;
import java.util.Comparator;

public class RankingTool {
	static private Comparator<WeightedElement> ascName;
	static private Comparator<WeightedElement> descWeight;
	static {
		ascName = new Comparator<WeightedElement>() {
			@Override
			public int compare(WeightedElement e1, WeightedElement e2) {
				return e1.getName().compareTo(e2.getName());
			}
		};

		descWeight = new Comparator<WeightedElement>() {
			@Override
			public int compare(WeightedElement e1, WeightedElement e2) {
				if (e2.getWeight() - e1.getWeight() > 0)
					return 1;
				else if (e2.getWeight() - e1.getWeight() < 0)
					return -1;
				else
					return 0;
			}
		};
	}
	public WeightedElement[] elements;

	public void setElements(String[] names, double[] weights) {
		elements = new WeightedElement[names.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new WeightedElement(names[i], weights[i]);
		}
	}
	public void setElements(String[] names, int[] weights) {
		elements = new WeightedElement[names.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new WeightedElement(names[i], weights[i]);
		}
	}

	public WeightedElement[] getTopKbyWeight(String[] names, double[] weights,
			int k) {
		setElements(names, weights);
		Arrays.sort(elements, descWeight);

		WeightedElement[] topKbyWeight = new WeightedElement[k];
		for (int i = 0; i < k; i++)
			topKbyWeight[i] = elements[i];
		return topKbyWeight;
	}
	
	public WeightedElement[] getTopKbyWeight(String[] names, int[] weights,
			int k) {
		setElements(names, weights);
		Arrays.sort(elements, descWeight);

		WeightedElement[] topKbyWeight = new WeightedElement[k];
		for (int i = 0; i < k; i++)
			topKbyWeight[i] = elements[i];
		return topKbyWeight;
	}

	public WeightedElement[] getTopKbyName(String[] names, double[] weights,
			int k) {
		setElements(names, weights);
		Arrays.sort(elements, ascName);

		WeightedElement[] topKbyName = new WeightedElement[k];
		for (int i = 0; i < k; i++)
			topKbyName[i] = elements[i];
		return topKbyName;
	}

	public WeightedElement[] getTopKbyName(String[] names, int[] weights, int k) {
		setElements(names, weights);
		Arrays.sort(elements, ascName);

		WeightedElement[] topKbyName = new WeightedElement[k];
		for (int i = 0; i < k; i++)
			topKbyName[i] = elements[i];
		return topKbyName;
	}

	public int[] getRank(double[] values, boolean descFlag) {
		elements = new WeightedElement[values.length];
		for (int i = 0; i < values.length; i++) {
			elements[i] = new WeightedElement("" + i, values[i]);
		}
		Arrays.sort(elements, descWeight);
		int[] rank = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			int oIndex = Integer.parseInt(elements[i].name);
			if (descFlag)
				rank[oIndex] = i;
			else
				rank[oIndex] = (values.length - 1) - i;
		}
		return rank;
	}

}
