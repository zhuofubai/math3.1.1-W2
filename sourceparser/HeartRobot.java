package sourceparser;

public class HeartRobot {

	public HeartRobot() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	public void MC(double Vel, double Pos){
		
		double	L1 = 0.215+0;
		double	L2 = 0.170+0;
		double	L3 = 0.0325+0;
				
		double		ma = 0.0202+0;
		double		Iaxx = 0.00004864+0;
		double		Iayy = 0.0000001843+0;
		double		Iazz = 0.00004864+0;
			
		double		mc = 0.0249+0;
		double		Icxx = 0.0000959+0;
		double		Icyy = 0.0000959+0;
		double		Iczz = 0.00000051+0;
			
		double	mbe = 0.2359+0;
		double		Ibexx= 0.001109+0;
		double		Ibeyy= 0.001006+0;
		double		Ibezz= 0.0000591+0;
		double		L5 = -0.0368+0;
				
		double		mdf = 0.1906+0;
		double		Idfxx= 0.000711+0;
		double		Idfyy= 0.0000629+0;
		double		Idfzz= 0.0006246+0;
		double		L6 = 0.0527+0;			
		double		Ibaseyy = 0.001187+0;

		double	A1 = (0.125)*(4*Iayy + 4*Iazz + 8*Ibaseyy + 4*Ibeyy + 4*Ibezz 	+ 4*Icyy  + 4*Iczz + 4*Idfyy + 4*Idfzz + 4*L1*L1*ma +    L2*L2*ma	+   L1*L1*mc	+ 4*L3*L3*mc);
		
		double		A2 = (0.125)*(4*Ibeyy - 4*Ibezz + 4*Icyy  - 4*Iczz	+ L1*L1*(4*ma + mc));

		double A3 = (0.125)*(4*Iayy - 4*Iazz	+ 4*Idfyy- 4*Idfzz - L2*L2*ma - 4*L3*L3*mc);

		double A4 = L1*(L2*ma+L3*mc);

		double		A5 = (0.25)*(4*(Ibexx + Icxx + L1*L1*ma) + L1*L1*mc);

		double		A6 = -(0.5)*L1*(L2*ma + L3*mc);

		double		A7 = -(0.5)*L1*(L2*ma + L3*mc);

		double		A8 = (0.25)*(4*Iaxx + 4*Idfxx + L2*L2*ma + 4*L3*L3*mc)	;

		double		M1 = A1 + A2*Math.cos(2*Pos) + A3*Math.cos(2*Pos) + A4*Math.cos(Pos)*Math.sin(Pos);
		double		M2= A5;
		double	    M3=A6 * Math.sin(Pos-Pos);
		double		M4=A6 * Math.sin(Pos-Pos);
		double	    M5=		A8;

		double		C1 =  4*Ibeyy - 4*Ibezz + 4*Icyy  - 4*Iczz + 4*L1*L1*ma + L1*L1*mc;

		double		C2 =  2*L1*(L2*ma + L3*mc); 

		double		C3 =  2*L1*(L2*ma + L3*mc); 

		double		C4 = -4*Iayy + 4*Iazz - 4*Idfyy + 4*Idfzz + L2*L2*ma + 4*L3*L3*mc;

		double		C5 =  4*Ibeyy - 4*Ibezz + 4*Icyy  - 4*Iczz	+ L1*L1*(4*ma + mc);

		double		C6 =  4*L1*(L2*ma + L3*mc);
		
		double		C7 =  -4*L1*(L2*ma + L3*mc);

		double		C8 = -4*Iayy + 4*Iazz - 4*Idfyy + 4*Idfzz + L2*L2*ma + 4*L3*L3*mc;

		double		C9 =  (0.5)*L1*(L2*ma + L3*mc);

		double		C10 = (0.5)*L1*(L2*ma + L3*mc);

		double		C =  zeros(3,3);

		double		C11 =  (0.125)*(-2*Math.sin(Pos)*(C1*Math.cos(Pos) + C2*Math.sin(Pos)) * Vel  +2*Math.cos(Pos)*(C3*Math.cos(Pos) + C4*Math.sin(Pos)) * Vel);

		double		C12 = (-0.125)*((C5 * Math.sin(2*Pos) + C6*Math.sin(Pos)*Math.sin(Pos))*Vel);

		double		C13 = (-0.125)*((C7 * Math.cos(Pos)*Math.cos(Pos) - C8*Math.sin(2*Pos))*Vel);

		double		C21 = - C12+0;
		
		double		C23 = C9 * Math.cos(Pos-Pos)*Vel;

		double		C31 = - C13+0;

		double		C32 = C10 *Math.cos(Pos-Pos)*Vel;

		double		CV=C*Vel;
				
	}
	
	public void N(double Pos){
		
		double 	g=9.81;            
				
		double 	L1 = 0.215+0;      
		double 	L2 = 0.170+0;       
		double 	L3 = 0.0325+0;     
				
		double 	ma = 0.0202+0;        
			
		double 	mc = 0.0249+0;        
			
		double 		mbe = 0.2359+0;      
		double 		L5 = -0.0368+0;       
				
		double 		mdf = 0.1906+0;       
		double 		L6 = 0.0527+0;		

				

		double 	y1=0+0;
		double 	y2=     0.5*g*(2*L1*ma + 2*L5*mbe + L1*mc)*Math.cos(Pos);
		double 	 y3=  0.5*g*(L2*ma + 2*L3*mc - 2*L6*mdf)*Math.sin(Pos);
				    
	}
	
	public void interpolator(double datSlow, double intVec){
		
				double intFactor = size(intVec,0);

				double dim = 3; 

				double Y = zeros(size(datSlow, 1)/dim, dim);

				
				for (int i = 0;i<(size(datSlow, 1)/dim-1);i++)
				{ double temp=i*dim+1;
					Y = datSlow;}
				

				double datFast = zeros( intFactor*(size(Y,1)-2)+1, size(Y,2) );

				double ParamMat = 0;
				double tau = fliplr((1-1/intFactor),1/intFactor,0);
				double Tproj_1 = tau*tau;
				double Tproj_2=tau;
				double Tproj_3=1;
				double Tproj=0;
				double BlockInt = Tproj*ParamMat;

				for(int curIndex = 1; curIndex< (size(Y, 1) - 2);curIndex++)
				    
				{ double m= (curIndex-1)*intFactor+1;
					datFast=   BlockInt*Y;}

				
				datFast = Y;
	}
	public void RLSupdate(double Des, double Data,double FF,double Wold,double Pold){
		
		double		alpha = Des - Wold*Data;
				
		double g = Pold*Data/(FF + Data*Pold*Data);

		double		P = Pold/FF - ((g*Data)/FF)*Pold;
		double		Wts = Wold + alpha*g;
			
	}
	public void IK_heart(double EndEfectPos){
		
			double	L1 = 0.215+0;         //m
			double		L2 = 0.170+0;         //m
				//bug1
			double		pox=EndEfectPos+0; //m
			double		poy=EndEfectPos+0; //m
			double		poz=EndEfectPos+0; //m
				//bug3
				//if (pox>poz)
			double		theta1=Math.atan2(pox,poz+L1);
				//  else
//				      theta1=atan2(pox,poz+L2);
				//  end
			double		d=Math.sqrt(pox*pox+(poz+L1)*(poz+L1));
			double		r=Math.sqrt(pox*pox+(poz+L1)*(poz+L1)+(poy-L2)*(poy-L2));
			double		theta2=Math.acos((L1*L1+r*r-L2*L2)/(2*L1*r))+Math.atan2(poy-L2,d);
			double		theta3=theta2+Math.acos((L1*L1-r*r+L2*L2)/(2*L1*L2))-Math.PI/2;

				
	}
	
	public void IK_breath(double EndEfectPos){
		
		double	L1 = 0.215+0;         //m
		double		L2 = 0.170+0;         //m
			//bug1
		double		pox=EndEfectPos+0; //m
		double		poy=EndEfectPos+0; //m
		double		poz=EndEfectPos+0; //m
			//bug3
			//if (pox>poz)
		double		theta1=Math.atan2(pox,poz+L1);
			//  else
//			      theta1=atan2(pox,poz+L2);
			//  end
		double		d=Math.sqrt(pox*pox+(poz+L1)*(poz+L1));
		double		r=Math.sqrt(pox*pox+(poz+L1)*(poz+L1)+(poy-L2)*(poy-L2));
		double		theta2=Math.acos((L1*L1+r*r-L2*L2)/(2*L1*r))+Math.atan2(poy-L2,d);
		double		theta3=theta2+Math.acos((L1*L1-r*r+L2*L2)/(2*L1*L2))-Math.PI/2;

			
}
	
	public void Gain_Iter1(double ReferenceInput,double Phi_r,double Gamma_r,double L,double Q,double K,double S,double R){
	

			double	horizon = length(ReferenceInput);

			double	y_desired = ReferenceInput+0;

			double	M = zeros(length(Phi_r),horizon);
			double bc=0;
				for( int counter = 1;counter<horizon-1;counter++  )
				{			    bc = horizon - counter; 
			
				    M = (Phi_r + K*Gamma_r)*M - Q*L*y_desired;
				}

				
				double u_ff = -inv(Gamma_r*S*Gamma_r + R)*Gamma_r*M;
				
	}
	
	public void Gain_Iter2(double ReferenceInput,double Phi_r,double Gamma_r,double L,double Q,double K,double S,double R){
		

		double	horizon = length(ReferenceInput);

		double	y_desired = ReferenceInput+0;

		double	M = zeros(length(Phi_r),horizon);
		double bc=0;
			for( int counter = 1;counter<horizon-1;counter++  )
			{			    bc = horizon - counter; 
		
			    M = (Phi_r + K*Gamma_r)*M - Q*L*y_desired;
			}

			
			double u_ff = -inv(Gamma_r*S*Gamma_r + R)*Gamma_r*M;
			
}
	public void Gain_Iter3(double ReferenceInput,double Phi_r,double Gamma_r,double L,double Q,double K,double S,double R){
		

		double	horizon = length(ReferenceInput);

		double	y_desired = ReferenceInput+0;

		double	M = zeros(length(Phi_r),horizon);
		double bc=0;
			for( int counter = 1;counter<horizon-1;counter++  )
			{			    bc = horizon - counter; 
		
			    M = (Phi_r + K*Gamma_r)*M - Q*L*y_desired;
			}

			
			double u_ff = -inv(Gamma_r*S*Gamma_r + R)*Gamma_r*M;
			
}
	public double inv(double a){
		return a;
	}
	public double fliplr(double a,double b, double c){
		return a;
	}
	public double length(double a){
		return a;
	}
	public double size(double a,double b){
		return a;
	}
	public double zeros(double a,double b){
		return a;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
