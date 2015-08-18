package hoang.tool;

import java.util.*;

public class PredictionMetricTool {
	private static int targetClass;
	@SuppressWarnings("unused")
	static private Comparator<PreditionElement> ascName;
	@SuppressWarnings("unused")
	static private Comparator<PreditionElement> ascIDString;
	@SuppressWarnings("unused")
	static private Comparator<PreditionElement> descWeight;
	static {
		ascName = new Comparator<PreditionElement>() {
			@Override
			public int compare(PreditionElement e1, PreditionElement e2) {
				return e1.getName().compareTo(e2.getName());
			}
		};

		ascIDString = new Comparator<PreditionElement>() {
			@Override
			public int compare(PreditionElement e1, PreditionElement e2) {
				return e1.getIDString().compareTo(e2.getIDString());
			}
		};

		descWeight = new Comparator<PreditionElement>() {
			@Override
			public int compare(PreditionElement e1, PreditionElement e2) {
				if (e2.getPredictionWeight(targetClass)
						- e1.getPredictionWeight(targetClass) > 0)
					return 1;
				else if (e2.getPredictionWeight(targetClass)
						- e1.getPredictionWeight(targetClass) < 0)
					return -1;
				else
					return 0;
			}
		};
	}

	public PreditionElement[] elements;

	public void setElement(String[] trueClasses, String[] predictedClasses) {
		elements = new PreditionElement[trueClasses.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = new PreditionElement(null, null, null, null,
					trueClasses[i], predictedClasses[i], null, null);
		}
	}

	// Classification measure
	public double getPrecision(String[] trueClasses, String[] predictedClasses,
			String targetClass) {
		int nPredtion = 0;
		int nTruePrediction = 0;
		for (int i = 0; i < predictedClasses.length; i++) {
			if (!predictedClasses[i].equals(targetClass))
				continue;
			nPredtion++;
			if (trueClasses[i].equals(targetClass))
				nTruePrediction++;
		}
		if (nPredtion == 0)
			return -1;
		else
			return (double) nTruePrediction / nPredtion;
	}

	public double getPrecision(int[] trueClasses, int[] predictedClasses,
			int targetClass) {
		int nPredtion = 0;
		int nTruePrediction = 0;
		for (int i = 0; i < predictedClasses.length; i++) {
			if (predictedClasses[i] != targetClass)
				continue;
			nPredtion++;
			if (trueClasses[i] == targetClass)
				nTruePrediction++;
		}
		if (nPredtion == 0)
			return -1;
		else
			return (double) nTruePrediction / nPredtion;
	}

	// Clustering measure
	public double getAverageEntropy(int[] trueClasses, int[] predictedClusters,
			boolean weightedflag) {
		HashMap<Integer, HashMap<Integer, Integer>> clusters = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (int i = 0; i < predictedClusters.length; i++) {
			if (clusters.containsKey(predictedClusters[i])) {
				HashMap<Integer, Integer> cluster = clusters
						.get(predictedClusters[i]);
				if (cluster.containsKey(trueClasses[i])) {
					int c = cluster.get(trueClasses[i]) + 1;
					cluster.remove(trueClasses[i]);
					cluster.put(trueClasses[i], c);
					clusters.remove(predictedClusters[i]);
					clusters.put(predictedClusters[i], cluster);
				} else {
					cluster.put(trueClasses[i], 1);
					clusters.remove(predictedClusters[i]);
					clusters.put(predictedClusters[i], cluster);
				}

			} else {
				HashMap<Integer, Integer> cluster = new HashMap<Integer, Integer>();
				cluster.put(trueClasses[i], 1);
				clusters.put(predictedClusters[i], cluster);
			}
		}

		double avgEntropy = 0;
		ArrayTool arrayTool = new ArrayTool();
		Iterator<Map.Entry<Integer, HashMap<Integer, Integer>>> clusterIterator = clusters
				.entrySet().iterator();
		while (clusterIterator.hasNext()) {
			HashMap<Integer, Integer> cluster = clusterIterator.next()
					.getValue();
			int[] count = new int[cluster.size()];
			int k = 0;
			int clusterSize = 0;
			Iterator<Map.Entry<Integer, Integer>> classIterator = cluster
					.entrySet().iterator();
			while (classIterator.hasNext()) {
				count[k] = classIterator.next().getValue();
				clusterSize += count[k];
				k++;
			}

			if (weightedflag)
				avgEntropy += ((double) clusterSize / predictedClusters.length)
						* arrayTool.getIntElementsEntropy(count);
			else
				avgEntropy += arrayTool.getIntElementsEntropy(count);
		}
		if (!weightedflag)
			avgEntropy /= clusters.size();
		return avgEntropy;
	}

	// Recommendation measure

	public double[] getAvgPrecision(int[] atKs,
			WeightedElement[] predictedElements, HashSet<String> trueElements) {
		if (atKs == null)
			return null;
		if (atKs.length == 0)
			return null;
		double[] ap = new double[atKs.length];
		for (int j = 0; j < atKs.length; j++)
			ap[j] = 0;
		if (trueElements.size() == 0)
			return ap;
		RankingTool rankTool = new RankingTool();
		String[] names = new String[predictedElements.length];
		double[] weights = new double[predictedElements.length];
		for (int i = 0; i < predictedElements.length; i++) {
			names[i] = predictedElements[i].getName();
			weights[i] = predictedElements[i].getWeight();
		}
		for (int j = 0; j < atKs.length; j++) {
			int k = atKs[j];
			WeightedElement[] topKpredictedElements = rankTool.getTopKbyWeight(
					names, weights, k);
			int correctPrediction = 0;
			for (int i = 0; i < k; i++) {
				if (trueElements.contains(topKpredictedElements[i].getName()))
					correctPrediction++;
				double precision = (double) correctPrediction / (i + 1);
				ap[j] = ap[j] + precision;
			}
			ap[j] = ap[j] / Math.min(trueElements.size(), k);
		}
		return ap;
	}

	// Predicting measure

	public double getAUPRC(boolean[] labels, double[] scores) {

		RankingTool rankTool = new RankingTool();
		int[] ranks = rankTool.getRank(scores, true);
		int[] invertedRanks = new int[labels.length];
		for (int i = 0; i < labels.length; i++) {
			int index = ranks[i];
			invertedRanks[index] = i;
		}
		double[] precision = new double[labels.length + 1];
		double[] recall = new double[labels.length + 1];
		int nPos = 0;
		precision[0] = 1;
		recall[0] = 0;
		for (int i = 1; i <= labels.length; i++) {
			int oIndex = invertedRanks[i - 1];
			if (labels[oIndex])
				nPos++;
			precision[i] = (double) nPos / i;
			recall[i] = nPos;
		}
		for (int i = 1; i <= labels.length; i++) {
			recall[i] /= nPos;
		}

		double a = 0;
		for (int i = 1; i <= labels.length; i++) {
			a = a + (recall[i] - recall[i - 1])
					* (precision[i - 1] + precision[i]) / 2;
		}

		return a;
	}
}
