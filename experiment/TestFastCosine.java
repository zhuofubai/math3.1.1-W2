/**
 * 
 */
package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.transform.DctNormalization;
import org.apache.commons.math3.transform.FastCosineTransformer;
import org.apache.commons.math3.transform.TransformType;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

/**
 * @author Zhuofu
 * 
 */
public class TestFastCosine {

	/**
	 * 
	 */
	public TestFastCosine() {
		// TODO Auto-generated constructor stub
	}

	public static int testOut(double[] truth, double[] result) {
		double tolerance = 0;
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
	public static void main(String[] args){
		
		try{
			Profiler.visitNewTest(-1);
			// TODO Auto-generated method stub
			FastCosineTransformer_bug2 transformer_bug = new FastCosineTransformer_bug2(
					DctNormalization.STANDARD_DCT_I);
			FastCosineTransformer transformer = new FastCosineTransformer(
					DctNormalization.STANDARD_DCT_I);
			double result[];//, tolerance = 1E-12;
			double truth[];

			int MAX=1;
			int MIN=-1;
			String filedir="TestCosineTransformData/Data3";
//			transformer_bug.log.setDir(filedir);
			String filename=filedir+"/"+"out.txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			
			int count=0;
			
			for (int i = 0; i < 1000; i++) {
				Profiler.visitNewTest(i);			
//				double y[]={Math.random()*5*Math.sin(a), Math.random()*5, Math.random()*5, Math.random()*5,
//						Math.random()*5, Math.random()*5, Math.random()*5, Math.random()*5};
				int k=2+(int)(Math.random()*(5-2+1));
				int datalength=(int)Math.pow(2, k)+1;
				double []y=new double[datalength];
				for(int j=1;j<datalength;j++){
					if (j==1)
					{y[j]=0.0;}
					else
					{y[j]=MIN+Math.random()*(MAX-MIN+1);
					}
				}
				//transformer_bug.log.setTestId(i);
				result = transformer_bug.transform(y, TransformType.FORWARD);
				truth =transformer.transform(y, TransformType.FORWARD);
				int yout=TestFastCosine.testOut(truth,result);
				if (yout==1){count++;}
				out.write(i+" "+yout);
				out.write("\n");
				out.flush();
			}
			Profiler.visitNewTest(1001);	
			Test2.main();
			
			 
			Profiler.stopProfiling();
			System.out.println("count is :"+count);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
