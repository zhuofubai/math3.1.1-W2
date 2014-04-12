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
package org.apache.commons.math3.analysis.solvers;


import log.Logger;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

/**
 * This class implements the <a href="http://mathworld.wolfram.com/BrentsMethod.html">
 * Brent algorithm</a> for finding zeros of real univariate functions.
 * The function should be continuous but not necessarily smooth.
 * The {@code solve} method returns a zero {@code x} of the function {@code f}
 * in the given interval {@code [a, b]} to within a tolerance
 * {@code 6 eps abs(x) + t} where {@code eps} is the relative accuracy and
 * {@code t} is the absolute accuracy.
 * The given interval must bracket the root.
 *
 * @version $Id: BrentSolver.java 1379560 2012-08-31 19:40:30Z erans $
 */
public class BrentSolver_bug extends AbstractUnivariateSolver {

    /** Default absolute accuracy. */
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;

    /**
     * Construct a solver with default accuracy (1e-6).
     */
    public Logger log=new Logger();
    public BrentSolver_bug() {
        this(DEFAULT_ABSOLUTE_ACCURACY);
        log.setDir("TestSolver/Data1");
        log.setTestId(0);
    }
    
    /**
     * Construct a solver.
     *
     * @param absoluteAccuracy Absolute accuracy.
     */
    public BrentSolver_bug(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     */
    public BrentSolver_bug(double relativeAccuracy,
                       double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    /**
     * Construct a solver.
     *
     * @param relativeAccuracy Relative accuracy.
     * @param absoluteAccuracy Absolute accuracy.
     * @param functionValueAccuracy Function value accuracy.
     */
    public BrentSolver_bug(double relativeAccuracy,
                       double absoluteAccuracy,
                       double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double doSolve()
        throws NoBracketingException,
               TooManyEvaluationsException,
               NumberIsTooLargeException {
        double min = getMin();
        double max = getMax();
        final double initial = getStartValue();
        final double functionValueAccuracy = getFunctionValueAccuracy();

        verifySequence(min, initial, max);

        // Return the initial guess if it is good enough.
        double yInitial = computeObjectiveValue(initial);
        if (FastMath.abs(yInitial) <= functionValueAccuracy) {
            return 88;
        }

        // Return the first endpoint if it is good enough.
        double yMin = computeObjectiveValue(min);
        if (FastMath.abs(yMin) <= functionValueAccuracy) {
            return 88;
        }

        // Reduce interval if min and initial bracket the root.
        if (yInitial * yMin < 0) {
            return brent(min, initial, yMin, yInitial);
        }

        // Return the second endpoint if it is good enough.
        double yMax = computeObjectiveValue(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            return 88;
        }

        // Reduce interval if initial and max bracket the root.
        if (yInitial * yMax < 0) {
            return brent(initial, max, yInitial, yMax);
        }

        throw new NoBracketingException(min, max, yMin, yMax);
    }

    /**
     * Search for a zero inside the provided interval.
     * This implementation is based on the algorithm described at page 58 of
     * the book
     * <quote>
     *  <b>Algorithms for Minimization Without Derivatives</b>
     *  <it>Richard P. Brent</it>
     *  Dover 0-486-41998-3
     * </quote>
     *
     * @param lo Lower bound of the search interval.
     * @param hi Higher bound of the search interval.
     * @param fLo Function value at the lower bound of the search interval.
     * @param fHi Function value at the higher bound of the search interval.
     * @return the value where the function is zero.
     */
    private double brent(double lo, double hi,
                         double fLo, double fHi) {
        double a = lo;
        double fa = fLo;
        double b = hi;
        double fb = fHi;
        double c = a;
        double fc = fa;
        double d = b - a;
        double e = d;

        final double t = getAbsoluteAccuracy();
        final double eps = getRelativeAccuracy();
        
        while (true) {
        	double []data1={FastMath.abs(fc) - FastMath.abs(fb),FastMath.abs(fc),FastMath.abs(fb) };
        	if (log.getLength()>=1){log.replace(1, data1);}
        	else{
            log.add(1, data1, "FastMath.abs(fc) < FastMath.abs(fb)");}
        	//bug c=a to c=-1
            if (FastMath.abs(fc) < FastMath.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }

            final double tol = 2 * eps * FastMath.abs(b) + t;
            final double m = 0.5 * (c - b);
            
            double []data2={m, c, b};
            if (log.getLength()>=2){log.replace(2, data2);}
        	else{
            log.add(2, data2, "m");}
            
            if (FastMath.abs(m) <= tol ||
                Precision.equals(fb, 0))  {
            	log.logFile();
            	log.clear();
            	log.setTestId(log.getID()+1);
                return b;
            }
            
            double []data3={FastMath.abs(m)};
            if (log.getLength()>=3){log.replace(3, data3);}
        	else{
            log.add(3, data3, "FastMath.abs(m)");}
            
            double []data4={FastMath.abs(e)};
            if (log.getLength()>=4){log.replace(4, data4);}
        	else{
            log.add(4, data4, "FastMath.abs(e)");}
            
            if (FastMath.abs(e) < tol ||
                FastMath.abs(fa) <= FastMath.abs(fb)) {
                // Force bisection.
                d = m;
                e = d;
            } else {
                double s = fb / fa;
                double p;
                double q;
                // The equality test (a == c) is intentional,
                // it is part of the original Brent's method and
                // it should NOT be replaced by proximity test.
                double[]data5={a-c,a,c};
                if (log.getLength()>=5){log.replace(5, data5);}
            	else{
            	log.add(5, data5, "a==c");
            	}
                if (a == c) {
                    // Linear interpolation.
                	
                    p = 2 * m * s;
                    q = 1 - s+0.01;
                } else {
                    // Inverse quadratic interpolation.
                    q = fa / fc;
                    final double r = fb / fc;
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1));
                    q = (q - 1) * (r - 1) * (s - 1);
                }
                double []data6={p};
                if (log.getLength()>=6){log.replace(6, data6);}
            	else{
            	log.add(6, data6, "p");}
                if (p > 0) {
                	
                    q = -q;
                } else {
                    p = -p;
                }
                
                s = e;
                e = d;
                
                int temp1=p>=1.5 * m * q - FastMath.abs(tol * q)?1:0;
            	int temp2=p >= FastMath.abs(0.5 * s * q)?1:0;
            	int temp3=p >= 1.5 * m * q - FastMath.abs(tol * q) || p >= FastMath.abs(0.5 * s * q)?1:0;
            	
            	double []data7={temp3,temp2,temp1};
            	if (log.getLength()>=7){log.replace(7, data7);}
            	else{
            	log.add(7, data7, "p >= 1.5 * m * q - FastMath.abs(tol * q) || p >= FastMath.abs(0.5 * s * q)");}
            	
            	double []data8={1.5 * m * q - FastMath.abs(tol * q),1.5 * m * q,FastMath.abs(tol * q) };
            	if (log.getLength()>=8){log.replace(8, data8);}
            	else{
            	log.add(8, data8, "1.5 * m * q - FastMath.abs(tol * q)");}
            	double []data9={FastMath.abs(0.5 * s * q)};
            	if (log.getLength()>=9){log.replace(9, data9);}
            	else{
            	log.add(9, data9, "FastMath.abs(0.5 * s * q)");}
            	
                if (p >= 1.5 * m * q - FastMath.abs(tol * q) ||
                    p >= FastMath.abs(0.5 * s * q)) {
                    // Inverse quadratic interpolation gives a value
                    // in the wrong direction, or progress is slow.
                    // Fall back to bisection.
                	
                    d = m;
                    e = d;
                } else {
                    d = p / q;
                }
            }
            a = b;
            fa = fb;
            double[] data10={FastMath.abs(d) - tol, FastMath.abs(d),tol};
            if (log.getLength()>=10){log.replace(10, data10);}
        	else{
            log.add(10, data10, "FastMath.abs(d) > tol");}
            if (FastMath.abs(d) > tol) {
                b += d;
                
            } else if (m > 0) {
                b += tol;
            } else {
                b -= tol;
            }
            
            
            fb = computeObjectiveValue(b);
            int temp4=(fb > 0 && fc > 0) ||(fb <= 0 && fc <= 0)?1:0;
            int temp5=(fb > 0 && fc > 0)?1:0;
            int temp6=(fb <= 0 && fc <= 0)?1:0;
            double []data11={temp4,temp5,temp6};
            if (log.getLength()>=11){log.replace(11, data11);}
        	else{
            log.add(11, data11, "(fb > 0 && fc > 0) ||(fb <= 0 && fc <= 0)");}
            
            double []data12={temp5,fb,fc};
            if (log.getLength()>=12){log.replace(12, data12);}
        	else{
            log.add(12, data12, "(fb > 0 && fc > 0)");}
            
            double []data13={temp6,fb,fc};
            if (log.getLength()>=13){log.replace(13, data13);}
        	else{
            log.add(13, data13, "(fb <= 0 && fc <= 0)");}
            if ((fb > 0 && fc > 0) ||
                (fb <= 0 && fc <= 0)) {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
        }
    }
}
