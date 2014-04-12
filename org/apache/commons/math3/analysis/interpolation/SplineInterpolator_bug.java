/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.analysis.interpolation;

import log.Logger;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.util.MathArrays;

/**
 * Computes a natural (also known as "free", "unclamped") cubic spline
 * interpolation for the data set.
 * <p>
 * The {@link #interpolate(double[], double[])} method returns a
 * {@link PolynomialSplineFunction} consisting of n cubic polynomials, defined
 * over the subintervals determined by the x values, x[0] < x[i] ... < x[n]. The
 * x values are referred to as "knot points."
 * </p>
 * <p>
 * The value of the PolynomialSplineFunction at a point x that is greater than
 * or equal to the smallest knot point and strictly less than the largest knot
 * point is computed by finding the subinterval to which x belongs and computing
 * the value of the corresponding polynomial at <code>x - x[i] </code> where
 * <code>i</code> is the index of the subinterval. See
 * {@link PolynomialSplineFunction} for more details.
 * </p>
 * <p>
 * The interpolating polynomials satisfy:
 * <ol>
 * <li>The value of the PolynomialSplineFunction at each of the input x values
 * equals the corresponding y value.</li>
 * <li>Adjacent polynomials are equal through two derivatives at the knot points
 * (i.e., adjacent polynomials "match up" at the knot points, as do their first
 * and second derivatives).</li>
 * </ol>
 * </p>
 * <p>
 * The cubic spline interpolation algorithm implemented is as described in R.L.
 * Burden, J.D. Faires, <u>Numerical Analysis</u>, 4th Ed., 1989, PWS-Kent, ISBN
 * 0-53491-585-X, pp 126-131.
 * </p>
 * 
 * @version $Id: SplineInterpolator.java 1379905 2012-09-01 23:56:50Z erans $
 */
public class SplineInterpolator_bug implements UnivariateInterpolator {
	/**
	 * Computes an interpolating function for the data set.
	 * 
	 * @param x
	 *            the arguments for the interpolation points
	 * @param y
	 *            the values for the interpolation points
	 * @return a function which interpolates the data set
	 * @throws DimensionMismatchException
	 *             if {@code x} and {@code y} have different sizes.
	 * @throws NonMonotonicSequenceException
	 *             if {@code x} is not sorted in strict increasing order.
	 * @throws NumberIsTooSmallException
	 *             if the size of {@code x} is smaller than 3.
	 */
	public Logger log = new Logger();

	public PolynomialSplineFunction interpolate(double x[], double y[])
			throws DimensionMismatchException, NumberIsTooSmallException,
			NonMonotonicSequenceException {
		if (x.length != y.length) {
			throw new DimensionMismatchException(x.length, y.length);
		}

		if (x.length < 3) {
			throw new NumberIsTooSmallException(
					LocalizedFormats.NUMBER_OF_POINTS, x.length, 3, true);
		}

		// Number of intervals. The number of data points is n + 1.
		final int n = x.length - 1;

		MathArrays.checkOrder(x);

		// Differences between knot points
		/** bug 1 add 1E-11 at the end of assignment h[i] store in data1*/
		final double h[] = new double[n];
		int tempi = 0;
		for (int i = 0; i < n; i++) {
			h[i] =(x[i + 1] - x[i])+1E-11;
			tempi = i;
		}

		// double []data1={x[n>>1], f[n>>1]};
		// log.add(1, data1, "x[n>>1] f[n>>1]");// exp1
		double[] data1 = { h[tempi], x[tempi + 1], x[tempi] };
		double[]data20={x[tempi + 1]- x[tempi],x[tempi + 1], x[tempi]};
		log.add(20, data20, "x[tempi + 1]- x[tempi]");
		log.add(1, data1, "h[i]");
		// h[i] x[i+1] x[i] exp1
		double[] data2 = { x[tempi + 1] };// x[i+1] exp2
		log.add(2, data2, "x[i+1]");
		double[] data3 = { x[tempi] };
		log.add(3, data3, "x[i]");// x[i] exp3
		final double mu[] = new double[n];
		final double z[] = new double[n + 1];
		mu[0] = 0d;
		z[0] = 0d;
		double g = 0;
		/** bug2 */
		for (int i = 1; i < n; i++) {
			g = 2d * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1];
			mu[i] = h[i] / g;
			z[i] = (3d
					* (y[i + 1] * h[i - 1] - y[i] * (x[i + 1] - x[i - 1]) + y[i - 1]
							* h[i]) / (h[i - 1] * h[i]) - h[i - 1] * z[i - 1])
					/ g;
			tempi = i;
		}
		double[] data4 = { g, (x[tempi + 1] - x[tempi - 1]),
				h[tempi - 1] * mu[tempi - 1] };// exp4
		log.add(4, data4, "g");
		double[] data5 = { x[tempi + 1] - x[tempi - 1], x[tempi + 1],
				x[tempi - 1] };// 2d (x[i+1]-x[i-1]) x[i+1] x[i-1]
		log.add(5, data5, "x[tempi+1]-x[tempi-1]");
		double[] data6 = { h[tempi - 1] * mu[tempi - 1], h[tempi - 1],
				mu[tempi - 1] };
		log.add(6, data6, "h[i-1]*mu[i-1]");// h[i-1]*mu[i-1] h[i-1] mu[i-1]
		double[] data7 = { h[tempi - 1] };
		log.add(7, data7, "h[i-1]");// h[i-1]
		double[] data8 = { mu[tempi - 1] };// mu[i-1]
		log.add(8, data8, "mu[i-1]");		
		
		/** z[i] (3d * (y[i + 1] * h[i - 1] - y[i] * (x[i + 1] - x[i - 1])+ y[i -1] * h[i]) /
		 (h[i - 1] * h[i]) - h[i - 1] * z[i - 1]) /g*/
		
		double[] data9 = {
				z[tempi],
				(3d
						*(y[tempi + 1] * h[tempi - 1] - y[tempi]
								* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]
								* h[tempi]) / (h[tempi - 1] * h[tempi]) - h[tempi - 1]
						* z[tempi - 1]), g };
		log.add(9, data9, "z[i]");

		// 3d * (y[i + 1] * h[i - 1] - y[i] * (x[i + 1] - x[i - 1])+ y[i - 1] *
		// h[i]) /(h[i - 1] * h[i]) h[i - 1] * z[i - 1]
		double[] data10 = {
				3d
						* (y[tempi + 1] * h[tempi - 1] - y[tempi]
								* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]
								* h[tempi]) / (h[tempi - 1] * h[tempi])
						- h[tempi - 1] * z[tempi - 1],
				3d
						* (y[tempi + 1] * h[tempi - 1] - y[tempi]
								* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]
								* h[tempi]) / (h[tempi - 1] * h[tempi]),
				h[tempi - 1] * z[tempi - 1] };
		log.add(10,
				data10,
				"3d* (y[tempi + 1] * h[tempi - 1] - y[tempi]* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]* h[tempi]) / (h[tempi - 1] * h[tempi]) - h[tempi - 1]* z[tempi - 1])"); // h[i
		double[]data11={h[tempi - 1] * h[tempi], h[tempi - 1] , h[tempi]};	
		log.add(11, data11, "h[i - 1] * h[i]");

		double[] data12 = {
				3d
						* (y[tempi + 1] * h[tempi - 1] - y[tempi]
								* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]
								* h[tempi]) / (h[tempi - 1] * h[tempi]),
				(h[tempi - 1] * h[tempi]),
				(y[tempi + 1] * h[tempi - 1] - y[tempi]
						* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]
						* h[tempi]) };
		log.add(12,
				data12,
				"3d* (y[tempi + 1] * h[tempi - 1] - y[tempi]* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]* h[tempi]) / (h[tempi - 1] * h[tempi])");
		
		double[]data13={h[tempi - 1]* z[tempi - 1],h[tempi - 1], z[tempi - 1]};
		log.add(13, data13, "h[i - 1]* z[i - 1])");
		
		double []data14={y[tempi + 1] * h[tempi - 1] - y[tempi]* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]* h[tempi], y[tempi + 1] * h[tempi - 1], y[tempi - 1]* h[tempi],y[tempi]* (x[tempi + 1] - x[tempi - 1]) };
		log.add(14, data14, "y[tempi + 1] * h[tempi - 1] - y[tempi]* (x[tempi + 1] - x[tempi - 1]) + y[tempi - 1]* h[tempi]");
		double []data15={y[tempi + 1] * h[tempi - 1],y[tempi + 1],h[tempi - 1]};
		log.add(15, data15, "y[tempi + 1] * h[tempi - 1]");
		double []data16={y[tempi]* (x[tempi + 1] - x[tempi - 1]),(x[tempi + 1] - x[tempi - 1]),y[tempi]};
		log.add(16, data16, "y[tempi]* (x[tempi + 1] - x[tempi - 1])");
		double []data17={(x[tempi + 1] - x[tempi - 1]), x[tempi + 1], x[tempi - 1]};
		log.add(17, data17, "(x[tempi + 1] - x[tempi - 1])");
		double []data18={y[tempi - 1]* h[tempi],y[tempi - 1], h[tempi]};
		log.add(18, data18, "y[tempi - 1]* h[tempi]");
		double []data19 ={h[tempi-1]*h[tempi],h[tempi-1],h[tempi]};
		log.add(19, data19, "h[tempi-1]*h[tempi]");
		log.logFile();
        log.clear();
		// cubic spline coefficients -- b is linear, c quadratic, d is cubic
		// (original y's are constants)
		final double b[] = new double[n];
		final double c[] = new double[n + 1];
		final double d[] = new double[n];

		z[n] = 0d;
		c[n] = 0d;

		for (int j = n - 1; j >= 0; j--) {
			c[j] = z[j] - mu[j] * c[j + 1];
			b[j] = (y[j + 1] - y[j]) / h[j] - h[j] * (c[j + 1] + 2d * c[j])
					/ 3d;
			d[j] = (c[j + 1] - c[j]) / (3d * h[j]);
		}

		final PolynomialFunction polynomials[] = new PolynomialFunction[n];
		final double coefficients[] = new double[4];
		for (int i = 0; i < n; i++) {
			coefficients[0] = y[i];
			coefficients[1] = b[i];
			coefficients[2] = c[i];
			coefficients[3] = d[i];
			polynomials[i] = new PolynomialFunction(coefficients);
		}

		return new PolynomialSplineFunction(x, polynomials);
	}
}
