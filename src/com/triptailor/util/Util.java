package com.triptailor.util;

public class Util {
	
	public static double trunk(double n, double m) {
		return Math.round(n * Math.pow(10, m)) / Math.pow(10, m);
	}
}
