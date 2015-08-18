package hoang.tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.*;

public class StatTool {
	public static int sampleMult(double[] prob, boolean flag, Random rand) {
		double p = 0;
		if (!flag) {// prob is not cummulative
			for (int i = 0; i < prob.length; i++) {
				p += prob[i];
			}
			p = rand.nextDouble() * p;
		} else {
			p = rand.nextDouble() * prob[prob.length - 1];
		}
		double csum = 0;
		for (int i = 0; i < prob.length; i++) {
			if (!flag)// prob is not cummulative
				csum += prob[i];
			else
				csum = prob[i];
			if (csum > p)
				return i;
		}

		System.out.println("p = " + p);
		for (int i = 0; i < prob.length; i++)
			System.out.println(prob[i] + "\t");

		System.out.println("mult sample failed!");
		System.exit(-1);
		return -1;
	}

	public static double[] sampleDirichlet(double[] alpha) {
		double[] instance = new double[alpha.length];
		double sum = 0;
		for (int i = 0; i < instance.length; i++) {
			if (alpha[i] <= 0)
				instance[i] = 0;
			else {
				GammaDistribution gamma = new GammaDistribution(alpha[i], 1);
				instance[i] = gamma.sample();
			}
			if (instance[i] < 0) {
				System.out.println("gamma sample failed!");
				System.exit(-1);
			}
			sum += instance[i];
		}
		for (int i = 0; i < instance.length; i++) {
			instance[i] /= sum;
		}
		return instance;
	}

	public static double[] sampleDirichletSkew(double[] alpha, int massIndex,
			double proportion) {
		if (proportion < 0 || proportion > 1)
			return sampleDirichlet(alpha);

		if (massIndex < 0 || massIndex >= alpha.length)
			return sampleDirichlet(alpha);

		double[] alphaPrime = new double[alpha.length - 1];
		int k = 0;
		for (int i = 0; i < alpha.length; i++) {
			if (i != massIndex) {
				alphaPrime[k] = alpha[i];
				k++;
			}
		}
		double[] instancePrime = sampleDirichlet(alphaPrime);
		double[] instance = new double[alpha.length];
		k = 0;
		for (int i = 0; i < alpha.length; i++) {
			if (i != massIndex) {
				instance[i] = instancePrime[k] * (1 - proportion);
				k++;
			} else
				instance[i] = proportion;
		}
		return instance;
	}

	public static double[] sampleDirichletSkew(double[] alpha,
			double focusPropotion, double massProportion, Random rand) {
		if (massProportion < 0 || massProportion > 1)
			return sampleDirichlet(alpha);
		if (focusPropotion < 0 || focusPropotion > 1)
			return sampleDirichlet(alpha);

		int nFocusElements = (int) Math.round(alpha.length * focusPropotion);

		boolean[] focusMark = new boolean[alpha.length];
		for (int i = 0; i < focusMark.length; i++)
			focusMark[i] = false;

		for (int i = 0; i < nFocusElements; i++) {
			int index = rand.nextInt(alpha.length);
			while (focusMark[index]) {
				index = sampleMult(alpha, false, rand);				
			}
			focusMark[index] = true;
		}

		double[] alphaPrime = new double[alpha.length - nFocusElements];
		double[] beta = new double[nFocusElements];
		int k = 0;
		int l = 0;
		for (int i = 0; i < alpha.length; i++) {
			if (!focusMark[i]) {
				alphaPrime[k] = alpha[i];
				k++;
			} else {
				beta[l] = alpha[i];
				l++;
			}
		}
		double[] nonFocusInstance = sampleDirichlet(alphaPrime);
		double[] focusInstance = sampleDirichlet(beta);
		double[] instance = new double[alpha.length];
		k = 0;
		l = 0;
		for (int i = 0; i < alpha.length; i++) {
			if (!focusMark[i]) {
				instance[i] = nonFocusInstance[k] * (1 - massProportion);
				k++;
			} else {
				instance[i] = focusInstance[l] * massProportion;
				l++;
			}
		}
		return instance;
	}

	public static double getKLDistance(double[] p, double[] q) {
		int d = 0;
		for (int i = 0; i < p.length; i++) {
			if (q[i] < 0 || p[i] < 0)
				return -1;
			if (q[i] <= Double.MIN_NORMAL && p[i] > Double.MIN_NORMAL)
				return -1;
			if (q[i] <= Double.MIN_NORMAL && p[i] == Double.MIN_NORMAL)
				continue;
			d += Math.log(p[i] / q[i]) * p[i];
		}
		return d;
	}

	public static double getKLDistance(double[] p, double[] q,
			int[] indexMatching) {
		int d = 0;
		for (int i = 0; i < p.length; i++) {
			int j = indexMatching[i];
			if (q[j] < 0 || p[i] < 0)
				return -1;
			if (q[j] <= Double.MIN_NORMAL && p[i] > Double.MIN_NORMAL)
				return -1;
			if (q[j] <= Double.MIN_NORMAL && p[i] == Double.MIN_NORMAL)
				continue;
			d += Math.log(p[i] / q[j]) * p[i];
		}
		return d;
	}

	public static double getEuclideanDistance(double[] p, double[] q) {
		double d = 0;
		for (int i = 0; i < p.length; i++) {
			d += Math.pow(p[i] - q[i], 2);
		}
		/*
		 * for (int i = 0; i < p.length; i++) System.out.print(p[i] + "\t");
		 * System.out.println(""); for (int i = 0; i < p.length; i++)
		 * System.out.print(q[i] + "\t"); System.out.println("");
		 */

		d = Math.sqrt(d);
		// System.out.println("ED = " + d);
		return d;
	}

	public static double getEuclideanDistance(double[] p, double[] q,
			int[] indexMatching) {
		double d = 0;
		for (int i = 0; i < p.length; i++) {
			int j = indexMatching[i];
			// System.out.println("j = " + j + "\t q.length = " + q.length);
			d += Math.pow(p[i] - q[j], 2);
		}
		d = Math.sqrt(d);
		return d;
	}

	public static double getMean(double[] p) {
		double mean = 0;
		for (int i = 0; i < p.length; i++) {
			mean += p[i];
		}
		mean /= (p.length);
		return mean;
	}

	public static double getMean(int[] p) {
		double mean = 0;
		for (int i = 0; i < p.length; i++) {
			mean += p[i];
		}
		mean /= (p.length);
		return mean;
	}

	public static double getStdDev(double[] p) {
		double stdDev = 0;
		double mean = getMean(p);
		for (int i = 0; i < p.length; i++) {
			stdDev += Math.pow(p[i] - mean, 2);
		}
		stdDev /= (p.length);
		stdDev = Math.sqrt(stdDev);
		return stdDev;
	}

	public static double getStdDev(int[] p) {
		double stdDev = 0;
		double mean = getMean(p);
		for (int i = 0; i < p.length; i++) {
			stdDev += Math.pow(p[i] - mean, 2);
		}
		stdDev /= (p.length);
		stdDev = Math.sqrt(stdDev);
		return stdDev;
	}

	public static double getCovariance(double[] p, double[] q) {
		double cov = 0;
		double p_mean = getMean(p);
		double q_mean = getMean(q);
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(double[] p, int[] q) {
		double cov = 0;
		double p_mean = getMean(p);
		double q_mean = getMean(q);
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(int[] p, double[] q) {
		double cov = 0;
		double p_mean = getMean(p);
		double q_mean = getMean(q);
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(int[] p, int[] q) {
		double cov = 0;
		double p_mean = getMean(p);
		double q_mean = getMean(q);
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(double[] p, double p_mean, double[] q,
			double q_mean) {
		double cov = 0;
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(double[] p, double p_mean, int[] q,
			double q_mean) {
		double cov = 0;
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(int[] p, double p_mean, double[] q,
			double q_mean) {
		double cov = 0;
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getCovariance(int[] p, double p_mean, int[] q,
			double q_mean) {
		double cov = 0;
		for (int i = 0; i < p.length; i++) {
			cov += ((p[i] - p_mean) * (q[i] - q_mean));
		}
		cov /= (p.length);
		return cov;
	}

	public static double getPearsonCC(double[] p, double[] q) {

		double p_mean = 0;
		double q_mean = 0;

		for (int i = 0; i < p.length; i++) {
			p_mean += p[i];
			q_mean += q[i];
		}

		p_mean /= (p.length);
		q_mean /= (p.length);

		double nominator = 0;
		double p_cov = 0;
		double q_cov = 0;

		for (int i = 0; i < p.length; i++) {
			nominator += (p[i] - p_mean) * (q[i] - q_mean);
			p_cov += Math.pow(p[i] - p_mean, 2);
			q_cov += Math.pow(q[i] - q_mean, 2);
		}
		p_cov = Math.sqrt(p_cov);
		q_cov = Math.sqrt(q_cov);
		return nominator / (p_cov * q_cov);
	}

	public static double getPearsonCC(double[] p, int[] q) {

		double p_mean = 0;
		double q_mean = 0;

		for (int i = 0; i < p.length; i++) {
			p_mean += p[i];
			q_mean += q[i];
		}

		p_mean /= (p.length);
		q_mean /= (p.length);

		double nominator = 0;
		double p_cov = 0;
		double q_cov = 0;

		for (int i = 0; i < p.length; i++) {
			nominator += (p[i] - p_mean) * (q[i] - q_mean);
			p_cov += Math.pow(p[i] - p_mean, 2);
			q_cov += Math.pow(q[i] - q_mean, 2);
		}
		p_cov = Math.sqrt(p_cov);
		q_cov = Math.sqrt(q_cov);
		return nominator / (p_cov * q_cov);
	}

	public static double getPearsonCC(int[] p, double[] q) {

		double p_mean = 0;
		double q_mean = 0;

		for (int i = 0; i < p.length; i++) {
			p_mean += p[i];
			q_mean += q[i];
		}

		p_mean /= (p.length);
		q_mean /= (p.length);

		double nominator = 0;
		double p_cov = 0;
		double q_cov = 0;

		for (int i = 0; i < p.length; i++) {
			nominator += (p[i] - p_mean) * (q[i] - q_mean);
			p_cov += Math.pow(p[i] - p_mean, 2);
			q_cov += Math.pow(q[i] - q_mean, 2);
		}
		p_cov = Math.sqrt(p_cov);
		q_cov = Math.sqrt(q_cov);
		return nominator / (p_cov * q_cov);
	}

	public static double getPearsonCC(int[] p, int[] q) {

		double p_mean = 0;
		double q_mean = 0;

		for (int i = 0; i < p.length; i++) {
			p_mean += p[i];
			q_mean += q[i];
		}

		p_mean /= (p.length);
		q_mean /= (p.length);

		double nominator = 0;
		double p_cov = 0;
		double q_cov = 0;

		for (int i = 0; i < p.length; i++) {
			nominator += (p[i] - p_mean) * (q[i] - q_mean);
			p_cov += Math.pow(p[i] - p_mean, 2);
			q_cov += Math.pow(q[i] - q_mean, 2);
		}
		p_cov = Math.sqrt(p_cov);
		q_cov = Math.sqrt(q_cov);
		return nominator / (p_cov * q_cov);
	}

	public static double getPearsonRankCC(double[] p, double[] q) {
		RankingTool rankingTool = new RankingTool();
		int[] p_rank = rankingTool.getRank(p, true);
		int[] q_rank = rankingTool.getRank(q, true);

		double mean = ((double) p.length - 1) / 2;
		double nominator = 0;
		double p_cov = 0;
		double q_cov = 0;

		for (int i = 0; i < p.length; i++) {
			nominator += (p_rank[i] - mean) * (q_rank[i] - mean);
			p_cov += Math.pow(p_rank[i] - mean, 2);
			q_cov += Math.pow(q_rank[i] - mean, 2);
		}
		p_cov = Math.sqrt(p_cov);
		q_cov = Math.sqrt(q_cov);
		return nominator / (p_cov * q_cov);
	}

	public static double getPearsonRankCC(HashMap<Integer, Double> p,
			HashMap<Integer, Double> q) {

		int nComm = 0;
		Iterator<Map.Entry<Integer, Double>> pIter = p.entrySet().iterator();
		while (pIter.hasNext()) {
			if (q.containsKey(pIter.next().getKey()))
				nComm++;
		}
		if (nComm == 0)
			return 0;
		int nElements = p.size() + q.size() - nComm;
		double[] pValues = new double[nElements];
		double[] qValues = new double[nElements];

		int index = 0;
		pIter = p.entrySet().iterator();
		while (pIter.hasNext()) {
			Map.Entry<Integer, Double> pPair = pIter.next();
			pValues[index] = pPair.getValue();
			if (q.containsKey(pPair.getKey())) {
				qValues[index] = q.get(pPair.getKey());
				q.remove(pPair.getKey());
			} else {
				qValues[index] = 0;
			}
			index++;
		}

		Iterator<Map.Entry<Integer, Double>> qIter = q.entrySet().iterator();
		while (qIter.hasNext()) {
			Map.Entry<Integer, Double> qPair = qIter.next();
			pValues[index] = 0;
			qValues[index] = qPair.getValue();
			index++;
		}
		return getPearsonRankCC(pValues, qValues);
	}
}
