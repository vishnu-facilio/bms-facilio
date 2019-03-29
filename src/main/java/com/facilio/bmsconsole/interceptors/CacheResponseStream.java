/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package com.facilio.bmsconsole.interceptors;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class CacheResponseStream extends ServletOutputStream {
  protected boolean closed = false;
  protected HttpServletResponse response = null;
  protected ServletOutputStream output = null;
  protected OutputStream cache = null;

  public CacheResponseStream(HttpServletResponse response,
      OutputStream cache) throws IOException {
    super();
    closed = false;
    this.response = response;
    this.cache = cache;
    System.out.println("##CacheResponseStream loaded"+cache);
  }

  public void close() throws IOException {
    if (closed) {
      throw new IOException(
        "This output stream has already been closed");
    }
    cache.close();
    close();
    closed = true;
    System.out.println("##cache closed");

  }

  public void flush() throws IOException {
    if (closed) {
      throw new IOException(
        "Cannot flush a closed output stream");
    }
    cache.flush();
    flush();
    System.out.println("##cache flushed");

  }

  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException(
        "Cannot write to a closed output stream");
    }
    cache.write((byte)b);
    write((byte)b);
    System.out.println("##byte write");

  }

  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
    cache.write(b, 0, b.length);
    System.out.println("##byte array write");

  }

  public void write(byte b[], int off, int len)
    throws IOException {
    if (closed) {
      throw new IOException(
       "Cannot write to a closed output stream");
    }
    cache.write(b, off, len);
    write(b, off, len);
    System.out.println("##byte array with offset write");

  }

  public boolean closed() {
    return (this.closed);
  }
  
  public void reset() {
    //noop
  }

@Override
public boolean isReady() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void setWriteListener(WriteListener arg0) {
	// TODO Auto-generated method stub
	
}
}
