package com.facilio.logging;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.graylog2.GelfAMQPSender;
import org.graylog2.GelfSender;
import org.graylog2.GelfTCPSender;
import org.graylog2.GelfUDPSender;
import org.graylog2.log.GelfAppender;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class FacilioGelfAppender extends GelfAppender {

    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final File ROOT_FILE = new File("/");
    private static long lastFreeSpaceCheckedTime = System.currentTimeMillis();
    private static final long FREE_SPACE_THRESHOLD = 10000000000L;
    private static long freeSpace = ROOT_FILE.getFreeSpace();

    public void append(LoggingEvent event) {
        if((lastFreeSpaceCheckedTime + 300_000L) < System.currentTimeMillis()) {
            lastFreeSpaceCheckedTime = System.currentTimeMillis();
            freeSpace = ROOT_FILE.getFreeSpace();
        }
        if(!(
                (   (event.getLevel().toInt() > Level.DEBUG_INT)
                        || (AccountUtil.getCurrentAccount() != null && (event.getLevel().toInt() >= AccountUtil.getCurrentAccount().getLevel().toInt()))
                )
                        && (freeSpace > FREE_SPACE_THRESHOLD)
        )) {
            return;
        }

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

        if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestUri() != null) {
            event.setProperty("req_uri", AccountUtil.getCurrentAccount().getRequestUri());
        } else {
            event.setProperty("req_uri", "-");
        }

        try {
            ThrowableInformation information = event.getThrowableInformation();
            if (information != null) {
                Throwable throwable = information.getThrowable();
                String exceptionType = throwable.getClass().getName();
                event.setProperty("exception", exceptionType);
                if( AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestParams() != null) {
                    event.setProperty("req_params", AccountUtil.getCurrentAccount().getRequestParams());
                }
            } else {
                event.setProperty("exception", "-");
                event.setProperty("req_params", "-");
            }
        } catch (Exception e) {
            event.setProperty("exception", "LogAppenderException");
        }
        super.append(event);
    }

    public FacilioGelfAppender() {
        super();
    }

    @Override
    public void setAdditionalFields(String additionalFields) {
        super.setAdditionalFields(additionalFields);
    }

    @Override
    public int getGraylogPort() {
        return super.getGraylogPort();
    }

    @Override
    public void setGraylogPort(int graylogPort) {
        super.setGraylogPort(graylogPort);
    }

    @Override
    public String getGraylogHost() {
        return super.getGraylogHost();
    }

    @Override
    public void setGraylogHost(String graylogHost) {
        super.setGraylogHost(graylogHost);
    }

    @Override
    public String getAmqpURI() {
        return super.getAmqpURI();
    }

    @Override
    public void setAmqpURI(String amqpURI) {
        super.setAmqpURI(amqpURI);
    }

    @Override
    public String getAmqpExchangeName() {
        return super.getAmqpExchangeName();
    }

    @Override
    public void setAmqpExchangeName(String amqpExchangeName) {
        super.setAmqpExchangeName(amqpExchangeName);
    }

    @Override
    public String getAmqpRoutingKey() {
        return super.getAmqpRoutingKey();
    }

    @Override
    public void setAmqpRoutingKey(String amqpRoutingKey) {
        super.setAmqpRoutingKey(amqpRoutingKey);
    }

    @Override
    public int getAmqpMaxRetries() {
        return super.getAmqpMaxRetries();
    }

    @Override
    public void setAmqpMaxRetries(int amqpMaxRetries) {
        super.setAmqpMaxRetries(amqpMaxRetries);
    }

    @Override
    public String getFacility() {
        return super.getFacility();
    }

    @Override
    public void setFacility(String facility) {
        super.setFacility(facility);
    }

    @Override
    public boolean isExtractStacktrace() {
        return super.isExtractStacktrace();
    }

    @Override
    public void setExtractStacktrace(boolean extractStacktrace) {
        super.setExtractStacktrace(extractStacktrace);
    }

    @Override
    public String getOriginHost() {
        return super.getOriginHost();
    }

    @Override
    public void setOriginHost(String originHost) {
        super.setOriginHost(originHost);
    }

    @Override
    public boolean isAddExtendedInformation() {
        return super.isAddExtendedInformation();
    }

    @Override
    public void setAddExtendedInformation(boolean addExtendedInformation) {
        super.setAddExtendedInformation(addExtendedInformation);
    }

    @Override
    public boolean isIncludeLocation() {
        return super.isIncludeLocation();
    }

    @Override
    public void setIncludeLocation(boolean includeLocation) {
        super.setIncludeLocation(includeLocation);
    }

    @Override
    public Map<String, String> getFields() {
        return super.getFields();
    }

    @Override
    public Object transformExtendedField(String field, Object object) {
        return super.transformExtendedField(field, object);
    }

    @Override
    public void activateOptions() {
        super.activateOptions();
    }

    @Override
    protected GelfUDPSender getGelfUDPSender(String udpGraylogHost, int graylogPort) throws IOException {
        return super.getGelfUDPSender(udpGraylogHost, graylogPort);
    }

    @Override
    protected GelfTCPSender getGelfTCPSender(String tcpGraylogHost, int graylogPort) throws IOException {
        return super.getGelfTCPSender(tcpGraylogHost, graylogPort);
    }

    @Override
    protected GelfAMQPSender getGelfAMQPSender(String amqpURI, String amqpExchangeName, String amqpRoutingKey, int amqpMaxRetries) throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        return super.getGelfAMQPSender(amqpURI, amqpExchangeName, amqpRoutingKey, amqpMaxRetries);
    }

    @Override
    public GelfSender getGelfSender() {
        return super.getGelfSender();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean requiresLayout() {
        return super.requiresLayout();
    }

    @Override
    public void addFilter(Filter newFilter) {
        super.addFilter(newFilter);
    }

    @Override
    public void clearFilters() {
        super.clearFilters();
    }

    @Override
    public void finalize() {
        super.finalize();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return super.getErrorHandler();
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    @Override
    public Layout getLayout() {
        return super.getLayout();
    }

    @Override
    public Priority getThreshold() {
        return super.getThreshold();
    }

    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        return super.isAsSevereAsThreshold(priority);
    }

    @Override
    public synchronized void doAppend(LoggingEvent event) {
        super.doAppend(event);
    }

    @Override
    public synchronized void setErrorHandler(ErrorHandler eh) {
        super.setErrorHandler(eh);
    }

    @Override
    public void setLayout(Layout layout) {
        super.setLayout(layout);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setThreshold(Priority threshold) {
        super.setThreshold(threshold);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
