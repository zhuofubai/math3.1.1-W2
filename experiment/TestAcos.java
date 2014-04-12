package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.transform.DstNormalization;
import org.apache.commons.math3.transform.FastSineTransformer;
import org.apache.commons.math3.transform.TransformType;

import log.Logger;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.FastMath_bug;



public class TestAcos {
	
	public static int testOut(double truth, double result) {
		double tolerance = 1E-30;
		
		double sum = 0;
		double sum2=0;
		
		double ratio=Math.abs(result-truth)/truth;
		
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
		FastMath_bug fastmathBug=new FastMath_bug();
		FastSineTransformer transformer = new FastSineTransformer(
				DstNormalization.STANDARD_DST_I);
		double result;//, tolerance = 1E-12;
		double truth;
		
		int MAX=1;
		int MIN=-1;
		String filedir="TestFastMathCosh/Data4";
		fastmathBug.log.setDir(filedir);
		String filename=filedir+"/"+"out.txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		
		int count=0;
		for (int i = 0; i < 3000; i++) {
//			double y[]={Math.random()*5*Math.sin(a), Math.random()*5, Math.random()*5, Math.random()*5,
//					Math.random()*5, Math.random()*5, Math.random()*5, Math.random()*5};
			int k=2+(int)(Math.random()*(5-2+1));
			int datalength=(int)Math.pow(2, k);
			double y=Math.random();
			
			fastmathBug.log.setTestId(i);
			result = fastmathBug.acos(y);
			truth =FastMath.acos(y);
			int yout=testOut(truth,result);
			if (yout==1){count++;}
			out.write(i+" "+yout);
			out.write("\n");
			out.flush();
		}
		System.out.println("count is :"+count);
	}
}