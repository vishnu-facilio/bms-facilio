package com.facilio.logging;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.Locale;
import java.util.Properties;

public class SysOutLogger extends PrintStream {

    private static final Logger LOGGER = LogManager.getLogger(SysOutLogger.class.getName());

    public SysOutLogger(OutputStream out) {
        super(out);
    }

    public SysOutLogger(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public SysOutLogger(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public SysOutLogger(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public SysOutLogger(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public SysOutLogger(File file) throws FileNotFoundException {
        super(file);
    }

    public SysOutLogger(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
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
        LOGGER.log(Level.INFO, x);
    }

    public void println(Object x) {
        if(x != null) {
            println(x.toString());
        } else {
            println("null");
        }
    }
}
