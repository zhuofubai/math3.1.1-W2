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
 * limitations under  the License.
 */
package experiment;

import java.io.Serializable;
import java.util.Random;

import log.Logger;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.transform.DctNormalization;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.RealTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.transform.TransformUtils;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

/**
 * Implements the Fast Cosine Transform for transformation of one-dimensional
 * real data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * <p>
 * There are several variants of the discrete cosine transform. The present
 * implementation corresponds to DCT-I, with various normalization conventions,
 * which are specified by the parameter {@link DctNormalization}.
 * <p>
 * DCT-I is equivalent to DFT of an <em>even extension</em> of the data series.
 * More precisely, if x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is the data set
 * to be cosine transformed, the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup> is
 * defined as follows
 * <ul>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>k</sub> if 0 &le; k &lt; N,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>2N-2-k</sub> if N &le; k &lt; 2N -
 * 2.</li>
 * </ul>
 * <p>
 * Then, the standard DCT-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of the N first elements of the DFT of the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup> <br/>
 * y<sub>n</sub> = (1 / 2) &sum;<sub>k=0</sub><sup>2N-3</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N - 2)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * <p>
 * The present implementation of the discrete cosine transform as a fast cosine
 * transform requires the length of the data set to be a power of two plus one
 * (N&nbsp;=&nbsp;2<sup>n</sup>&nbsp;+&nbsp;1). Besides, it implicitly assumes
 * that the sampled function is even.
 * 
 * @version $Id: FastCosineTransformer.java 1385310 2012-09-16 16:32:10Z tn $
 * @since 1.2
 */


public class FastCosineTransformer_bug2 implements RealTransformer,
		Serializable {

	/** Serializable version identifier. */
	static final long serialVersionUID = 20120212L;

	/** The type of DCT to be performed. */
	private final DctNormalization normalization;

	/**
	 * The instrumented logger
	 */

	/**
	 * Creates a new instance of this class, with various normalization
	 * conventions.
	 * 
	 * @param normalization
	 *            the type of normalization to be applied to the transformed
	 *            data
	 */
	public FastCosineTransformer_bug2(final DctNormalization normalization) {
		this.normalization = normalization;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws MathIllegalArgumentException
	 *             if the length of the data array is not a power of two plus
	 *             one
	 */
	public double[] transform(final double[] f, final TransformType type)
			throws MathIllegalArgumentException {
		if (type == TransformType.FORWARD) {
			if (normalization == DctNormalization.ORTHOGONAL_DCT_I) {
				final double s = FastMath.sqrt(2.0 / (f.length - 1));
				return TransformUtils.scaleArray(fct(f), s);
			}
			return fct(f);
		}
		final double s2 = 2.0 / (f.length - 1);
		final double s1;
		if (normalization == DctNormalization.ORTHOGONAL_DCT_I) {
			s1 = FastMath.sqrt(s2);
		} else {
			s1 = s2;
		}
		return TransformUtils.scaleArray(fct(f), s1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
	 *             if the lower bound is greater than, or equal to the upper
	 *             bound
	 * @throws org.apache.commons.math3.exception.NotStrictlyPositiveException
	 *             if the number of sample points is negative
	 * @throws MathIllegalArgumentException
	 *             if the number of sample points is not a power of two plus one
	 */
	public double[] transform(final UnivariateFunction f, final double min,
			final double max, final int n, final TransformType type)
			throws MathIllegalArgumentException {

		final double[] data = FunctionUtils.sample(f, min, max, n);
		return transform(data, type);
	}

	/**
	 * Perform the FCT algorithm (including inverse).
	 * 
	 * @param f
	 *            the real data array to be transformed
	 * @return the real transformed array
	 * @throws MathIllegalArgumentException
	 *             if the length of the data array is not a power of two plus
	 *             one
	 */
	protected double[] fct(double[] f) throws MathIllegalArgumentException {
		final double[] transformed = new double[f.length];
		final int n = f.length - 1;
		if (!ArithmeticUtils.isPowerOfTwo(n)) {
			throw new MathIllegalArgumentException(
					LocalizedFormats.NOT_POWER_OF_TWO_PLUS_ONE,
					Integer.valueOf(f.length));
		}
		if (n == 1) { // trivial case
			transformed[0] = 0.5 * (f[0] + f[1]);
			transformed[1] = 0.5 * (f[0] - f[1]);
			return transformed;
		}
		test test1=new test();
		// construct a new array and perform FFT on it
		final double[] x = new double[n];
		x[0] = 0.5 * (f[0] + f[n]);
		String funname = "cosh/";
		double tempexpression = 0;
		double ta=3.24,tb=2.31,tc=7.86,td=5.12;
		int te=2;
		boolean tf=false;
		x[n >> 1] = f[n >> 1];
		ta=tb+tc+mid((int)ta+1, (int)tb, (int)tc)+td;
		// temporary variable for transformed[1]
		double t1 = 0.5 * (f[0] - f[n]);
		  ta=(te>>2)+tc%tb+td;
		  ta=tb+tc+td;
		  ta=tb+tc-td;
		  ta=tb+tc+td+te;
		  ta=tb*tc*td;
		  ta=tb*tc/td;
		  ta=tb*tc*td*te;
		  ta=ta*ta+tb*tb+tc*tc;
		  ta=tc-(td+te);
		  ta=tc+tb-(td+te+tc);
		  ta=tc*tb/tc+test1.a+3;
		  ta=tc+tb/td-test1.f.a;
		  ta=td+Math.cos(ta+tc-td-te*tb)+tb;
		  ta=Math.min(tc,td+1)+1;		  
		for (int i = 1; i < (n >> 1); i++) {
			final double a = 0.5 * (f[i] + f[n - i]);

			final double b = FastMath.sin(i * FastMath.PI / n)* (f[i] - f[n - i]);

			/*****
			 * bug2 store in Data2 FastMath.sin(i * FastMath.PI / n) to
			 * FastMath.sin(2*i * FastMath.PI / n)
			 *******/

			final double c = FastMath.cos(i * FastMath.PI / n)* (f[i] - f[n - i]);

			x[i] = a + b;

			x[n - i] = a - b;

			tempexpression = t1;

			t1 =t1+ c;

		}

		FastFourierTransformer transformer;
		transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] y = transformer.transform(x, TransformType.FORWARD);

		// reconstruct the FCT result for the original array
		transformed[0] = y[0].getReal();

		transformed[1] = t1;

		for (int i = 1; i < (n >> 1); i++) {

			transformed[2 * i] = y[i].getReal();

			/***
			 * bug 1, store in Data1, add Math.abs() on transformed[2 * i - 1] -
			 * y[i].getImaginary()
			 ***/

			transformed[2 * i + 1] = transformed[2 * i - 1]- y[i].getImaginary();

		}

		transformed[n] = y[n >> 1].getReal();

		return transformed;
	}
	
	private static int calc(int a, int b,int c){
		int u=0;
		b=c*c;
		a=u+(c-b);
		u=a+c+b;
		return u;
	}
	private static int mid(int x, int y, int z) {

		int m = z;
		int a=1,b=2,c=3,d=4;
		a=b+c;
		b=b*b*c-a;
		c=calc(a,b,c);
		d=a+5+c;
		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				m = x;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		return m;
	}
	
}


class Test2 {

	/**
	 * @param args
	 */
	public static void main() {
		
		try{
			Profiler.visitNewTest(2000);
			System.out.println("-----------------------------------------");
			int a1 = 2, b1 = 10;
			System.out.println(a1 + b1);
			System.out.println(a1 - b1);
			System.out.println(a1 * b1);
			System.out.println(a1 / b1);

			Profiler.visitNewTest(2001);
			System.out.println("-----------------------------------------");
			short a2 = 2, b2 = 10;
			System.out.println(a2 + b2);
			System.out.println(a2 - b2);
			System.out.println(a2 * b2);
			System.out.println(a2 / b2);
			
			Profiler.visitNewTest(2002);
			System.out.println("-----------------------------------------");
			float a3 = 2, b3 = 10;
			System.out.println(a3 + b3);
			System.out.println(a3 - b3);
			System.out.println(a3 * b3);
			System.out.println(a3 / b3);
			
			Profiler.visitNewTest(2003);
			System.out.println("-----------------------------------------");
			long a4 = 2, b4 = 10;
			System.out.println(a4 + b4);
			System.out.println(a4 - b4);
			System.out.println(a4 * b4);
			System.out.println(a4 / b4);
			
			Profiler.visitNewTest(2004);
			System.out.println("-----------------------------------------");
			double a5 = 2, b5 = 10;
			System.out.println(a5 + b5);
			System.out.println(a5 - b5);
			System.out.println(a5 * b5);
			System.out.println(a5 / b5);
			
			Profiler.visitNewTest(2005);
			System.out.println("-----------------------------------------");
			int c1 = 2;
			System.out.println(c1 + 10);
			System.out.println(c1 - 10);
			System.out.println(c1 * 10);
			System.out.println(c1 / 10);

			Profiler.visitNewTest(2006);
			System.out.println("-----------------------------------------");
			float c2 = 2;
			System.out.println(c2 + (c1 * 5));
			System.out.println(c2 - (c1 * 5));
			System.out.println(c2 * (c1 * 5));
			System.out.println(c2 / (c1 * 5));
			
			Profiler.visitNewTest(2007);
			System.out.println("-----------------------------------------");
			float c3 = 3;
			System.out.println(c3 + sqrt(c1 * 5));
			System.out.println(c3 - sqrt(c1 * 5));
			System.out.println(c3 * sqrt(c1 * 5));
			System.out.println(c3 / sqrt(c1 * 5));
			
			Profiler.visitNewTest(2008);
			System.out.println("-----------------------------------------");
			double c31 = 2;
			double c4 = 3;
			System.out.println(c4 + sqrt(c31 * 5));
			System.out.println(c4 - sqrt(c31 * 5));
			System.out.println(c4 * sqrt(c31 * 5));
			System.out.println(c4 / sqrt(c31 * 5));
			
			Profiler.visitNewTest(2009);
			System.out.println("-----------------------------------------");
			int[] array = new int[5];
			array[0] = 1;
			array[1] = 2;
			array[2] = 3;
			array[3] = 4;
			array[4] = 5;
			System.out.println(array[0] + sqrt(array[4] * 5));
			
			Profiler.visitNewTest(2010);
			System.out.println("-----------------------------------------");
			int d1 = 3;
			float d2 = 3;
			long d3 = 3;
			double d4 = 3;
			System.out.println(-d1);
			System.out.println(-d2);
			System.out.println(-d3);
			System.out.println(-d4);
			
			Profiler.visitNewTest(2011);
			System.out.println("-----------------------------------------");
			mid(1, 3, 2);
			(new Test2()).mid2(4, 6, 5);
			
			Profiler.visitNewTest(2012);
			System.out.println("-----------------------------------------");
			(new Test2()).mid3(3, "str1", 6, "str2", 5);
			
			
			int n = 5;
			Random r = new Random();
			for (int i = 0; i < n; i++) {
				Profiler.visitNewTest(2020+i);
				System.out.println("-----------------------------------------");
				int x = r.nextInt(n);
				int y = r.nextInt(n);
				int z = r.nextInt(n);
				mid(x, y, z);
			}
			
		}finally{
//			Profiler.stopProfiling();
		}
	}
	
	private static float sqrt(int s){
		return (float)Math.sqrt(s);
	}
	
	private static double sqrt(double s){
		return Math.sqrt(s);
	}
	
	public static int mid(int x, int y, int z) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				//////////
				m = y+1;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		return m;
	}
	
	private int mid2(int x, int y, int z) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				//////////
				m = y;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}
		return m;

		
	}
	
	private int mid3(int x, String str1, int y, String str2, int z) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				//////////
				m = y;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		return m;
	}
}