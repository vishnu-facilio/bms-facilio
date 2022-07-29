package com.facilio.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class SizeBasedLogAppender extends RollingFileAppender {

    public SizeBasedLogAppender() {
        super();
    }

    public SizeBasedLogAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename,true);
        setFile(getFileName(filename));
    }


    public void activateOptions() {
        super.activateOptions();
    }

    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
    }

    public void setFile(String file) {
        super.setFile(file);
    }

    public boolean getAppend() {
        return super.getAppend();
    }

    public String getFile() {
        return super.getFile();
    }

    protected void closeFile() {
        super.closeFile();
    }

    public boolean getBufferedIO() {
        return super.getBufferedIO();
    }

    public int getBufferSize() {
        return super.getBufferSize();
    }

    public void setAppend(boolean flag) {
        super.setAppend(flag);
    }

    public void setBufferedIO(boolean bufferedIO) {
        super.setBufferedIO(bufferedIO);
    }

    public void setBufferSize(int bufferSize) {
        super.setBufferSize(bufferSize);
    }

    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
    	fileName = getFileName(fileName);
        super.setFile(fileName, append, bufferedIO, bufferSize);
    }

    protected void setQWForFiles(Writer writer) {
        super.setQWForFiles(writer);
    }

    protected void reset() {
        super.reset();
    }

    public void setImmediateFlush(boolean value) {
        super.setImmediateFlush(value);
    }

    public boolean getImmediateFlush() {
        return super.getImmediateFlush();
    }

    public void append(LoggingEvent event) {
        if( FacilioLogHandler.isLoggable(event) ) {
            super.append(FacilioLogHandler.addEventProps(event));
        }
    }

    protected boolean checkEntryConditions() {
        return super.checkEntryConditions();
    }

    public synchronized void close() {
        super.close();
    }

    protected void closeWriter() {
        super.closeWriter();
    }

    protected OutputStreamWriter createWriter(OutputStream os) {
        return super.createWriter(os);
    }

    public String getEncoding() {
        return super.getEncoding();
    }

    public void setEncoding(String value) {
        super.setEncoding(value);
    }

    public synchronized void setErrorHandler(ErrorHandler eh) {
        super.setErrorHandler(eh);
    }

    public synchronized void setWriter(Writer writer) {
        super.setWriter(writer);
    }

    public boolean requiresLayout() {
        return super.requiresLayout();
    }

    protected void writeFooter() {
        super.writeFooter();
    }

    protected void writeHeader() {
        super.writeHeader();
    }

    protected boolean shouldFlush(LoggingEvent event) {
        return super.shouldFlush(event);
    }

    public void addFilter(Filter newFilter) {
        super.addFilter(newFilter);
    }

    public void clearFilters() {
        super.clearFilters();
    }

    public void finalize() {
        super.finalize();
    }

    public ErrorHandler getErrorHandler() {
        return super.getErrorHandler();
    }

    public Filter getFilter() {
        return super.getFilter();
    }

    public Layout getLayout() {
        return super.getLayout();
    }

    public Priority getThreshold() {
        return super.getThreshold();
    }

    public boolean isAsSevereAsThreshold(Priority priority) {
        return super.isAsSevereAsThreshold(priority);
    }

    public synchronized void doAppend(LoggingEvent event) {
        if( FacilioLogHandler.isLoggable(event) ) {
            super.doAppend(FacilioLogHandler.addEventProps(event));
        }
    }

    public void setLayout(Layout layout) {
        super.setLayout(layout);
    }

    public void setName(String name) {
        super.setName(name);
    }

    public void setThreshold(Priority threshold) {
        super.setThreshold(threshold);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return super.toString();
    }
    
    private String getFileName(String fileName) {
		Date date = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");  
	    String strDate= formatter.format(date);
    	String pathArr[] = fileName.split("" + java.io.File.separatorChar);
    	String newFileName = pathArr[pathArr.length-1];
    	String a[] = newFileName.split("\\.");
    	if (a.length == 1) {
    		//append the date string to filename
    		newFileName =  newFileName + "." + strDate;
    	} else if (a[1].endsWith(strDate)) {
    		//nothing change, return the path as such
    		return fileName;
    	} else {
    		//date changed, return the path with new date
    		newFileName =  a[0] + "." + strDate;
    	}
    	StringBuilder sb  = new StringBuilder();
    	for (int i = 0; i < pathArr.length-1; i++) {
    		sb.append(pathArr[i]).append(java.io.File.separatorChar);
    	}
    	sb.append(newFileName);
    	return sb.toString();    	
    }
    
}
