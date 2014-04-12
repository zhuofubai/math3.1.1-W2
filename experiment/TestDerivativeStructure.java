package experiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import org.apache.commons.math3.analysis.differentiation.DSCompiler;
import org.apache.commons.math3.analysis.differentiation.DSCompiler_bug_tem;
import org.apache.commons.math3.analysis.differentiation.DSCompiler_bug3;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure_bug;
//import org.apache.commons.math3.analysis.differentiation.DerivativeStructure_bug1;

//import org.apache.commons.math.linear.EigenDecomposition;
//import org.apache.commons.math.linear.EigenDecompositionImpl;
//import org.apache.commons.math.linear.EigenDecompositionImpl_bug1;
//import org.apache.commons.math.linear.RealMatrix;
//import org.apache.commons.math.util.MathUtils;


import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class TestDerivativeStructure {
	static Random r = new Random(17899l);// 17899l
	static int maxOrder=3;
	public TestDerivativeStructure() {
		// TODO Auto-generated constructor stub
	}

	public static int testOut(double truth, double result, double tolerance) {
		double diff=Math.abs(truth-result);
		if (diff>tolerance){
			return 1;
		}else{
			return 0;
		}
		//double tolerance = 1E-12;// 5E-2;
//		if (truth.length != result.length) {
//			System.out
//					.println("two inputs' dimension is mismatch, the result length is: "
//							+ result.length);
//			return 1;
//		}
//		double sum = 0;
//		double sum2 = 0;
//		for (int i = 0; i < truth.length; i++) {
//			sum = sum + Math.abs(Math.abs(truth[i]) - Math.abs(result[i]));
//			sum2 = sum2 + Math.abs(truth[i]);
//		}
//		double ratio = sum / sum2;
//
//		if (ratio > tolerance) {
//			return 1;
//		} else {
//			return 0;
//		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Profiler.visitNewTest(-1);
		try {
			int count = 0;
			int count2 = 0;
			String filedir = "TestDSCompiler/out";
			String filename = filedir + "/" + "out.txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(filename,
					false));
			// double[] mainTridiagonal = { 0.5, 1, 1, 2, 0.5, 2, 13 };
			// double[] secondaryTridiagonal = { -4, 2, 5, 2, 0.7, 0.0002 };
			
			
			double truth=0;
			double result=0;
			int u1,u2;
			int yout=0;
			for (int i = 0; i < 10000; i++) {
				Profiler.visitNewTest(i);
				double x=r.nextDouble()+0.1;
				double y=r.nextDouble()+0.1;
				double z=r.nextDouble()+0.1;
				int order=r.nextInt(3)+1;
				
				int u = r.nextInt(4) + 1;
				//u=1;
				switch(u){
				case 1:
					u1=r.nextInt(2);
					u2=r.nextInt(4)+1;
					truth = testExpression(x,y,z,u1,u2);
					result = testExpression_bug(x,y,z,u1,u2);
					double epsilon = 2.5e-13;
					yout=testOut(truth,result,epsilon);
					break;
				case 2:
					u1=r.nextInt(3)+1;
					u2=r.nextInt(4)+1;
					truth = testTrigo(x,y,z,u1,u2);
					result = testTrigo_bug(x,y,z,u1,u2);
					double epsilon2 = 2.0e-12;
					yout=testOut(truth,result,epsilon2);
					//System.out.println(u1+" "+u2);
					break;
				case 3:
					int orderx=r.nextInt(4);
					int ordery=r.nextInt(4);
					while(orderx+ordery>=maxOrder){
						orderx=r.nextInt(4);
						ordery=r.nextInt(4);
					}
					
					truth = testTaylorAtan2(x,y,orderx,ordery);
					result = testTaylorAtan2_bug(x,y,orderx,ordery);
					 double[] epsilon3 = new double[] {0.00214, 0.000241, 0.0000422, 6.48e-6, 8.04e-7};//{ 5.0e-16, 3.0e-15, 2.2e-14, 1.0e-12, 8.0e-11 };
					yout=testOut(truth,result,epsilon3[orderx+ordery]);
					break;
				case 4:
					truth = testTrigo2(x,order);
					result = testTrigo2_bug(x,order);
					double epsilon4=1e-12;
					yout=testOut(truth,result,epsilon4);
					break;
				}
				
			//	System.out.println(truth+"   "+result);
				
				// int yout = testOut(1, 1);
				
				 if (yout == 1) {
					// System.out.println("case"+i);
				 count++;
				 }
				out.write(i + " " + yout);
				out.write("\n");
				out.flush();
				count2++;
			}

			System.out.println("count is :" + count + "  count2 is" + count2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	public static double testExpression(double x, double y, double z,int u1,int u2) {
		
		double result=0;
		DerivativeStructure dsX = new DerivativeStructure(3, 5, 0, x);

		DerivativeStructure dsY = new DerivativeStructure(3, 5, 1, y);

		DerivativeStructure dsZ = new DerivativeStructure(3, 5, 2, z);

		
		
		// f(x, y, z) += x + 5 x y - 2 z + (8 z x - y)^3
		DerivativeStructure ds = new DerivativeStructure(1, dsX, 5,
				dsX.multiply(dsY), -2, dsZ, 1, new DerivativeStructure(8,
						dsZ.multiply(dsX), -1, dsY).pow(3)).subtract(dsY.log().add(dsZ.log1p()).divide(dsX.exp())).add(dsX.pow(2).add(dsY.pow(2)).cbrt());
//		else{
//		 ds = new DerivativeStructure(1, dsX, 5,
//				dsX.multiply(dsY), -2, dsZ).add(new DerivativeStructure(8, dsZ
//				.multiply(dsX), -1, dsY).pow(3)).add(dsZ.tan().divide(dsX.cbrt()));}
//		
		
		switch(u2){
		case 1:
			result=ds.getValue();break;
		case 2:
			result=ds.getPartialDerivative(1, 0, 0);break;
		case 3:
			result=ds.getPartialDerivative(1, 1, 0);break;
		case 4:	
			result=ds.getPartialDerivative(1, 1, 1);break;
		}
		return result;
//		double f = x + 5 * x * y - 2 * z + FastMath.pow(8 * z * x - y, 3);
//		Assert.assertEquals(f, ds.getValue(), FastMath.abs(epsilon * f));
//		Assert.assertEquals(f, dsOther.getValue(), FastMath.abs(epsilon * f));
//
//		// df/dx = 1 + 5 y + 24 (8 z x - y)^2 z
//		double dfdx = 1 + 5 * y + 24 * z * FastMath.pow(8 * z * x - y, 2);
//		Assert.assertEquals(dfdx, ds.getPartialDerivative(1, 0, 0),
//				FastMath.abs(epsilon * dfdx));
//		Assert.assertEquals(dfdx, dsOther.getPartialDerivative(1, 0, 0),
//				FastMath.abs(epsilon * dfdx));
//
//		// df/dxdy = 5 + 48 z*(y - 8 z x)
//		double dfdxdy = 5 + 48 * z * (y - 8 * z * x);
//		Assert.assertEquals(dfdxdy, ds.getPartialDerivative(1, 1, 0),
//				FastMath.abs(epsilon * dfdxdy));
//		Assert.assertEquals(dfdxdy, dsOther.getPartialDerivative(1, 1, 0),
//				FastMath.abs(epsilon * dfdxdy));
//
//		// df/dxdydz = 48 (y - 16 z x)
//		double dfdxdydz = 48 * (y - 16 * z * x);
//		Assert.assertEquals(dfdxdydz, ds.getPartialDerivative(1, 1, 1),
//				FastMath.abs(epsilon * dfdxdydz));
//		Assert.assertEquals(dfdxdydz, dsOther.getPartialDerivative(1, 1, 1),
//				FastMath.abs(epsilon * dfdxdydz));

	}

	public static double testTrigo(double x, double y, double z,int u1, int u2) {
		
		double result = 0;
		DerivativeStructure dsX = new DerivativeStructure(3, maxOrder, 0, x);

		DerivativeStructure dsY = new DerivativeStructure(3, maxOrder, 1, y);

		DerivativeStructure dsZ = new DerivativeStructure(3, maxOrder, 2, z);
		
		DerivativeStructure f1 = dsX.divide(dsY.cos().add(dsZ.tan())).sin();
		DerivativeStructure f2 = dsX.cosh().multiply(dsY.sinh()).multiply(dsY.multiply(dsX).atan());
		DerivativeStructure f3 = dsX.tanh().divide(dsZ.cos()).sqrt().log1p();
		DerivativeStructure f4 = dsX.sin().divide(dsY.cos().pow(2).multiply(2));
		DerivativeStructure f5 = DerivativeStructure.atan2(dsY, dsZ);
		DerivativeStructure f=f1.add(f2).subtract(f3).add(f4).add(f5);
		// double a = FastMath.cos(y) + FastMath.tan(z);
		// double f0 = FastMath.sin(x / a);
		// Assert.assertEquals(f0, f.getValue(),
		// FastMath.abs(epsilon * f0));
		if (f.getOrder() > 0) {
			//int u = (int) (Math.random() * 3) + 1;
			switch (u1) {

			case 1:
				result = f.getPartialDerivative(1, 0, 0);
				break;

			case 2:
				result = f.getPartialDerivative(0, 1, 0);
				break;
			case 3:
				result = f.getPartialDerivative(0, 0, 1);
				break;
			}
			if (f.getOrder() > 1) {

				//u = (int) (Math.random() * 4) + 1;
				switch (u2) {
				case 1:
					result =f.getPartialDerivative(2, 0, 0);
					break;
				case 2:
					result =f.getPartialDerivative(0, 2, 0);
					break;
				case 3:
					result =f.getPartialDerivative(0, 0, 2);
					break;
				case 4:
					result =f.getPartialDerivative(1, 1, 0);
					break;

				}

			}

		}
		return result;

	}

	public static double testTaylorAtan2(double x, double y, int orderx, int ordery) {

		DerivativeStructure dsX = new DerivativeStructure(2, maxOrder, 0, x);

		DerivativeStructure dsY = new DerivativeStructure(2, maxOrder, 1, y);
		DerivativeStructure atan2 = DerivativeStructure.atan2(dsY, dsX);
		return atan2.taylor(x/10,y/10);
		// DerivativeStructure ref = dsY.divide(dsX).atan();
		// if (x < 0) {
		// ref = (y < 0) ? ref.subtract(FastMath.PI) : ref.add(FastMath.PI);
		// }
		// DerivativeStructure zero = atan2.subtract(ref);
		// for (int n = 0; n <= maxOrder; ++n) {
		// for (int m = 0; m <= maxOrder; ++m) {
		// if (n + m <= maxOrder) {
		// Assert.assertEquals(0, zero.getPartialDerivative(n, m),
		// epsilon[n + m]);
		// }
		// }
		// }
	}

	public static double testTrigo2(double x, int order) {
		
		DerivativeStructure dsX = new DerivativeStructure(1, maxOrder, 0, x);
		
		DerivativeStructure rebuiltX = dsX.cos().acos();
		DerivativeStructure rebuiltX2 = rebuiltX.sin().asin();
		DerivativeStructure rebuiltX3 = rebuiltX2.tan().atan();
		DerivativeStructure rebuiltX4 = rebuiltX3.sinh().asinh();
		DerivativeStructure rebuiltX5 = rebuiltX4.cosh().acosh();
		DerivativeStructure rebuiltX6 = rebuiltX5.tanh().atanh().pow(3).cbrt();
		DerivativeStructure rebuiltX7 = rebuiltX6.sqrt().exp().log().expm1()
				.log1p();

		return rebuiltX7.getPartialDerivative(order);
	}
	
	
	/********************************************************************************/

	public static double testExpression_bug(double x, double y, double z, int u1,int u2) {
		//double epsilon = 2.5e-13;
		double result=0;
		DerivativeStructure_bug dsX = new DerivativeStructure_bug(3, 5, 0, x);

		DerivativeStructure_bug dsY = new DerivativeStructure_bug(3, 5, 1, y);

		DerivativeStructure_bug dsZ = new DerivativeStructure_bug(3, 5, 2, z);

		//int u = (int) (Math.random() * 2) ;
		
		// f(x, y, z) += x + 5 x y - 2 z + (8 z x - y)^3
		DerivativeStructure_bug ds= new DerivativeStructure_bug(1, dsX, 5,
				dsX.multiply(dsY), -2, dsZ, 1, new DerivativeStructure_bug(8,
						dsZ.multiply(dsX), -1, dsY).pow(3)).subtract(dsY.log().add(dsZ.log1p()).divide(dsX.exp())).add(dsX.pow(2).add(dsY.pow(2)).cbrt());

//		if (u1==1)
//		 {ds = new DerivativeStructure_bug1(1, dsX, 5,
//				dsX.multiply(dsY), -2, dsZ, 1, new DerivativeStructure_bug1(8,
//						dsZ.multiply(dsX), -1, dsY).pow(3)).add(dsY.log().add(dsZ.log1p()).divide(dsX.exp())).add(dsX.pow(2).add(dsY.pow(2)).cbrt());}
//		else{
//		 ds = new DerivativeStructure_bug1(1, dsX, 5,
//				dsX.multiply(dsY), -2, dsZ).add(new DerivativeStructure_bug1(8, dsZ
//				.multiply(dsX), -1, dsY).pow(3));}
//		
		//u = (int) (Math.random() * 4)+1 ;
		switch(u2){
		case 1:
			result=ds.getValue();break;
		case 2:
			result=ds.getPartialDerivative(1, 0, 0);break;
		case 3:
			result=ds.getPartialDerivative(1, 1, 0);break;
		case 4:	
			result=ds.getPartialDerivative(1, 1, 1);break;
		}
		return result;
//		double f = x + 5 * x * y - 2 * z + FastMath.pow(8 * z * x - y, 3);
//		Assert.assertEquals(f, ds.getValue(), FastMath.abs(epsilon * f));
//		Assert.assertEquals(f, dsOther.getValue(), FastMath.abs(epsilon * f));
//
//		// df/dx = 1 + 5 y + 24 (8 z x - y)^2 z
//		double dfdx = 1 + 5 * y + 24 * z * FastMath.pow(8 * z * x - y, 2);
//		Assert.assertEquals(dfdx, ds.getPartialDerivative(1, 0, 0),
//				FastMath.abs(epsilon * dfdx));
//		Assert.assertEquals(dfdx, dsOther.getPartialDerivative(1, 0, 0),
//				FastMath.abs(epsilon * dfdx));
//
//		// df/dxdy = 5 + 48 z*(y - 8 z x)
//		double dfdxdy = 5 + 48 * z * (y - 8 * z * x);
//		Assert.assertEquals(dfdxdy, ds.getPartialDerivative(1, 1, 0),
//				FastMath.abs(epsilon * dfdxdy));
//		Assert.assertEquals(dfdxdy, dsOther.getPartialDerivative(1, 1, 0),
//				FastMath.abs(epsilon * dfdxdy));
//
//		// df/dxdydz = 48 (y - 16 z x)
//		double dfdxdydz = 48 * (y - 16 * z * x);
//		Assert.assertEquals(dfdxdydz, ds.getPartialDerivative(1, 1, 1),
//				FastMath.abs(epsilon * dfdxdydz));
//		Assert.assertEquals(dfdxdydz, dsOther.getPartialDerivative(1, 1, 1),
//				FastMath.abs(epsilon * dfdxdydz));

	}

	public static double testTrigo_bug(double x, double y, double z,  int u1, int u2) {
		double epsilon = 2.0e-12;
		double result = 0;
		DerivativeStructure_bug dsX = new DerivativeStructure_bug(3, maxOrder, 0, x);

		DerivativeStructure_bug dsY = new DerivativeStructure_bug(3, maxOrder, 1, y);

		DerivativeStructure_bug dsZ = new DerivativeStructure_bug(3, maxOrder, 2, z);
		
		DerivativeStructure_bug f1 = dsX.divide(dsY.cos().add(dsZ.tan())).sin();
		DerivativeStructure_bug f2 = dsX.cosh().multiply(dsY.sinh()).multiply(dsY.multiply(dsX).atan());
		DerivativeStructure_bug f3 = dsX.tanh().divide(dsZ.cos()).sqrt().log1p();
		DerivativeStructure_bug f4 = dsX.sin().divide(dsY.cos().pow(2).multiply(2));
		DerivativeStructure_bug f5 = DerivativeStructure_bug.atan2(dsY, dsZ);
		DerivativeStructure_bug f=f1.add(f2).subtract(f3).add(f4).add(f5);
		// double a = FastMath.cos(y) + FastMath.tan(z);
		// double f0 = FastMath.sin(x / a);
		// Assert.assertEquals(f0, f.getValue(),
		// FastMath.abs(epsilon * f0));
		if (f.getOrder() > 0) {
			//int u = (int) (Math.random() * 3) + 1;
			switch (u1) {

			case 1:
				result = f.getPartialDerivative(1, 0, 0);
				break;

			case 2:
				result = f.getPartialDerivative(0, 1, 0);
				break;
			case 3:
				result = f.getPartialDerivative(0, 0, 1);
				break;
			}
			if (f.getOrder() > 1) {

				//u = (int) (Math.random() * 4) + 1;
				switch (u2) {
				case 1:
					result =f.getPartialDerivative(2, 0, 0);
					break;
				case 2:
					result =f.getPartialDerivative(0, 2, 0);
					break;
				case 3:
					result =f.getPartialDerivative(0, 0, 2);
					break;
				case 4:
					result =f.getPartialDerivative(1, 1, 0);
					break;

				}

			}

		}
		return result;

	}

	public static double testTaylorAtan2_bug(double x, double y, int orderx, int ordery) {

		//DerivativeStructure _dsX = new DerivativeStructure(2, maxOrder, 0, x);

		//DerivativeStructure _dsY = new DerivativeStructure(2, maxOrder, 1, y);
		//DerivativeStructure _atan2 = DerivativeStructure.atan2(_dsY, _dsX);
		//DSCompiler a=_dsX.getCompiler();
		//DSCompiler_bug3 dsc=new DSCompiler_bug3(a.getFreeParameters(), a.getOrder(), a.getsizes(), a.getderivativesIndirection(),a.getlowerIndirection(), a.getmultIndirection(), a.getcompIndirection());
		DerivativeStructure_bug dsX = new DerivativeStructure_bug(2, maxOrder, 0, x);
		DerivativeStructure_bug dsY = new DerivativeStructure_bug(2, maxOrder, 1, y);
		
		
		DerivativeStructure_bug atan2 = DerivativeStructure_bug.atan2(dsY, dsX);
		return atan2.taylor(x/10,y/10);
		// DerivativeStructure ref = dsY.divide(dsX).atan();
		// if (x < 0) {
		// ref = (y < 0) ? ref.subtract(FastMath.PI) : ref.add(FastMath.PI);
		// }
		// DerivativeStructure zero = atan2.subtract(ref);
		// for (int n = 0; n <= maxOrder; ++n) {
		// for (int m = 0; m <= maxOrder; ++m) {
		// if (n + m <= maxOrder) {
		// Assert.assertEquals(0, zero.getPartialDerivative(n, m),
		// epsilon[n + m]);
		// }
		// }
		// }
	}

	public static double testTrigo2_bug(double x, int order) {
		
		DerivativeStructure_bug dsX = new DerivativeStructure_bug(1, maxOrder, 0, x);
		
		DerivativeStructure_bug rebuiltX = dsX.cos().acos();
		DerivativeStructure_bug rebuiltX2 = rebuiltX.sin().asin();
		DerivativeStructure_bug rebuiltX3 = rebuiltX2.tan().atan();
		DerivativeStructure_bug rebuiltX4 = rebuiltX3.sinh().asinh();
		DerivativeStructure_bug rebuiltX5 = rebuiltX4.cosh().acosh();
		DerivativeStructure_bug rebuiltX6 = rebuiltX5.tanh().atanh().pow(3).cbrt();
		DerivativeStructure_bug rebuiltX7 = rebuiltX6.sqrt().exp().log().expm1()
				.log1p();

		return rebuiltX7.getPartialDerivative(order);
	}
	
	
}
