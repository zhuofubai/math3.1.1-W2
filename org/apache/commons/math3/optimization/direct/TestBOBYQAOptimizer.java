package org.apache.commons.math3.optimization.direct;

import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleBounds;
//import org.apache.commons.math3.optimization.direct.BOBYQAOptimizerTest.Rosen;
import org.junit.Assert;
import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;
public class TestBOBYQAOptimizer {
	static final int DIM = 13;

	public TestBOBYQAOptimizer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{Profiler.visitNewTest(-1);
		testConstrainedRosenWithMoreInterpolationPoints();
		Profiler.stopProfiling();}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("end");
	}

	public static void testConstrainedRosenWithMoreInterpolationPoints() {
		final double[] startPoint = point(DIM, 0.9999);
		final double[][] boundaries = boundaries(DIM, -1, 2);
		final PointValuePair expected = new PointValuePair(point(DIM, 1.0), 0.0);

		// This should have been 78 because in the code the hard limit is
		// said to be
		// ((DIM + 1) * (DIM + 2)) / 2 - (2 * DIM + 1)
		// i.e. 78 in this case, but the test fails for 48, 59, 62, 63, 64,
		// 65, 66, ...
		final int maxAdditionalPoints = 10;
		// doTest(new Rosen(), startPoint, boundaries, GoalType.MINIMIZE,
		// 1e-12, 1e-6, 2000, 0, expected, "num=" + 0);
		for (int num = 1; num <= maxAdditionalPoints; num++) {
			System.out.print("test" + num);
			doTest(new Rosen(), startPoint, boundaries, GoalType.MINIMIZE,
					1e-12, 1e-6, 2000, num, expected, "num=" + num);
		}
	}

	private static void doTest(MultivariateFunction func, double[] startPoint,
			double[][] boundaries, GoalType goal, double fTol, double pointTol,
			int maxEvaluations, int additionalInterpolationPoints,
			PointValuePair expected, String assertMsg) {

		// System.out.println(func.getClass().getName() + " BEGIN"); // XXX

		int dim = startPoint.length;
		// MultivariateOptimizer optim =
		// new PowellOptimizer(1e-13, Math.ulp(1d));
		// PointValuePair result = optim.optimize(100000, func, goal,
		// startPoint);
		final double[] lB = boundaries == null ? null : boundaries[0];
		final double[] uB = boundaries == null ? null : boundaries[1];
		final int numIterpolationPoints = 2 * dim + 1
				+ additionalInterpolationPoints;
		BOBYQAOptimizer_bug optim = new BOBYQAOptimizer_bug(
				numIterpolationPoints);
		PointValuePair result = boundaries == null ? optim.optimize(
				maxEvaluations, func, goal, new InitialGuess(startPoint))
				: optim.optimize(maxEvaluations, func, goal, new InitialGuess(
						startPoint), new SimpleBounds(lB, uB));

		BOBYQAOptimizer optim2 = new BOBYQAOptimizer(numIterpolationPoints);
		PointValuePair result2 = boundaries == null ? optim2.optimize(
				maxEvaluations, func, goal, new InitialGuess(startPoint))
				: optim2.optimize(maxEvaluations, func, goal, new InitialGuess(
						startPoint), new SimpleBounds(lB, uB));

		// System.out.println(func.getClass().getName() + " = "
		// + optim.getEvaluations() + " f(");
		// for (double x: result.getPoint()) System.out.print(x + " ");
		// System.out.println(") = " + result.getValue());
		double error = result.getValue() - result2.getValue();
		System.out.println("value:" + error);
		for (int i = 0; i < dim; i++) {
			error = result.getPoint()[i] - expected.getPoint()[i];
			// System.out.println("point:"+error);

		}

		// System.out.println(func.getClass().getName() + " END"); // XXX
	}

	private static class Rosen implements MultivariateFunction {

		public double value(double[] x) {
			double f = 0;
			for (int i = 0; i < x.length - 1; ++i)
				f += 1e2 * (x[i] * x[i] - x[i + 1]) * (x[i] * x[i] - x[i + 1])
						+ (x[i] - 1.) * (x[i] - 1.);
			return f;
		}
	}

	private static double[] point(int n, double value) {
		double[] ds = new double[n];
		Arrays.fill(ds, value);
		return ds;
	}

	private static double[][] boundaries(int dim, double lower, double upper) {
		double[][] boundaries = new double[2][dim];
		for (int i = 0; i < dim; i++)
			boundaries[0][i] = lower;
		for (int i = 0; i < dim; i++)
			boundaries[1][i] = upper;
		return boundaries;
	}
}
