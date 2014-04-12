package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.math3.distribution.PascalDistribution;

public class TestPascalDistribution {

	public TestPascalDistribution() {
		// TODO Auto-generated constructor stub
	}
	public static int testOut(double truth, double result) {
		double tolerance = 1E-4;
		double ratio=Math.abs(truth-result)/Math.abs(truth);
		
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
		 PascalDistribution dist;
		 PascalDistribution_bug dist2;
		 String filedir="TestPascalDistributio/Data2";
		 String filename=filedir+"/"+"out.txt";
	     BufferedWriter out = new BufferedWriter(new FileWriter(filename));
	     double truth=0,result=0;
		 int r=0;
		 double p=0;
		 int count=0;
		 Random generator = new Random();
		 double flag=0;
		 for(int i=0;i<8000;i++){
			 flag=Math.random();
			 if(flag>0.5){
				 p=0.5+(Math.random()-0.5)*0.0001;
			 }else{
					 p=Math.random();}
			 r = Math.abs(generator.nextInt(15)+1);
	        dist = new PascalDistribution(r, p);
	        dist2 = new PascalDistribution_bug(r,p,i,filedir);
	        
			//dist2.log.setDir(filedir);
			truth=dist.getNumericalVariance();
			result=dist2.getNumericalVariance();
			int yout=testOut(truth,result);
			if (yout==1){count++;}
			out.write(i+" "+yout);
			out.write("\n");
			out.flush();
		 }
		 System.out.println("count is :"+count);
	}

}
