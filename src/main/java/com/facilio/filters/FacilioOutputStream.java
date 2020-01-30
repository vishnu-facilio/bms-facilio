package com.facilio.filters;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

public class FacilioOutputStream extends ServletOutputStream {

    private ServletOutputStream responseOutputStream;
    private int lengthInBytes = 0;
    private FacilioHttpResponse response;
    FacilioOutputStream(FacilioHttpResponse response, ServletOutputStream outputStream){
        this.response = response;
        responseOutputStream = outputStream;
    }

    private void addLength(int length) {
        lengthInBytes = lengthInBytes + length;
        response.setLengthInBytes(lengthInBytes);
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
        addLength(b);
        responseOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        addLength(len);
        responseOutputStream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        addLength(b.length);
        responseOutputStream.write(b);
    }

    @Override
    public void print(int i) throws IOException {
        addLength(String.valueOf(i).length());
        responseOutputStream.print(i);
    }
}
