/**
 * 
 */
package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.transform.DstNormalization;
import org.apache.commons.math3.transform.FastSineTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 * @author Zhuofu
 * 
 */
public class TestFastSine {

	/**
	 * 
	 */
	public TestFastSine() {
		// TODO Auto-generated constructor stub
	}

	public static int testOut(double[] truth, double[] result) {
		double tolerance = 1E-3;
		if (truth.length != result.length) {
			System.out
					.println("two inputs' dimension is mismatch, the result length is: "
							+ result.length);
			return 1;
		}
		double sum = 0;
		double sum2=0;
		for (int i = 0; i < truth.length; i++) {
			sum = sum + Math.abs(truth[i] - result[i]);
			sum2=sum2+Math.abs(truth[i]);
		}
		double ratio=sum/sum2;
		
		if (ratio > tolerance) {
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
		FastSineTransformer_bug transformer_bug = new FastSineTransformer_bug(
				DstNormalization.STANDARD_DST_I);
		FastSineTransformer transformer = new FastSineTransformer(
				DstNormalization.STANDARD_DST_I);
		double result[];//, tolerance = 1E-12;
		double truth[];

		int MAX=1;
		int MIN=-1;
		String filedir="TestSineTransformData/Data2";
		transformer_bug.log.setDir(filedir);
		String filename=filedir+"/"+"out.txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		
		int count=0;
		for (int i = 0; i < 5000; i++) {
//			double y[]={Math.random()*5*Math.sin(a), Math.random()*5, Math.random()*5, Math.random()*5,
//					Math.random()*5, Math.random()*5, Math.random()*5, Math.random()*5};
			int k=2+(int)(Math.random()*(5-2+1));
			int datalength=(int)Math.pow(2, k);
			double []y=new double[datalength];
			for(int j=1;j<datalength;j++){
				if (j==1)
				{y[j]=0.0;}
				else
				{y[j]=MIN+Math.random()*(MAX-MIN+1);
				}
			}
			transformer_bug.log.setTestId(i);
			result = transformer_bug.transform(y, TransformType.FORWARD);
			truth =transformer.transform(y, TransformType.FORWARD);
			int yout=TestFastSine.testOut(truth,result);
			if (yout==1){count++;}
			out.write(i+" "+yout);
			out.write("\n");
			out.flush();
		}
		System.out.println("count is :"+count);
	}

}
