package com.facilio.filters;

import java.io.*;
import java.util.Locale;

class FacilioPrintWriter extends PrintWriter {

    private static final int LINE_SEPARATOR_LENGTH = System.lineSeparator().length();
    private PrintWriter writer;
    private int lengthInBytes;

    FacilioPrintWriter(FacilioHttpResponse response, PrintWriter writer) {
        this(writer);
        this.writer = writer;
    }


    FacilioPrintWriter(Writer out) {
        super(out);
    }

    public FacilioPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public FacilioPrintWriter(OutputStream out) {
        super(out);
    }

    public FacilioPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public FacilioPrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public FacilioPrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public FacilioPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public FacilioPrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    private void addLength(int length) {
        lengthInBytes = lengthInBytes + length;
    }

    @Override
    public void print(int i) {
        writer.print(i);
    }

    @Override
    public void write(int c) {
        addLength(1);
        writer.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        addLength(len);
        writer.write(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        addLength(buf.length);
        writer.write(buf);
    }

    @Override
    public void write(String s, int off, int len) {
        addLength(len);
        writer.write(s, off, len);
    }

    @Override
    public void write(String s) {
        addLength(s.length());
        writer.write(s);
    }

    @Override
    public void print(boolean b) {
        print(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        addLength(1);
        writer.print(c);
    }

    @Override
    public void print(long l) {
        print(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        print(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        print(String.valueOf(d));
    }

    @Override
    public void print(char[] s) {
        addLength(s.length);
        writer.print(s);
    }

    @Override
    public void print(String s) {
        addLength(s.length());
        writer.print(s);
    }

    @Override
    public void print(Object obj) {
        print(String.valueOf(obj));
    }

    @Override
    public void println() {
        addLength(LINE_SEPARATOR_LENGTH);
        writer.println();
    }

    @Override
    public void println(boolean x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(char x) {
        addLength(1 + LINE_SEPARATOR_LENGTH);
        writer.println(x);
    }

    @Override
    public void println(int x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(double x) {
        println(String.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        addLength(x.length + LINE_SEPARATOR_LENGTH);
        writer.println(x);
    }

    @Override
    public void println(String x) {
        addLength(x.length() + LINE_SEPARATOR_LENGTH);
        writer.println(x);
    }

    @Override
    public void println(Object x) {
        println(String.valueOf(x));
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
        addLength(String.format(format, args).length());
        return writer.printf(format, args);
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
        addLength(String.format(l, format, args).length());
        return writer.printf(l, format, args);
    }

    @Override
    public PrintWriter format(String format, Object... args) {
        addLength(String.format(format, args).length());
        return writer.format(format, args);
    }

    @Override
    public PrintWriter format(Locale l, String format, Object... args) {
        addLength(String.format(l, format, args).length());
        return writer.format(l, format, args);
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        if(csq != null) {
            addLength(csq.length());
        }
        return writer.append(csq);
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        addLength(end - start);
        return writer.append(csq, start, end);
    }

    @Override
    public PrintWriter append(char c) {
        addLength(1);
        return writer.append(c);
    }
}
