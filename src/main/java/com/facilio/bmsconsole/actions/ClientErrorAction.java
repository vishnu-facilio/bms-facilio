package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class ClientErrorAction extends FacilioAction {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ClientErrorAction.class.getName());
    private static Appender appender;
    private static final String APPENDER_NAME = "clientAppender";

    public void init() {
        appender = logger.getAppender(APPENDER_NAME);
    }

    private String ceType;
    public String getCeType() {
        return ceType;
    }
    public void setCeType(String ceType) {
        this.ceType = ceType;
    }

    private String route;
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    private String stacktrace;
    public String getStacktrace() {
        return stacktrace;
    }
    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    private String browser;
    public String getBrowser() {
        return browser;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    private String os;
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

    public Map<String, Object> ceInfo;
    public Map<String, Object> getCeInfo() {
        return ceInfo;
    }
    public void setCeInfo(Map<String, Object> ceInfo) {
        this.ceInfo = ceInfo;
    }

    public Map<String, Object> ceUa;
    public Map<String, Object> getCeUa() {
        return ceUa;
    }
    public void setCeUa(Map<String, Object> ceUa) {
        this.ceUa = ceUa;
    }

    public boolean isNotNull(Map<String, Object> value) {
        return !(value == null || value.isEmpty());
    }

    public String logClientError() {
        LoggingEvent event = new LoggingEvent(logger.getName(), logger, Level.INFO, message, null);

        if (this.route != null) {
            event.setProperty("route", this.route);
        }
        if (this.browser != null) {
            event.setProperty("browser", this.browser);
        }
        if (this.os != null) {
            event.setProperty("os", this.os);
        }

        event.setProperty("ceType", this.ceType);
        event.setProperty("stacktrace", this.stacktrace);
        event.setProperty("userId", this.userId);
        event.setProperty("orgId", this.orgId);

        if (this.isNotNull(this.ceInfo)) {
            event.setProperty("ceInfo", this.ceInfo.toString());
        }
        if (this.isNotNull(this.ceUa)) {
            event.setProperty("ceUa", this.ceUa.toString());
        }

        if(appender != null) {
            appender.doAppend(event);
        } else {
            logger.callAppenders(event);
        }
        setResult("result", "success");
        return SUCCESS;
    }
}