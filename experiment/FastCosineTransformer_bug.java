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
package experiment;

import java.io.Serializable;

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
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup>
 * is defined as follows
 * <ul>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>k</sub> if 0 &le; k &lt; N,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>2N-2-k</sub>
 * if N &le; k &lt; 2N - 2.</li>
 * </ul>
 * <p>
 * Then, the standard DCT-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of the N first elements of the DFT of the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup>
 * <br/>
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
public class FastCosineTransformer_bug implements RealTransformer, Serializable {

    /** Serializable version identifier. */
    static final long serialVersionUID = 20120212L;

    /** The type of DCT to be performed. */
    private final DctNormalization normalization;
    /**
     * The instrumented logger
     */
    public Logger log;
    /**
     * Creates a new instance of this class, with various normalization
     * conventions.
     *
     * @param normalization the type of normalization to be applied to the
     * transformed data
     */
    public FastCosineTransformer_bug(final DctNormalization normalization) {
        this.normalization = normalization;
        log=new Logger(); 
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two plus one
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
     * if the lower bound is greater than, or equal to the upper bound
     * @throws org.apache.commons.math3.exception.NotStrictlyPositiveException
     * if the number of sample points is negative
     * @throws MathIllegalArgumentException if the number of sample points is
     * not a power of two plus one
     */
    public double[] transform(final UnivariateFunction f,
        final double min, final double max, final int n,
        final TransformType type) throws MathIllegalArgumentException {

        final double[] data = FunctionUtils.sample(f, min, max, n);
        return transform(data, type);
    }

    /**
     * Perform the FCT algorithm (including inverse).
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two plus one
     */
    protected double[] fct(double[] f)
        throws MathIllegalArgumentException {

        final double[] transformed = new double[f.length];

        final int n = f.length - 1;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(
                LocalizedFormats.NOT_POWER_OF_TWO_PLUS_ONE,
                Integer.valueOf(f.length));
        }
        if (n == 1) {       // trivial case
            transformed[0] = 0.5 * (f[0] + f[1]);
            transformed[1] = 0.5 * (f[0] - f[1]);
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0.5 * (f[0] + f[n]);
        String funname="cosh/";
        double tempexpression=0;
        double[] data1={x[0],f[0],f[n]};
        String expressionname="x[0]";
        log.add(1,data1, funname+expressionname);
        
        x[n >> 1] = f[n >> 1];
        
        double[] data2={x[n>>1],f[n>>1]};
         expressionname="x[n>>1]";
        log.add(2,data2, funname+expressionname);
        // temporary variable for transformed[1]
        double t1 = 0.5 * (f[0] - f[n]);
        double[] data3={t1,f[0],f[n]};
        expressionname="t1";
       log.add(3,data3, funname+expressionname);
       
       double[] data4={f[0]-f[n],f[0],f[n]};
       expressionname="f[0]-f[n]";
       log.add(4,data4, funname+expressionname);
      
        for (int i = 1; i < (n >> 1); i++) {
            final double a = 0.5 * (f[i] + f[n - i]);
            
            double[] data5={a, f[i],f[n-i]};
            expressionname="a";
            log.add(5,data5, funname+expressionname);
            
            final double b = FastMath.sin(i * FastMath.PI / n) * (f[i] - f[n - i]);
            
            double[] data6={b, FastMath.sin(i * FastMath.PI / n),(f[i] - f[n - i])};
            expressionname="b";
            log.add(6,data6, funname+expressionname);
            /*****bug2 store in Data2 FastMath.sin(i * FastMath.PI / n) to FastMath.sin(2*i * FastMath.PI / n)*******/
            double[] data7={FastMath.sin(i * FastMath.PI / n), i * FastMath.PI,n};
            expressionname="FastMath.sin(i * FastMath.PI / n)";
            log.add(7,data7, funname+expressionname);
            
            double[] data8={(f[i] - f[n - i]), f[i],f[n-i]};
            expressionname="f[i] - f[n - i]";
            log.add(8,data8, funname+expressionname);
            
            
            
            final double c = FastMath.cos(i * FastMath.PI / n) * (f[i] - f[n - i]);
            
            double[] data9={c, FastMath.cos(i * FastMath.PI / n) , (f[i] - f[n - i])};
            expressionname="c";
            log.add(9,data9, funname+expressionname);
            
            double[] data10={FastMath.cos(i * FastMath.PI / n) , i * FastMath.PI,n};
            expressionname="FastMath.cos(i * FastMath.PI / n)";
            log.add(10,data10, funname+expressionname);
            
            double[] data11={ (f[i] - f[n - i]), f[i] , f[n - i]};
            expressionname="c";
            log.add(11,data11, funname+expressionname);
            
            x[i] = a + b;
            
            double[] data12={ x[i], a , b};
            expressionname="x[i]";
            log.add(12,data12, funname+expressionname);
            
            x[n - i] = a - b;
            
            double[] data13={ x[n-i], a , b};
            expressionname="x[n-i]";
            log.add(13,data13, funname+expressionname);
            
            tempexpression=t1;
            
            t1 += c;
            
            double[] data14={ t1, tempexpression , c};
            expressionname="t1";
            log.add(14,data14, funname+expressionname);
            
        }
        

       
        FastFourierTransformer transformer;
        transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] y = transformer.transform(x, TransformType.FORWARD);

        // reconstruct the FCT result for the original array
        transformed[0] = y[0].getReal();
        
        double[] data15={ transformed[0]};
        expressionname="transformed[0]";
        log.add(15,data15, funname+expressionname);
        
        transformed[1] = t1;
        
        double[] data16={ transformed[1]};
        expressionname="transformed[1]";
        log.add(16,data16, funname+expressionname);
        
        for (int i = 1; i < (n >> 1); i++) {
        	
            transformed[2 * i]     = y[i].getReal();
            
            double[] data17={ transformed[2*i]};
            expressionname="transformed[2 * i]";
            log.add(17,data17, funname+expressionname);
            /***bug 1, store in Data1, add Math.abs() on transformed[2 * i - 1] - y[i].getImaginary()***/
            
            transformed[2 * i + 1] = transformed[2 * i - 1] - y[i].getImaginary();
            
            double[] data18={ transformed[2*i+1] , transformed[2 * i - 1] , y[i].getImaginary()};
            expressionname="transformed[2 * i+1]";
            log.add(18,data18, funname+expressionname);
            
            double[] data20={transformed[2 * i - 1] - y[i].getImaginary(),transformed[2 * i - 1] , y[i].getImaginary()};
            expressionname="transformed[2 * i - 1] - y[i].getImaginary()";
            log.add(20,data20, funname+expressionname);
        }
        
        transformed[n] = y[n >> 1].getReal();
        
        double[] data19={ transformed[n] };
        expressionname="transformed[n]";
        log.add(19,data19, funname+expressionname);
        
        log.logFile();
        log.clear();
        return transformed;
    }
}
