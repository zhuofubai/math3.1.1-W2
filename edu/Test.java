package edu;

import java.util.Random;

import edu.cwru.eecs.gang.faultlocalization.expressionvalue.profiler.Profiler;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			Profiler.visitNewTest(1110);
			mid(100,200,300);
			
			Profiler.visitNewTest(1111);
			System.out.println("-----------------------------------------");
			int a1 = 2, b1 = 10;
			System.out.println(a1 + b1);
			System.out.println(a1 - b1);
			System.out.println(a1 * b1);
			System.out.println(a1 / b1);

			Profiler.visitNewTest(1);
			System.out.println("-----------------------------------------");
			short a2 = 2, b2 = 10;
			System.out.println(a2 + b2);
			System.out.println(a2 - b2);
			System.out.println(a2 * b2);
			System.out.println(a2 / b2);
			
			Profiler.visitNewTest(2);
			System.out.println("-----------------------------------------");
			float a3 = 2, b3 = 10;
			System.out.println(a3 + b3);
			System.out.println(a3 - b3);
			System.out.println(a3 * b3);
			System.out.println(a3 / b3);
			
			Profiler.visitNewTest(3);
			System.out.println("-----------------------------------------");
			long a4 = 2, b4 = 10;
			System.out.println(a4 + b4);
			System.out.println(a4 - b4);
			System.out.println(a4 * b4);
			System.out.println(a4 / b4);
			
			Profiler.visitNewTest(4);
			System.out.println("-----------------------------------------");
			double a5 = 2, b5 = 10;
			System.out.println(a5 + b5);
			System.out.println(a5 - b5);
			System.out.println(a5 * b5);
			System.out.println(a5 / b5);
			
			Profiler.visitNewTest(5);
			System.out.println("-----------------------------------------");
			int c1 = 2;
			System.out.println(c1 + 10);
			System.out.println(c1 - 10);
			System.out.println(c1 * 10);
			System.out.println(c1 / 10);

			Profiler.visitNewTest(6);
			System.out.println("-----------------------------------------");
			float c2 = 2;
			System.out.println(c2 + (c1 * 5));
			System.out.println(c2 - (c1 * 5));
			System.out.println(c2 * (c1 * 5));
			System.out.println(c2 / (c1 * 5));
			
			Profiler.visitNewTest(7);
			System.out.println("-----------------------------------------");
			float c3 = 3;
			System.out.println(c3 + sqrt(c1 * 5));
			System.out.println(c3 - sqrt(c1 * 5));
			System.out.println(c3 * sqrt(c1 * 5));
			System.out.println(c3 / sqrt(c1 * 5));
			
			Profiler.visitNewTest(8);
			System.out.println("-----------------------------------------");
			double c31 = 2;
			double c4 = 3;
			System.out.println(c4 + sqrt(c31 * 5));
			System.out.println(c4 - sqrt(c31 * 5));
			System.out.println(c4 * sqrt(c31 * 5));
			System.out.println(c4 / sqrt(c31 * 5));
			
			Profiler.visitNewTest(9);
			System.out.println("-----------------------------------------");
			int[] array = new int[5];
			array[0] = 1;
			array[1] = 2;
			array[2] = 3;
			array[3] = 4;
			array[4] = 5;
			System.out.println(array[0] + sqrt(array[4] * 5));
			
			Profiler.visitNewTest(10);
			System.out.println("-----------------------------------------");
			int d1 = 3;
			float d2 = 3;
			long d3 = 3;
			double d4 = 3;
			System.out.println(-d1);
			System.out.println(-d2);
			System.out.println(-d3);
			System.out.println(-d4);
			
			Profiler.visitNewTest(11);
			System.out.println("-----------------------------------------");
			mid(1, 3, 2);
			(new Test()).mid2(4, 6, 5, 8);
			
			Profiler.visitNewTest(12);
			System.out.println("-----------------------------------------");
			(new Test()).mid3(4, "str1", 6, "str2", 5);
			
			
			int n = 5;
			Random r = new Random();
			for (int i = 0; i < n; i++) {
				Profiler.visitNewTest(20+i);
				System.out.println("-----------------------------------------");
				int x = r.nextInt(n);
				int y = r.nextInt(n);
				int z = r.nextInt(n);
				mid(x, y, z);
			}
			
		}finally{
			Profiler.stopProfiling();
		}
	}
	
	private static float sqrt(int s){
		return (float)Math.sqrt(s);
	}
	
	private static double sqrt(double s){
		return Math.sqrt(s);
	}
	
	private static int mid(int x, int y, int z) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y + 1;
			} else if (x < z) {
				//////////
				m = y;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		m = mid2(x, y, z, x*y)+5+6;
		
		m = m + 1;
		
		long mm = m;
		
		mm++;
		
		return (int)mm;
	}
	
	private static int mid2(int x, int y, int z, int ee) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				//////////
				m = y;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		return m;
	}
	
	private int mid3(int x, String str1, int y, String str2, int z) {

		int m = z;

		if (y < z) {
			if (x < y) {
				m = y;
			} else if (x < z) {
				//////////
				m = y;
			}
		} else {
			if (x > y) {
				m = y;
			} else if (x > z) {
				m = x;
			}
		}

		return m;
	}
}
