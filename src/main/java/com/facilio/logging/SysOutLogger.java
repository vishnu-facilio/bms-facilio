package com.facilio.logging;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.aws.util.AwsUtil;

public class SysOutLogger extends PrintStream {

    private String loggerName;
    private Logger logger;

    public SysOutLogger(String loggerName) throws FileNotFoundException {
        super(loggerName);
        this.loggerName = loggerName;
        this.logger = Logger.getLogger(loggerName);
    }


    public void print(boolean b) {
        println(b);
    }

    public void print(char c) {
        println(c);
    }

    public void print(int i) {
        println(i);
    }

    public void print(long l) {
        println(l);
    }

    public void print(float f) {
        println(f);
    }

    public void print(double d) {
        println(d);
    }

    public void print(char[] s) {
        println(s);
    }

    public void print(String s) {
        println(s);
    }

    public void print(Object obj) {
        println(obj);
    }

    public void println() {
        println(System.lineSeparator());
    }

    public void println(boolean x) {
        println(Boolean.valueOf(x));
    }

    public void println(char x) {
        println(Character.toString(x));
    }

    public void println(int x) {
        println(Integer.toString(x));
    }

    public void println(long x) {
        println(Long.toString(x));
    }

    public void println(float x) {
        println(Float.toString(x));
    }

    public void println(double x) {
        println(Double.toString(x));
    }

    public void println(char[] x) {
        println(String.valueOf(x));
    }

    public void println(String x) {
        if(AwsUtil.isDevelopment()) {
        		logger.log(Level.INFO, x);
        }
    }

    public void println(Object x) {
        if(x != null) {
            println(x.toString());
        } else {
            println("null");
        }
    }
}
