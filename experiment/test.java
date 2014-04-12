package experiment;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class test {
	double a=1;// TODO Auto-generated constructor stub
	double b=5;
	foo_1 f=new foo_1();
	public test() {
		
//		test12 u=new test12();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Profiler.visitNewTest(0);
		// TODO Auto-generated method stub
		double ta=3.24,tb=2.31,tc=7.86,td=5.12;
		int te=2;
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
		  ta=tc*tb/tc;
		  ta=td+Math.abs(ta+tc-td-te*tb)+tb;
		  System.out.println(ta);
		  
		 Profiler.stopProfiling();
	}

}
