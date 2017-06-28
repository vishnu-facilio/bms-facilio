package com.facilio.bmsconsole.customfields;

import java.util.HashMap;
import java.util.Map;

public class CFTypes {
	public static int STRING = 1;
	public static int NUMBER = 2;
	public static int DECIMAL = 3;
	public static int BOOLEAN = 4;
	public static int DATE = 5;
	public static int DATE_TIME = 6;
	
	private static Map<Integer, String> typeString = new HashMap<>();
	
	public static void init() {
		typeString.put(STRING, "String");
	}
}
