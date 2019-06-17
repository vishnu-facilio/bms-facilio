package com.facilio.logging;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.*;

public class FacilioLogAppender extends DailyRollingFileAppender {

    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final File ROOT_FILE = new File("/");
    private static long lastFreeSpaceCheckedTime = System.currentTimeMillis();
    private static final long FREE_SPACE_THRESHOLD = 10000000000L;
    private static long freeSpace = ROOT_FILE.getFreeSpace();
    public FacilioLogAppender() {
        super();
    }

    public FacilioLogAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, datePattern);
    }

    public void setDatePattern(String pattern) {
        super.setDatePattern(pattern);
    }

    public String getDatePattern() {
        return super.getDatePattern();
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
        Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            event.setProperty("orgId", String.valueOf(org.getOrgId()));
        } else {
            event.setProperty("orgId", DEFAULT_ORG_USER_ID);
        }
        User user = AccountUtil.getCurrentUser();
        if (user != null) {
        	event.setProperty("userId", String.valueOf(user.getOuid()));
        } else {
            event.setProperty("userId", DEFAULT_ORG_USER_ID);
        }
        try {
            ThrowableInformation information = event.getThrowableInformation();
            if (information != null) {
                Throwable throwable = information.getThrowable();
                String exceptionType = throwable.getClass().getName();
                event.setProperty("exception", exceptionType);
                if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestUri() != null) {
                    event.setProperty("req_uri", AccountUtil.getCurrentAccount().getRequestUri());
                }
                if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestParams() != null) {
                    event.setProperty("req_params", AccountUtil.getCurrentAccount().getRequestParams());
                }
            } else {
                event.setProperty("exception", "-");
                event.setProperty("req_uri", "-");
                event.setProperty("req_params", "-");
            }
        } catch (Exception e) {
            event.setProperty("exception", "LogAppenderException");
        }
        if((lastFreeSpaceCheckedTime + 300_000L) < System.currentTimeMillis()) {
            lastFreeSpaceCheckedTime = System.currentTimeMillis();
            freeSpace = ROOT_FILE.getFreeSpace();
        }

        if(freeSpace > FREE_SPACE_THRESHOLD) {
            super.append(event);
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
        super.doAppend(event);
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
}
