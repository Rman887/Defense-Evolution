package com.rman.engine;

public final class GMath {

	public static final float PI = 3.14159f;
	public static final float ONE_OVER_PI = 0.31830f;
	public static final float TWO_PI = 6.28318f;
	public static final float E = 2.71828f;
	public static final float GOLDEN_RATIO = 1.61803f;
	public static final float ROOT_TWO = 1.41421f;
	public static final float ROOT_THREE = 1.73205f;

	

	/**
	 * <pre>public static int gcd(int a, int b)</pre>
	 * 
	 * <p> Computes the greatest common divisor of two numbers. </p>
	 * 
	 * @param a - The first number
	 * @param b - The second number
	 * 
	 * @return The greatest common divisor of the two numbers
	 */
	public static int gcd(int a, int b) {
		a = Math.abs(a);
		b = Math.abs(b);
		while (b != 0) {
			int temp = b;
			b = a % temp;
			a = temp;
		}
		return a;
	}
	
	/**
	 * <pre>public static int lcm(int a, int b)</pre>
	 * 
	 * <p> Computes the least common multiple of two numbers. </p>
	 * 
	 * @param a - The first number
	 * @param b - The second number
	 * 
	 * @return The least common multiple of the two numbers
	 */
	public static int lcm(int a, int b) {
		if (a == 0 && b == 0) {
			return 0;
		}
		return Math.abs(a * b) / gcd(a, b);
	}
}
