package com.facilio.util;

import java.math.BigDecimal;

public class ArithmeticUtil {
	
	public static double add(String a, String b) {
		
		BigDecimal a1 = new BigDecimal(a);
		a1 = a1.add(new BigDecimal(b));
		
		return a1.doubleValue();
	}
	
	public static double subtract(String a, String b) {
		
		BigDecimal a1 = new BigDecimal(a);
		a1 = a1.subtract(new BigDecimal(b));
		
		return a1.doubleValue();
	}
	
	public static double multiply(String a, String b) {
		
		BigDecimal a1 = new BigDecimal(a);
		a1 = a1.multiply(new BigDecimal(b));
		
		return a1.doubleValue();
	}
	
	public static double divide(String a, String b) {
		
		BigDecimal a1 = new BigDecimal(a);
		a1 = a1.divide(new BigDecimal(b));
		
		return a1.doubleValue();
	}
	
}
