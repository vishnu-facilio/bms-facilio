package com.facilio.workflows.util;

public class FunctionUtil {

	public static double getCostValueForSlab(int slab) {
		
		switch(slab) {
		
		case 1:
			return 0.23;
		case 2:
			return 0.28;
		case 3:
			return 0.32;
		case 4:
			return 0.38;
		}
		return 0.0;
	}
}
