package com.facilio.filters;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

public class FacilioOutputStream extends ServletOutputStream {

    private ServletOutputStream responseOutputStream;
    private int lengthInBytes = 0;

    FacilioOutputStream(ServletOutputStream outputStream){
        responseOutputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        return responseOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        responseOutputStream.setWriteListener(writeListener);
    }

    @Override
    public void write(int b) throws IOException {
        lengthInBytes = lengthInBytes + b;
        responseOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        lengthInBytes = lengthInBytes + len;
        responseOutputStream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        lengthInBytes = lengthInBytes + b.length;
        responseOutputStream.write(b);
    }

    @Override
    public void print(int i) throws IOException {
        lengthInBytes = lengthInBytes + String.valueOf(i).length();
        responseOutputStream.print(i);
    }

    public int getLengthInBytes() {
        return lengthInBytes;
    }
}
