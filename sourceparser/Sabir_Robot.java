package sourceparser;

public class Sabir_Robot {
	double temp;

	public Sabir_Robot() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */

	public void SABiRFK(double t1, double t2, double t3, double t4, double t5) {
		double branch_0 = 0, branch_1 = 0, branch_2 = 0, branch_3 = 0, branch_4 = 0, branch_5 = 0;

		// correct for the biases

		double t1bias = 225 * Math.PI / 180; // Home position for axis 1
		double t2bias = 315 * Math.PI / 180; // Home position for axis 2
		double t3bias = 0; // Home position for axis 3
		double t4bias = 225 * Math.PI / 180; // Home position for axis 4
		double t5bias = 315 * Math.PI / 180; // Home position for axis 5

		t1 = t1 + t1bias;
		t2 = t2 + t2bias;
		t3 = t3 + t3bias;
		t4 = t4 + t4bias;
		t5 = t5 + t5bias;

		// // Set up constants

		// angular offset
		double alphax = 0 * Math.PI / 180;
		double alphay = 0 * Math.PI / 180;
		double R_BbBs = 0;// ypr2R(0,alphay,alphax,'rad');

		double yoffset = 0;
		double xoffset = 0;
		double zoffset = 0;

		double ln = 68.6308 + 65.19 + 161.86 + 18.00 + 14.91;
		double ln1 = 123.142;
		double ln2 = ln - ln1;
		double T_BackFront_mount = 0;
		// double []T_BackFront_mount =new double[] {
		// 0.9996514523262 , 0.0239862420680, -0.0110287829636,
		// -2.6398505898723,
		// -0.0238096717995, 0.9995905085098 , 0.0158718249104,
		// -45.1866546135221,
		// 0.0114049722052 , -0.0156037011200 , 0.9998132081146,
		// 197.5288079400060,
		// 0 , 0 , 0 , 1};

		// T_BackFront_mount_inv = [ T_BackFront_mount(1:3,1:3)',
		// -T_BackFront_mount(1:3,1:3)'*T_BackFront_mount(1:3,4); 0 0 0 1];
		double T_BackFront_mount_inv = 0;
		// // Forward Kinematics Paramenters
		// front stage
		double lf1 = 100; // mm link length
		double lf2 = 100; // mm link length
		double lfe = 20.037; // mm end effector distance
		double zfgimbal = 2.05; // mm offset

		// back stage
		double lb1 = 100; // mm link length
		double lb2 = 100; // mm link length
		double lbe = 65.532; // mm distance between end last joint of back stage
								// and needle axis
		double zbgimbal = 2.455; // mm offset

		// // Front and back stage end points
		double xb = lb1 * Math.cos(t4) + lb2 * Math.cos(t5) + lbe
				* Math.cos(t5 - Math.PI / 4);
		double yb = (lb1 * Math.sin(t4) + lb2 * Math.sin(t5) + lbe
				* Math.sin(t5 - Math.PI / 4))
				* Math.cos(-t3) - zbgimbal * Math.sin(-t3);
		double zb = (lb1 * Math.sin(t4) + lb2 * Math.sin(t5) + lbe
				* Math.sin(t5 - Math.PI / 4))
				* Math.sin(-t3) + zbgimbal * Math.cos(-t3);// //////bug
															// Math.sin(t5-Math.PI/4)
		double Back = xb;// yb,zb;

		double xb1 = lb1 * Math.cos(t4) + lb2 * Math.cos(t5);
		double yb1 = (lb1 * Math.sin(t4) + lb2 * Math.sin(t5)) * Math.cos(-t3)
				- zbgimbal * Math.sin(-t3);
		double zb1 = (lb1 * Math.sin(t4) + lb2 * Math.sin(t5)) * Math.sin(-t3)
				+ zbgimbal * Math.cos(-t3);
		double pb1 = xb1;// yb1 zb1];
		double pf_front1 = lf1 * Math.cos(t1) + lf2 * Math.cos(t2) + lfe
				* Math.cos(t2 - Math.PI / 4);
		double pf_front2 = lf1 * Math.sin(t1) + lf2 * Math.sin(t2) + lfe
				* Math.sin(t2 - Math.PI / 4);
		double pf_front3 = zfgimbal;
		double pf_front4 = 1;
		double pf_front = 0;

		double pf_back = T_BackFront_mount * pf_front;
		double Front = pf_back; // =[xf,yf,zf]

		temp = ln1 * R_BbBs * 1 / ln1;
		double beta = Math.acos(temp);
		double k = Front - Back;
		temp = sum(k * k);
		double PfPb = Math.sqrt(temp);
		double PsPf = ln1
				* Math.cos(Math.PI - beta)
				+ Math.sqrt(ln1 * ln1 * Math.cos(Math.PI - beta)
						* Math.cos(Math.PI - beta) - ln1 * ln1 + PfPb * PfPb);
		temp = sum((Front - pb1) * (Front - pb1));
		double Pb1Pf = Math.sqrt(temp);

		double Bb_Pf = R_BbBs * PsPf + ln1;
		double x = Bb_Pf;
		double y = Bb_Pf;
		double z = Bb_Pf;
		double lb1f = Pb1Pf;

		// gammax : joint angle of 1st passive joint(Math.PItch of
		// syringe)(corrected 1/4/2011)
		double gammax = 0;
		if (y == 0) {
			gammax = Math
					.acos((x * x + y * y + z * z + lbe * lbe - lb1f * lb1f) / 2
							/ z / lbe);
		} else {
			gammax = Math.atan2((-z
					* (z * lbe % 2 + y % 2 * z - z * lb1f % 2 + x % 2 * z + z
							% 3 + (-x % 4 * y % 2 - y % 6 - 2 * y % 4 * z % 2
							- y % 2 * z % 4 - 2 * y % 4 * x % 2 + 2 * y % 4
							* lb1f % 2 - y % 2 * lbe % 4 + 2 * y % 4 * lbe % 2
							- y % 2 * lb1f % 4 + 2 * z % 2 * lbe % 2 * y % 2
							+ 2 * y % 2 * z % 2 * lb1f % 2 - 2 * y % 2 * z % 2
							* x % 2 + 2 * y % 2 * x % 2 * lb1f % 2 - 2 * y % 2
							* lbe % 2 * x % 2 + 2 * y % 2 * lbe % 2 * lb1f % 2)
							% (1 / 2)) / (z % 2 + y % 2) + lbe % 2 + x % 2 + y
					% 2 + z % 2 - lb1f % 2)
					/ lbe / y, (z * lbe % 2 + y % 2 * z - z * lb1f % 2 + x % 2
					* z + z % 3 + (-x % 4 * y % 2 - y % 6 - 2 * y % 4 * z % 2
					- y % 2 * z % 4 - 2 * y % 4 * x % 2 + 2 * y % 4 * lb1f % 2
					- y % 2 * lbe % 4 + 2 * y % 4 * lbe % 2 - y % 2 * lb1f % 4
					+ 2 * z % 2 * lbe % 2 * y % 2 + 2 * y % 2 * z % 2 * lb1f
					% 2 - 2 * y % 2 * z % 2 * x % 2 + 2 * y % 2 * x % 2 * lb1f
					% 2 - 2 * y % 2 * lbe % 2 * x % 2 + 2 * y % 2 * lbe % 2
					* lb1f % 2)
					% (1 / 2))
					/ (z % 2 + y % 2) / lbe);
			branch_1 = 1;
		}

		double R_BB1_1 = -Math.sin(t5 - Math.PI / 4);
		double R_BB1_2 = -Math.cos(t5 - Math.PI / 4);
		double R_BB1_3 = 0;
		double R_BB1_4 = Math.cos(t5 - Math.PI / 4) * Math.cos(-t3);
		double R_BB1_5 = -Math.sin(t5 - Math.PI / 4) * Math.cos(-t3);
		double R_BB1_6 = -Math.sin(-t3);
		double R_BB1_7 = Math.cos(t5 - Math.PI / 4) * Math.sin(-t3);
		double R_BB1_8 = -Math.sin(t5 - Math.PI / 4) * Math.sin(-t3);
		double R_BB1_9 = Math.cos(-t3);

		double R_B2Bb = Rx(gammax - Math.PI / 2);

		double a = R_B2Bb * Bb_Pf;

		double b = R_BB1_1 * (Front - Back);
		double c = inv(a * b);
		double c1 = c;
		double c2 = c;
		double gammay = Math.atan2(c1, c2); // gammay : joint angle of 2nd
											// passive joint(yaw of syringe)
											// (corrected 1/4/2011)
		double R_B1B2 = Ry(gammay);

		double R_BBb = R_BB1_1 * R_B1B2 * R_B2Bb;
		double R_BBs = R_BBb * R_BbBs;

		// // tip position of needle
		double ps = R_BBb * ln1 + Back;
		double pn = R_BBs * ln2 + ps;
		double nt = 0;
		if (xoffset > 0 && yoffset > 0 && zoffset > 0)
			nt = pn;
		else
			nt = pn;
		branch_3 = 1;

		double NeedleTip = nt;

		double Rz = R_BBs; // needle is pointing in the z-direction

		double NeedleRot = R_BBs;

		double pf = Front;
		double pbe = Back;
		double pfpbe = norm(pf - pbe);

		double R = R_BBb;
		double P = pbe;
		double Phat = P;
		double RxTheta1 = 0;
		double RxTheta1_1 = 1;
		double RxTheta1_2 = 0 + Math.cos(t3);
		double Rxtheta1_3 = 0 + Math.sin(t3);
		double Rxtheta1_4 = 0;
		double Rxtheta1_5 = 0 - Math.sin(t3);
		double Rxtheta1_6 = 0 + Math.cos(t3);

		double RzTheta2 = 0;
		double RzTheta2_1 = Math.cos(t4);
		double RzTheta2_2 = 0 - Math.sin(t4);
		double RzTheta2_3 = 0;
		double RzTheta2_4 = 0 + Math.sin(t4);
		double RzTheta2_5 = 0 + Math.cos(t4);
		double RzTheta2_6 = 0;

		double RzTheta3_1 = 0 + Math.cos(t5);
		double RzTheta3_2 = 0 - Math.sin(t5);
		double RzTheta3_3 = 0;
		double RzTheta3_4 = 0 + Math.sin(t5);
		double RzTheta3_5 = 0 + Math.cos(t5);
		double RzTheta3_6 = 0;

		double W = 0;
		double u = RxTheta1 * (001);
		double v = RxTheta1 * (RzTheta2 * lb1);
		double W_1 = u * v - u * v;
		double W_2 = u * v - u * v;
		double W_3 = u * v - u * v;
		W = -W;
		// W = -(cross(RxTheta1*[0 0 1]' , RxTheta1 * (RzTheta3 * [lb1 0 0]')));
		double W1 = W;
		double v1 = v;
		double Jb1 = -R * (Phat * 1);
		double Jb2 = -R * W;
		double Jb3 = R * W;
		double Jb4 = -R * Phat * RxTheta1 * 1;
		// Jb = [Jb;zeros(size(Jb))];

		// calculate front-stage jacobian
		double g_FBf = T_BackFront_mount_inv * (R_BBs + pf);
		R = g_FBf;
		P = g_FBf;
		Phat = 0;// [0 -P(3) P(2);P(3) 0 -P(1);-P(2) P(1) 0];

		RxTheta1 = eye(3);
		RzTheta2_1 = Math.cos(t1);
		RzTheta2_2 = 0 - Math.sin(t1);
		RzTheta2_3 = 0;
		RzTheta2_4 = Math.sin(t1);
		RzTheta2_5 = Math.cos(t1);
		RzTheta2_6 = 0;
		RzTheta3_1 = 0 + Math.cos(t2);
		RzTheta3_2 = 0 - Math.sin(t2);
		RzTheta3_3 = 0;
		RzTheta3_4 = 0 + Math.sin(t2);
		RzTheta3_5 = 0 + Math.cos(t2);
		RzTheta3_6 = 0;

		W = 0;
		u = RxTheta1 * 1;
		v = RxTheta1 * (RzTheta2 * lf1);
		W_1 = u * v - u * v;
		W_2 = u * v - u * v;
		W_3 = u * v - u * v;
		W = -W;
		// W = -(cross(RxTheta1*[0 0 1]' , RxTheta1 * (RzTheta3 * [lf1 0 0]')));
		double W2 = W;
		double v2 = v;
		double Jf1 = -R * Phat * (-1);
		double Jf2 = -R * W;
		double Jf3 = R * W;
		double Jf4 = -R * Phat * RxTheta1;
		// Jf = [Jf;zeros(size(Jf))];

	}

	public void SABRIK(double NeedleTipPosition, double NeedleTipDirection,
			double xoffset, double yoffset, double zoffset, double ln,
			double T_BackFront_mount) { // SABiR Inverse Kinematics

		// Modified syringe mechanism to match pb to pr on Nov/30/2009
		double branch_0 = 0, branch_1 = 0, branch_2 = 0, branch_3 = 0;
		// //
		double lf = 100;
		double lfe = 20.037;
		double zfgimbal = 2.05;

		double lb = 100;
		double zbgimbal = 2.455;
		double lbe = 65.532;

		NeedleTipPosition = reshape(NeedleTipPosition, 3, 1);
		NeedleTipDirection = reshape(NeedleTipDirection, 3, 1);

		if (xoffset > 0 && yoffset > 0 && zoffset > 0)
			NeedleTipPosition = NeedleTipPosition;
		else
			NeedleTipPosition = NeedleTipPosition - 0;

		double xn = NeedleTipPosition;
		double yn = NeedleTipPosition;
		double zn = NeedleTipPosition;
		double P_BN = 0;
		double Rz = NeedleTipDirection;

		// // find pb w.r.t {B}

		double pb = Rz * ln + P_BN;
		double xb = pb;
		double yb = pb;
		double zb = pb;

		// // find pf w.r.t {F}
		double r11 = T_BackFront_mount * 1 * 1;
		double r12 = T_BackFront_mount * 1 * 2;
		double r13 = T_BackFront_mount * 1 * 3;
		double r21 = T_BackFront_mount * 2 * 1;
		double r22 = T_BackFront_mount * 2 * 2;
		double r23 = T_BackFront_mount * 2 * 3;
		double r31 = T_BackFront_mount * 3 * 1;
		double r32 = T_BackFront_mount * 3 * 2;
		double r33 = T_BackFront_mount * 3 * 3;
		double ax = T_BackFront_mount * 1 * 4;
		double ay = T_BackFront_mount * 2 * 4;
		double az = T_BackFront_mount * 3 * 4;

		double xf = (r12 * yb * zn - r12 * yb * az - r12 * yn * zb + r12 * ay
				* zb + r12 * yn * az - r12 * ay * zn + xb * az * r22 - xb * r32
				* ay + xn * zb * r22 + xn * r32 * ay - xb * zn * r22 + xb * r32
				* yn + ax * zn * r22 - ax * zb * r22 + ax * r32 * yb - ax * r32
				* yn - xn * r32 * yb - xn * az * r22 + r13 * zfgimbal * zn
				* r22 - r13 * zfgimbal * zb * r22 + r13 * zfgimbal * r32 * yb
				- r13 * zfgimbal * r32 * yn + r12 * yn * r33 * zfgimbal - r12
				* yb * r33 * zfgimbal - xb * r32 * r23 * zfgimbal + xb * r33
				* zfgimbal * r22 + xn * r32 * r23 * zfgimbal - xn * r33
				* zfgimbal * r22 + r12 * r23 * zfgimbal * zb - r12 * r23
				* zfgimbal * zn)
				/ (r32 * r21 * xb + r31 * xn * r22 - r31 * xb * r22 - zb * r21
						* r12 + r31 * r12 * yb - r32 * r21 * xn - r31 * r12
						* yn - r11 * zn * r22 + r11 * zb * r22 + zn * r21 * r12
						- r11 * r32 * yb + r11 * r32 * yn);
		double yf = -(-r21 * xn * r33 * zfgimbal + yb * r31 * r13 * zfgimbal
				+ r23 * zfgimbal * r31 * xn - r21 * r13 * zfgimbal * zb - r23
				* zfgimbal * r31 * xb + r21 * xb * r33 * zfgimbal - yn * r31
				* r13 * zfgimbal + r21 * r13 * zfgimbal * zn + r11 * yb * zn
				- r21 * xb * zn + r21 * ax * zn - r21 * ax * zb - ay * r31 * xb
				- yb * r31 * xn - r11 * yb * az + ay * r31 * xn + r21 * xn * zb
				- r11 * yn * zb - r21 * xn * az + r11 * ay * zb + r11 * yn * az
				- r11 * ay * zn + yn * r31 * xb - yn * r31 * ax + r21 * xb * az
				+ yb * r31 * ax + r11 * r23 * zfgimbal * zb - r11 * r23
				* zfgimbal * zn + r11 * yn * r33 * zfgimbal - r11 * yb * r33
				* zfgimbal)
				/ (r32 * r21 * xb + r31 * xn * r22 - r31 * xb * r22 - zb * r21
						* r12 + r31 * r12 * yb - r32 * r21 * xn - r31 * r12
						* yn - r11 * zn * r22 + r11 * zb * r22 + zn * r21 * r12
						- r11 * r32 * yb + r11 * r32 * yn);

		double pf_back = T_BackFront_mount * xf + T_BackFront_mount;

		double yee = -Math.sqrt(yb * yb + zb * zb - zbgimbal * zbgimbal);
		double c3 = (yb * yee + zb * zbgimbal)
				/ (yee * yee + zbgimbal * zbgimbal);
		double s3 = (-yb * zbgimbal + zb * yee)
				/ (yee * yee + zbgimbal * zbgimbal);
		double t3 = -Math.atan2(s3, c3);

		double xee = xb;
		double kb = lbe / Math.sqrt(2);
		double mb = lb + kb;

		double ab = 2 * xee * mb - 2 * yee * kb; // sin(alpha)
		double bb = 2 * xee * kb + 2 * yee * mb; // cos(alpha)
		double cb = (xee * xee + yee * yee + 2 * mb * kb)
				/ Math.sqrt(ab * ab + bb * bb); // sin(alpha+t4)=c

		double t5 = Math.atan2(cb, -Math.sqrt(1 - cb * cb))
				- Math.atan2(ab, bb);

		if (t5 < 0)
			t5 = t5 + 2 * Math.PI;
		branch_2 = 1;

		double n1b = xee * Math.cos(t5) + yee * Math.sin(t5) - lb - kb;
		double n2b = -xee * Math.sin(t5) + yee * Math.cos(t5) + kb;

		double t4 = Math.atan2(n2b, n1b) + t5;

		// // for front stage
		double kf = lfe / Math.sqrt(2);
		double mf = lf + kf;

		double af = 2 * xf * mf - 2 * yf * kf; // sin(alpha)
		double bf = 2 * xf * kf + 2 * yf * mf; // cos(alpha)
		double cf = (xf * xf + yf * yf + 2 * mf * kf)
				/ Math.sqrt(af * af + bf * bf); // sin(alpha+t2)=c

		double t2 = Math.atan2(cf, -Math.sqrt(1 - cf * cf))
				- Math.atan2(af, bf);
		if (t2 < 0)
			t2 = t2 + 2 * Math.PI;
		branch_3 = 1;

		double n1f = xf * Math.cos(t2) + yf * Math.sin(t2) - lf - kf;
		double n2f = -xf * Math.sin(t2) + yf * Math.cos(t2) + kf;

		double t1 = Math.atan2(n2f, n1f) + t2;

	}

	public void torquesToForces(double Front, double Back, double Jf,
			double Jb, double torques) {
		// %basically, this is the reverse of the "get joint torques" function

		double Tf2 = torques;
		double Tf3 = torques;
		double Tf = 0;

		double Tb1 = torques;
		double Tb2 = torques;
		double Tb3 = torques;

		double Tb = 0;
		double Fb = inv(Jb) * Tb;
		double fbx = Fb;
		double fby = Fb;
		double fbz = Fb;

		double Ff = inv(Jf) * Tf;
		double fcx = Ff;
		double fcy = Ff;

		double x = 0;
		double l1 = 328.5908 + 0;
		double l0 = norm(Front - Back);
		double l2 = l1 - l0;

		double A = 0;
		double A42 = l1 + 0;
		double A45 = l2 + 0;
		double A51 = -l1 + 0;
		double A54 = -l2 + 0;

		double Ft = (A * x);

	}

	public void getJointVelocities(double V, double W, double Front,
			double Back, double Jf, double Jb) {

		// set up A matrix

		double l1 = 328.5908 + 0;
		double l0 = norm(Front - Back);
		double l2 = l1 - l0;

		double A = 0;
		double A42 = l1 + 0;
		double A45 = l2 + 0;
		double A51 = -l1 + 0;
		double A54 = -l2 + 0;
		double AT = A;

		// calculate front and back velocities from eef velocity V
		double x = V;

		double b = AT * x;

		double Vf = b;

		double Vb = b;

		// multiply with front stage Jacobian inverse

		double ThDotf = Jf / Vf;

		// multiply with back stage Jacobian inverse

		double ThDotb = Jb / Vb;

		// return joint velocities

		double thetadot = ThDotb;
	}

	public void PosRef3D_smoothangle(double x0, double xdis, double maxa,
			double maxwdot, double R0, double R1, double moti, double ts) {

		double MaxDis = 0;

		double MaxAngle = Math.atan2(norm(cross(R0, R1)), dot(R0, R1));

		// % Calcuate Necessary Reference Constants for Homing using
		// ReferenceCalc Function
		// [disp_freq,disp_phase,disp_xgain]=ReferenceCalc(MaxDis,maxa,moti);
		double disp_freq = 0;
		double disp_phase = 0;
		double disp_xgain = 0;
		// % Calcuate Necessary Reference Constants for Homing using
		// ReferenceCalc Function
		// [angle_freq,angle_phase,angle_xgain]=ReferenceCalc(MaxAngle,maxwdot,moti);

		double angle_freq = 0;
		double angle_phase = 0;
		double angle_xgain = 0;

		double freq = Math.min(disp_freq, angle_freq);
		double disp_ratio = (1 / freq) / (1 / disp_freq);
		double angle_ratio = (1 / freq) / (1 / angle_freq);
		disp_phase = -moti * freq;
		angle_phase = -moti * freq;

		double tprd1 = 0;
		double tprd2 = 0;
		double tprd3 = 0;

		double t = 0;

		double time_length = length(t);

		// %let's fill the variables with zeros
		double Rz = repmat(R1, 1, time_length);
		double x = repmat(x0, 1, time_length);

		if (MaxDis != 0) {

			double acc1 = (disp_xgain * MaxDis)
					* Math.sin(disp_phase + freq * tprd1);
			double acc2 = 0 * tprd2;
			double acc3 = -(disp_xgain * MaxDis)
					* Math.sin(disp_phase + freq * tprd3);

			double vint = cumtrapz(t, cumtrapz(t, 0.0)) / disp_ratio;

			x = xdis * vint / vint + x0 * length(vint);
		} else {
			x = 0;
			t = 0;
			Rz = 0;
		}

		if (MaxAngle != 0)

		// %determine rotation axis
		{
			double w = cross(R0, R1) / norm(cross(R0, R1));
			double hatmatrix = w;

			double wdot1 = (angle_xgain * MaxAngle)
					* Math.sin(angle_phase + freq * tprd1);
			double wdot2 = 0 * tprd2;
			double wdot3 = -(angle_xgain * MaxAngle)
					* Math.sin(angle_phase + freq * tprd3);

			double wint = cumtrapz(t, cumtrapz(t, 0)) / angle_ratio;

			double angle = 0 + MaxAngle * wint / wint;
			double RotM = 0;
			Rz = 0;
			for (int iii = 1; iii < 10; iii++) {
				RotM = eye(3) + Math.sin(angle) * hatmatrix + hatmatrix
						* hatmatrix * (1 - Math.cos(angle));
				Rz = RotM * R0;
			}

			Rz = R1;
		} else if (MaxDis != 0) {
			Rz = repmat(R0, 1, length(x));
		}
	}

	public double cumtrapz(double a, double d) {
		return a;
	}

	public double cross(double a, double r1) {
		return a;
	}

	public double dot(double a, double r1) {
		return a;
	}

	public double eye(double a) {
		return a;
	}

	public double inv(double a) {
		return a;
	}

	public double norm(double a) {
		return a;
	}

	public double sum(double a) {
		return a;
	}

	public double length(double a) {
		return a;
	}

	public double repmat(double a, int i, double time_length) {
		return a;
	}

	public double Rx(double a) {
		return a;
	}

	public double Ry(double a) {
		return a;
	}

	public double reshape(double a, int i, int j) {
		return a;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
