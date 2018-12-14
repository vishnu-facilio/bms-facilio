package com.facilio.instrumentation;

import java.lang.instrument.Instrumentation;

final public class SizeOf {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long sizeOf(Object o) {
    	if (instrumentation == null) {
    		return -1;
    	}
    	if (o != null) {
    		return instrumentation.getObjectSize(o);
    	}
    	return 0;
    }
}
