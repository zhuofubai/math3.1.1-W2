/**
 * 
 */
package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator_bug;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.junit.Assert;

/**
 * @author Zhuofu
 * 
 */
public class TestSplineInterpolator {

	/**
	 * 
	 */
	public TestSplineInterpolator() {
		// TODO Auto-generated constructor stub
	}

	public static int testOut(double[] truth, double[] result,
			UnivariateFunction f1, UnivariateFunction f2) {
		double tolerance = 1E-10;
		if (truth.length != result.length) {
			System.out
					.println("two inputs' dimension is mismatch, the result length is: "
							+ result.length);
			
			return 1;
		}
		double sum = 0;
		double diff=0;
		double a=0;
		double b=0;
		double sum2=0;
		for (int i = 0; i < truth.length; i++) {
			double a1=truth[0]+Math.random()*(truth[truth.length-1]-truth[0]);
			sum = sum + Math.pow((f1.value(a1)-f2.value(a1)),2);
			sum2= sum2+Math.pow(f1.value(a1),2);
//			a=a+Math.pow(a, 2);
//			b=b+Math.pow(a, 2);
		}
		double ratio=Math.sqrt(sum)/Math.sqrt(sum2);
		if (ratio > tolerance){
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int MAX=2;
		int MIN=0;
		String filedir="TestSplineInterpolatorData/Data1";
		SplineInterpolator a = new SplineInterpolator();
		SplineInterpolator_bug b = new SplineInterpolator_bug();
		b.log.setDir(filedir);
		String filename=filedir+"/"+"out.txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		double temp=0;
		double temp2=0;
		int count=0;
		for (int i=0;i<3000;i++){
			b.log.setTestId(i);
			int k=2+(int)(Math.random()*(5-2+1));
			int datalength=(int)Math.pow(2, k);
			double []y=new double[datalength];
			double []x=new double[datalength];
			
			for(int j=1;j<datalength;j++){
				
				temp=MIN+Math.random()*(MAX-MIN+1);
				temp2=MIN+Math.random()*(MAX-MIN+1);
				y[j]=temp2;
				x[j]=temp;
			}
			Arrays.sort(x);
			Arrays.sort(y);
		
		
		UnivariateFunction f1 = a.interpolate(x, y);
		UnivariateFunction f2 = b.interpolate(x, y);
		int yout = testOut(x, y, f1, f2);
		if (yout==1){count++;}
		out.write(i+" "+yout);
		out.write("\n");
		out.flush();}
		System.out.println("count is "+ count);
	}

}
