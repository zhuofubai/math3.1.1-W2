/**
 * 
 */
package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver_bug;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;


/**
 * @author Zhuofu
 *
 */
public class TestBrentSolver {

	/**
	 * 
	 */
	public TestBrentSolver() {
		// TODO Auto-generated constructor stub
	}
	public static int testOut(double truth, double result) {
		double tolerance = 1E-1;
		
		double ratio=Math.abs((truth-result)/truth);
		
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
		
		UnivariateFunction f = new QuinticFunction();
        
        // Brent-Dekker solver.
        UnivariateSolver solver = new BrentSolver();// TODO Auto-generated method stub
        UnivariateSolver solver_bug = new BrentSolver_bug();
        String filedir="TestSolver/Data1";
		//solver_bug.log.setDir(filedir);
		String filename=filedir+"/"+"out.txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		double min=0,max=0;
		double truth=0,result=0;
		int count=0;
		double[] minset={-0.2,-0.1,-0.3,0.3,0.2,0.05,0.85,0.8,0.85,0.55,0.85,0.8,0.5,0.45,-0.15,-0.05};
		double[] maxset={0.2,0.3,0.45,0.7,0.6,0.85,1.25,1.2,1.75,1.45,5,4.5,1.4,1.35,0.25,0.35};
		for (int i = 0; i < minset.length; i++) {
			
			//System.out.println(i);
			min=minset[i];
			max=maxset[i];
			truth=solver.solve(100, f, min, max);
			result=solver_bug.solve(1000,f,min,max);
			if(result==88){}else{
			int yout=TestBrentSolver.testOut(truth,result);
			if (yout==1){count++;}
			out.write(i+" "+yout);
			out.write("\n");
			out.flush();}
		}
		for (int i = 0; i < minset.length; i++) {
			//System.out.println(i);
			min=minset[i]+0.05;
			max=maxset[i]+0.05;
			truth=solver.solve(100, f, min, max);
			result=solver_bug.solve(1000,f,min,max);
			if(result==88){}else{
				int yout=TestBrentSolver.testOut(truth,result);
				if (yout==1){count++;}
				out.write(i+" "+yout);
				out.write("\n");
				out.flush();}
		}
		for (int i = 0; i < minset.length; i++) {
			//System.out.println(i);
			min=minset[i]-0.1;
			max=maxset[i]-0.1;
			truth=solver.solve(100, f, min, max);
			result=solver_bug.solve(1000,f,min,max);
			if(result==88){}else{
				int yout=TestBrentSolver.testOut(truth,result);
				if (yout==1){count++;}
				out.write(i+" "+yout);
				out.write("\n");
				out.flush();}
		}
		System.out.println("countis"+count);
	}

}
