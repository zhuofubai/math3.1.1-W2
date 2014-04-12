package experiment;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.analysis.MultivariateFunction;
//import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.direct.BOBYQAOptimizer_bug;
import org.apache.commons.math3.optimization.direct.BOBYQAOptimizer;

//import org.apache.commons.math3.optimization.direct.BOBYQAOptimizerTest.Rosen;
import org.junit.Assert;
import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class TestBOBYQAOptimizer {
	static final int DIM = 13;
	static int threshold1=0;
	static int threshold2=0;
	static int sum=0;
	public TestBOBYQAOptimizer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Profiler.visitNewTest(-1);
			testConstrainedRosenWithMoreInterpolationPoints();
			Profiler.stopProfiling();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
		System.out.println("threshold1 "+threshold1);
		System.out.println("threshold2 "+threshold2);
		System.out.println("sum "+sum);
	}

	public static void testConstrainedRosenWithMoreInterpolationPoints() {
		double[] startPoint = point(DIM, 2);
		// final double[][] boundaries = boundaries(DIM, -1, 2);
		double[][] boundaries = null;
		final PointValuePair expected = new PointValuePair(point(DIM, 1.0), 0.0);
		int maxcount = 0;
		double ft = 0, pt = 0;
		// This should have been 78 because in the code the hard limit is
		// said to be
		// ((DIM + 1) * (DIM + 2)) / 2 - (2 * DIM + 1)
		// i.e. 78 in this case, but the test fails for 48, 59, 62, 63, 64,
		// 65, 66, ...
		final int maxAdditionalPoints = 3;
		// doTest(new Rosen(), startPoint, boundaries, GoalType.MINIMIZE,
		// 1e-12, 1e-6, 2000, 0, expected, "num=" + 0);
		MultivariateFunction func = null;
		int testid = 0;
		
		try {
			Profiler.visitNewTest(-1);
			for (int funcindex = 1; funcindex < 3; funcindex++) {
				System.out.println("test func: " + funcindex);
				func = getFunc(funcindex);
				maxcount = getMaxcount(funcindex);
				ft = getFt(funcindex);
				pt = getPt(funcindex);
				for (int paranum = 1; paranum < 21; paranum++) {
					double para = paranum * 0.1;

					startPoint = getStartPoint(funcindex, para);
					for (int num = 1; num <= maxAdditionalPoints; num++) {
						Profiler.visitNewTest(testid);
						// System.out.println(num);
						// System.out.println(testid);
						// System.out.print("test"+testid +
						// " func: "+funcindex+" para: "+para+" additionalPoint: "+num+" "
						// );
					try{System.out.print("test"+testid);	
						doTest(func, startPoint, boundaries, GoalType.MINIMIZE,
								ft, pt, maxcount, num, expected, "num=" + num);
						
					} catch (org.apache.commons.math3.exception.TooManyEvaluationsException e) {
						System.out.println("test"+testid+"exceed the maximum iteration");
					}
						testid = testid + 1;
					}
				}
			}
			Profiler.stopProfiling();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getMaxcount(int funcindex) {
		int maxcount = 5000;

		switch (funcindex) {
		case 5:
			maxcount = 12000;
			break;
		case 8:
			maxcount = 50000;
			break;
		default:
			maxcount = 2000;
			break;
		}
		return maxcount;
	}

	public static double getPt(int funcindex) {
		double pt = 1e-6;
		switch (funcindex) {
		case 3:
			pt = 5e-5;
			break;
		case 7:
			pt = 5e-6;
			break;
		case 8:
			pt = 1.3e-1;
			break;
		case 10:
			pt = 1e-5;

			break;
		default:
			pt = 1e-6;
			break;
		}
		return pt;
	}

	public static double getFt(int funcindex) {
		double ft = 1e-13;
		switch (funcindex) {

		case 5:
			ft = 1e-12;
			break;
		case 7:
			ft = 2e-10;
			break;
		case 8:
			ft = 1e-2;
			break;
		case 10:
			ft = 1e-8;
			break;
		default:
			ft = 1e-13;
			break;
		}
		return ft;
	}

	public static double[] getStartPoint(int funcindex, double para) {
		double[] startPoint = null;
		switch (funcindex) {
		case 8:
			startPoint = point(DIM / 2, para);
			break;
		default:
			startPoint = point(DIM, para);
			break;
		}
		return startPoint;
	}

	public static MultivariateFunction getFunc(int i) {
		MultivariateFunction func = null;
		switch (i) {

		case 1:
			func = new Cigar();
			break;
		case 2:
			func = new Tablet();
			break;
		case 3:
			func = new CigTab();
			break;
		case 4:
			func = new TwoAxes();
			break;
		case 5:
			func = new ElliRotated();
			break;
		case 6:
			func = new Elli();
			break;
		case 7:
			func = new MinusElli();
			break;
		case 8:
			func = new SsDiffPow();
			break;
		case 9:
			func = new Rosen();
			break;
		case 10:
			func = new Ackley();
			break;

		}
		return func;
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
					: optim.optimize(maxEvaluations, func, goal,
							new InitialGuess(startPoint), new SimpleBounds(lB,
									uB));

			BOBYQAOptimizer optim2 = new BOBYQAOptimizer(numIterpolationPoints);
			PointValuePair result2 = boundaries == null ? optim2.optimize(
					maxEvaluations, func, goal, new InitialGuess(startPoint))
					: optim2.optimize(maxEvaluations, func, goal,
							new InitialGuess(startPoint), new SimpleBounds(lB,
									uB));

			// System.out.println(func.getClass().getName() + " = "
			// + optim.getEvaluations() + " f(");
			// for (double x: result.getPoint()) System.out.print(x + " ");
			// System.out.println(") = " + result.getValue());
			double error = result.getValue() - result2.getValue();
			if(error>1e-13){
				threshold1++;
			}
			if(error>1e-12){
				threshold2++;
			}
			sum++;
			System.out.println("value of error :" + error);
			for (int i = 0; i < dim; i++) {
				error = result.getPoint()[i] - result2.getPoint()[i];
				// System.out.println("point error" + ":" + error);
			}

		
		// System.out.println(func.getClass().getName() + " END"); // XXX
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

class Sphere implements MultivariateFunction {

	public double value(double[] x) {
		double f = 0;
		for (int i = 0; i < x.length; ++i)
			f += x[i] * x[i];
		return f;
	}
}

class Cigar implements MultivariateFunction {
	private double factor;

	Cigar() {
		this(1e3);
	}

	Cigar(double axisratio) {
		factor = axisratio * axisratio;
	}

	public double value(double[] x) {
		double f = x[0] * x[0];
		for (int i = 1; i < x.length; ++i)
			f += factor * x[i] * x[i];
		return f;
	}
}

class Tablet implements MultivariateFunction {
	private double factor;

	Tablet() {
		this(1e3);
	}

	Tablet(double axisratio) {
		factor = axisratio * axisratio;
	}

	public double value(double[] x) {
		double f = factor * x[0] * x[0];
		for (int i = 1; i < x.length; ++i)
			f += x[i] * x[i];
		return f;
	}
}

class CigTab implements MultivariateFunction {
	private double factor;

	CigTab() {
		this(1e4);
	}

	CigTab(double axisratio) {
		factor = axisratio;
	}

	public double value(double[] x) {
		int end = x.length - 1;
		double f = x[0] * x[0] / factor + factor * x[end] * x[end];
		for (int i = 1; i < end; ++i)
			f += x[i] * x[i];
		return f;
	}
}

class TwoAxes implements MultivariateFunction {

	private double factor;

	TwoAxes() {
		this(1e6);
	}

	TwoAxes(double axisratio) {
		factor = axisratio * axisratio;
	}

	public double value(double[] x) {
		double f = 0;
		for (int i = 0; i < x.length; ++i)
			f += (i < x.length / 2 ? factor : 1) * x[i] * x[i];
		return f;
	}
}

class ElliRotated implements MultivariateFunction {
	private Basis B = new Basis();
	private double factor;

	ElliRotated() {
		this(1e3);
	}

	ElliRotated(double axisratio) {
		factor = axisratio * axisratio;
	}

	public double value(double[] x) {
		double f = 0;
		x = B.Rotate(x);
		for (int i = 0; i < x.length; ++i)
			f += Math.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
		return f;
	}
}

class Elli implements MultivariateFunction {

	private double factor;

	Elli() {
		this(1e3);
	}

	Elli(double axisratio) {
		factor = axisratio * axisratio;
	}

	public double value(double[] x) {
		double f = 0;
		for (int i = 0; i < x.length; ++i)
			f += Math.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
		return f;
	}
}

class MinusElli implements MultivariateFunction {
	private final Elli elli = new Elli();

	public double value(double[] x) {
		return 1.0 - elli.value(x);
	}
}

class DiffPow implements MultivariateFunction {
	// private int fcount = 0;
	public double value(double[] x) {
		double f = 0;
		for (int i = 0; i < x.length; ++i)
			f += Math.pow(Math.abs(x[i]), 2. + 10 * (double) i
					/ (x.length - 1.));
		// System.out.print("" + (fcount++) + ") ");
		// for (int i = 0; i < x.length; i++)
		// System.out.print(x[i] + " ");
		// System.out.println(" = " + f);
		return f;
	}
}

class SsDiffPow implements MultivariateFunction {

	public double value(double[] x) {
		double f = Math.pow(new DiffPow().value(x), 0.25);
		return f;
	}
}

class Rosen implements MultivariateFunction {

	public double value(double[] x) {
		double f = 0;
		for (int i = 0; i < x.length - 1; ++i)
			f += 1e2 * (x[i] * x[i] - x[i + 1]) * (x[i] * x[i] - x[i + 1])
					+ (x[i] - 1.) * (x[i] - 1.);
		return f;
	}
}

class Ackley implements MultivariateFunction {
	private double axisratio;

	Ackley(double axra) {
		axisratio = axra;
	}

	public Ackley() {
		this(1);
	}

	public double value(double[] x) {
		double f = 0;
		double res2 = 0;
		double fac = 0;
		for (int i = 0; i < x.length; ++i) {
			fac = Math.pow(axisratio, (i - 1.) / (x.length - 1.));
			f += fac * fac * x[i] * x[i];
			res2 += Math.cos(2. * Math.PI * fac * x[i]);
		}
		f = (20. - 20. * Math.exp(-0.2 * Math.sqrt(f / x.length))
				+ Math.exp(1.) - Math.exp(res2 / x.length));
		return f;
	}
}

class Rastrigin implements MultivariateFunction {

	private double axisratio;
	private double amplitude;

	Rastrigin() {
		this(1, 10);
	}

	Rastrigin(double axisratio, double amplitude) {
		this.axisratio = axisratio;
		this.amplitude = amplitude;
	}

	public double value(double[] x) {
		double f = 0;
		double fac;
		for (int i = 0; i < x.length; ++i) {
			fac = Math.pow(axisratio, (i - 1.) / (x.length - 1.));
			if (i == 0 && x[i] < 0)
				fac *= 1.;
			f += fac * fac * x[i] * x[i] + amplitude
					* (1. - Math.cos(2. * Math.PI * fac * x[i]));
		}
		return f;
	}
}

class Basis {
	double[][] basis;
	Random rand = new Random(2); // use not always the same basis

	double[] Rotate(double[] x) {
		GenBasis(x.length);
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; ++i) {
			y[i] = 0;
			for (int j = 0; j < x.length; ++j)
				y[i] += basis[i][j] * x[j];
		}
		return y;
	}

	void GenBasis(int DIM) {
		if (basis != null ? basis.length == DIM : false)
			return;

		double sp;
		int i, j, k;

		/* generate orthogonal basis */
		basis = new double[DIM][DIM];
		for (i = 0; i < DIM; ++i) {
			/* sample components gaussian */
			for (j = 0; j < DIM; ++j)
				basis[i][j] = rand.nextGaussian();
			/* substract projection of previous vectors */
			for (j = i - 1; j >= 0; --j) {
				for (sp = 0., k = 0; k < DIM; ++k)
					sp += basis[i][k] * basis[j][k]; /* scalar product */
				for (k = 0; k < DIM; ++k)
					basis[i][k] -= sp * basis[j][k]; /* substract */
			}
			/* normalize */
			for (sp = 0., k = 0; k < DIM; ++k)
				sp += basis[i][k] * basis[i][k]; /* squared norm */
			for (k = 0; k < DIM; ++k)
				basis[i][k] /= Math.sqrt(sp);
		}
	}
}