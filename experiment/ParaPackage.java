/**
 * 
 */
package experiment;

import java.util.Random;

/**
 * @author zhuofu
 *
 */
public class ParaPackage {
	double [] m1;
	double [] m2;
	double [] m3;
	double [] m4;
	double [] m5;
	double [] m6;
	double [] m7;
	double [] m8;
	double [] m9;
	double [] m10;
	double [] m11;
	double [] m12;
	double angle1;
	int Coindex;
	int Eoindex;
	double alpha1, alpha2, alpha3;	
	double[] in; 
	double[] out;
	double p1;
	double p2;
	double p3;
	double p4;
	double p5;
	double p6;
	double p7;
	double scale=1;
	/**
	 * 
	 */
	public ParaPackage(Random r) {
		m1=generateArray(r,scale);
		m2=generateArray(r,scale);
		m3=generateArray(r,scale);
		m4=generateArray(r,scale);
		m5=generateArray(r,scale);
		m6=generateArray(r,scale);
		m7=generateArray(r,scale);
		m8=generateArray(r,scale);
		m9=generateArray(r,scale);
		m10=generateArray(r,scale);
		m11=generateArray(r,scale);
		m12=generateArray(r,scale);
		angle1=r.nextDouble()*Math.PI*2;
		Coindex=r.nextInt(6);
		Eoindex=r.nextInt(6);
		alpha1=r.nextDouble()*scale;
		alpha2=r.nextDouble()*scale;
		alpha3=r.nextDouble()*scale;
		in=generateArray(r,scale);
		out=generateArray(r,scale);
		p1=r.nextDouble();
		p2=r.nextDouble();
		p3=r.nextDouble();
		p4=r.nextDouble();
		p5=r.nextDouble();
		p6=r.nextDouble();
		p7=r.nextDouble();
		
		// TODO Auto-generated constructor stub
	}
	public double[] generateArray(Random r,double scale){
		double [] a=new double[3];
		for(int i=0;i<3;i++){
			a[i]=(r.nextDouble()-0.5)*2*scale;
		}
		return a;
	}
	
}
