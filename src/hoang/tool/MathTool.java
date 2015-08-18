package hoang.tool;

public class MathTool {
	public static double getEuclideanNorm(double[] x) {
		double norm = 0;
		for (int i = 0; i < x.length; i++)
			norm += Math.pow(x[i], 2);
		norm = Math.sqrt(norm);
		return norm;
	}

	public static double getEuclideanNorm(int[] x) {
		double norm = 0;
		for (int i = 0; i < x.length; i++)
			norm += Math.pow(x[i], 2);
		norm = Math.sqrt(norm);
		return norm;
	}

	public static double getDotProd(double[] x, double[] y) {
		double prod = 0;
		for (int i = 0; i < x.length; i++)
			prod += (x[i] * y[i]);
		return prod;
	}

	public static double getDotProd(double[] x, int[] y) {
		double prod = 0;
		for (int i = 0; i < x.length; i++)
			prod += (x[i] * y[i]);
		return prod;
	}

	public static double getDotProd(int[] x, double[] y) {
		double prod = 0;
		for (int i = 0; i < x.length; i++)
			prod += (x[i] * y[i]);
		return prod;
	}

	public static double getDotProd(int[] x, int[] y) {
		double prod = 0;
		for (int i = 0; i < x.length; i++)
			prod += (x[i] * y[i]);
		return prod;
	}

}
