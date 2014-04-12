/**
 * 
 */
package org.apache.commons.math3.dfp;

/**
 * @author zhuofu
 *
 */
public class DfpMath_Exp {

	/**
	 * 
	 */
	public DfpMath_Exp() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  DfpField factory;
		  Dfp pinf;
		  Dfp ninf;
		  Dfp nan;
		  Dfp qnan;
		factory = new DfpField(20);
		Dfp a1=factory.newDfp(1.2);
		Dfp a2=DfpMath.log(a1);
		System.out.println("end");
//        pinf = factory.newDfp("1").divide(factory.newDfp("0"));
//        ninf = factory.newDfp("-1").divide(factory.newDfp("0"));
//        nan = factory.newDfp("0").divide(factory.newDfp("0"));
//        qnan = factory.newDfp((byte)1, Dfp.QNAN);
//        ninf.getField().clearIEEEFlags();
//
//        // force loading of dfpmath
//        Dfp pi = factory.getPi();
//        pi.getField().clearIEEEFlags();
        
	}

}
