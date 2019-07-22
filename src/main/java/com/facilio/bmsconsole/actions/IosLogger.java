package com.facilio.bmsconsole.actions;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Map;

public class IosLogger extends FacilioAction {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(IosLogger.class.getName());
    private static Appender appender;
    private static final String APPENDER_NAME = "iosAppender";

    public void init() {
        appender = logger.getAppender(APPENDER_NAME);
    }

    private String message = "-";
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private String appVersion = "-";
    public String getappVersion() {
        return appVersion;
    }
    public void setappVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    private String className = "-";
    public String getclassName() {
        return className;
    }
    public void setclassName(String className) {
        this.className = className;
    }

    private String stacktrace = "-";
    public String getStacktrace() {
        return stacktrace;
    }
    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    private String os = "-";
    public String getOs() {
        return os;
    }
    public void setOs(String os) {
        this.os = os;
    }

    private String userId = "-1";
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        if (userId != null) {
            this.userId = userId;
        }
    }

    private String orgId = "-1";
    public String getOrgId() {
        return orgId;
    }
    public void setOrgId(String orgId) {
        if (orgId != null) {
            this.orgId = orgId;
        }
    }

    public boolean isNotNull(Map<String, Object> value) {
        return !(value == null || value.isEmpty());
    }

    public String logError() {
        LoggingEvent event = new LoggingEvent(logger.getName(), logger, Level.INFO, message, null);

        event.setProperty("appVersion", this.appVersion);
        event.setProperty("className", this.className);
        event.setProperty("stacktrace", this.stacktrace);
        event.setProperty("os", this.os);
        event.setProperty("userId", this.userId);
        event.setProperty("orgId", this.orgId);

        if(appender != null) {
            appender.doAppend(event);
        } else {
            logger.callAppenders(event);
        }
        setResult("result", "success");
        return SUCCESS;
    }
}