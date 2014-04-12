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
package org.apache.commons.math3.transform;

import java.io.Serializable;

import log.Logger;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.DstNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.RealTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.transform.TransformUtils;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Implements the Fast Sine Transform for transformation of one-dimensional real
 * data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * <p>
 * There are several variants of the discrete sine transform. The present
 * implementation corresponds to DST-I, with various normalization conventions,
 * which are specified by the parameter {@link DstNormalization}.
 * <strong>It should be noted that regardless to the convention, the first
 * element of the dataset to be transformed must be zero.</strong>
 * <p>
 * DST-I is equivalent to DFT of an <em>odd extension</em> of the data series.
 * More precisely, if x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is the data set
 * to be sine transformed, the extended data set x<sub>0</sub><sup>&#35;</sup>,
 * &hellip;, x<sub>2N-1</sub><sup>&#35;</sup> is defined as follows
 * <ul>
 * <li>x<sub>0</sub><sup>&#35;</sup> = x<sub>0</sub> = 0,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>k</sub> if 1 &le; k &lt; N,</li>
 * <li>x<sub>N</sub><sup>&#35;</sup> = 0,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = -x<sub>2N-k</sub> if N + 1 &le; k &lt;
 * 2N.</li>
 * </ul>
 * <p>
 * Then, the standard DST-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of i (the pure imaginary number) times the N first elements of the DFT of the
 * extended data set x<sub>0</sub><sup>&#35;</sup>, &hellip;,
 * x<sub>2N-1</sub><sup>&#35;</sup> <br />
 * y<sub>n</sub> = (i / 2) &sum;<sub>k=0</sub><sup>2N-1</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * <p>
 * The present implementation of the discrete sine transform as a fast sine
 * transform requires the length of the data to be a power of two. Besides,
 * it implicitly assumes that the sampled function is odd. In particular, the
 * first element of the data set must be 0, which is enforced in
 * {@link #transform(UnivariateFunction, double, double, int, TransformType)},
 * after sampling.
 *
 * @version $Id: FastSineTransformer.java 1385310 2012-09-16 16:32:10Z tn $
 * @since 1.2
 */
public class FastSineTransformer_bug implements RealTransformer, Serializable {

    /** Serializable version identifier. */
    static final long serialVersionUID = 20120211L;

    /** The type of DST to be performed. */
    private final DstNormalization normalization;
    /**
     * The instrumented logger
     */
    public Logger log;
    /**
     * Creates a new instance of this class, with various normalization conventions.
     *
     * @param normalization the type of normalization to be applied to the transformed data
     */
    public FastSineTransformer_bug(final DstNormalization normalization) {
        this.normalization = normalization;
        log=new Logger(); 
    }
    
    /**
     * {@inheritDoc}
     *
     * The first element of the specified data set is required to be {@code 0}.
     *
     * @throws MathIllegalArgumentException if the length of the data array is
     *   not a power of two, or the first element of the data array is not zero
     */
    public double[] transform(final double[] f, final TransformType type) {
        if (normalization == DstNormalization.ORTHOGONAL_DST_I) {
            final double s = FastMath.sqrt(2.0 / f.length);
            return TransformUtils.scaleArray(fst(f), s);
        }
        if (type == TransformType.FORWARD) {
            return fst(f);
        }
        final double s = 2.0 / f.length;
        return TransformUtils.scaleArray(fst(f), s);
    }
    
    /**
     * {@inheritDoc}
     *
     * This implementation enforces {@code f(x) = 0.0} at {@code x = 0.0}.
     *
     * @throws org.apache.commons.math3.exception.NonMonotonicSequenceException
     *   if the lower bound is greater than, or equal to the upper bound
     * @throws org.apache.commons.math3.exception.NotStrictlyPositiveException
     *   if the number of sample points is negative
     * @throws MathIllegalArgumentException if the number of sample points is not a power of two
     */
    public double[] transform(final UnivariateFunction f,
        final double min, final double max, final int n,
        final TransformType type) {

        final double[] data = FunctionUtils.sample(f, min, max, n);
        data[0] = 0.0;
        return transform(data, type);
    }

    /**
     * Perform the FST algorithm (including inverse). The first element of the
     * data set is required to be {@code 0}.
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     *   not a power of two, or the first element of the data array is not zero
     */
    protected double[] fst(double[] f) throws MathIllegalArgumentException {

        final double[] transformed = new double[f.length];

        if (!ArithmeticUtils.isPowerOfTwo(f.length)) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING,
                    Integer.valueOf(f.length));
        }
        if (f[0] != 0.0) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.FIRST_ELEMENT_NOT_ZERO,
                    Double.valueOf(f[0]));
        }
        final int n = f.length;
        if (n == 1) {       // trivial case
            transformed[0] = 0.0;
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0.0;
        
        x[n >> 1] = 2.0 * f[n>>1];
        double []data1={x[n>>1], f[n>>1]};
        log.add(1, data1, "x[n>>1] f[n>>1]");//x[n>>1] f[n>>1] exp1
        double []data2={f[n>>1]};
        log.add(2, data2, "f[n>>1]");//f[n>>1] exp2
        double tempa=0;
        double tempb=0;
        int tempi=0;
        /*Bug2 change FastMath.PI/n to FastMath.PI/(n-1)
         * Bug 3 change 0.5 to 0.45 for b
         */
        /*Bug1 add a small value*/
        for (int i = 1; i < (n >> 1); i++) {
            final double a = FastMath.sin(i * FastMath.PI / n) * (f[i] + f[n - i]);
            final double b = 0.5* (f[i] - f[n - i]);
            x[i]     = a + b;
            x[n - i] = a - b;
            tempa=a;
            tempb=b;
            tempi=i;
        }
        
        double []data3={tempa, FastMath.sin(tempi * FastMath.PI / n),f[tempi] + f[n - tempi]};
        log.add(3, data3, "a");// a FastMath.sin(i*FastMath.PI/n)  (f[i]+f[n-i])
        double []data4={FastMath.sin(tempi * FastMath.PI / n), (double)tempi};// FastMath.sin(i * FastMath.PI / n) i
        log.add(4, data4, "FastMath.sin(temp * FastMath.PI / n)");
        double []data5={f[tempi] + f[n - tempi],f[tempi],f[n-tempi]};
        log.add(5, data5, "f[temp] + f[n - temp]");// (f[i] + f[n - i]) f[i] f[n-i]
        double []data6={f[tempi]};
        log.add(6, data6, "f[temp]");//f[i]
        double []data7={f[n-tempi]};
        log.add(7, data7, "f[n-temp]");//f[n-i]
        double []data8={x[tempi], tempa, tempb};
        log.add(8, data8, "x[i]");//x[i] a b
        double []data9={x[n-tempi], tempa, tempb};
        log.add(9, data9, "x[n-i]");//x[n-i] a b
        double []data10={tempa};
        log.add(10, data10, "a");//a
        double []data11={tempb,f[tempi],f[n-tempi]};
        log.add(11, data11, "b");//b
        FastFourierTransformer transformer;
        transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] y = transformer.transform(x, TransformType.FORWARD);

        // reconstruct the FST result for the original array
        transformed[0] = 0.0;
        /*Bug 1 add a small number 0.03 to transformed[1]  */
        transformed[1] = 0.5 * y[0].getReal();
        double []data12={transformed[1], y[0].getReal()};
        log.add(12, data12, "transformed[1]");//transformed[1] y[0].getReal()
        double []data13={y[0].getReal()};
        log.add(13, data13, "y[0].getReal()");//y[0].getReal()
        for (int i = 1; i < (n >> 1); i++) {
        	 /*Bug 4 add abs to y[i].getImaginary  */
            transformed[2 * i]     = -y[i].getImaginary();
          /*Bug 5 change 2*i-1 to 2*i           */
            transformed[2 * i + 1] = y[i].getReal() + transformed[2 * i-1 ];
            tempi=i;
        }
        double []data14={transformed[2*tempi]};
        log.add(14, data14, "transformed[2*i]");// transformed[2*i]  -y[i].getImaginary();
        double []data15={transformed[2*tempi+1],  y[tempi].getReal(),  transformed[2 * tempi-1 ]};// transformed[2*i+1]  y[i].getReal()  transformed[2 * i - 1]
        log.add(15, data15, "transformed[2*ti+1]");
        double []data16={y[tempi].getReal()};
        log.add(16, data16, "y[i].getReal()");// y[i].getReal()
        double []data17={transformed[2 * tempi-1]};
        log.add(17, data17, "transformed[2 * i - 1]");// transformed[2 * i - 1]
        log.logFile();
        log.clear();
        return transformed;
    }
}
