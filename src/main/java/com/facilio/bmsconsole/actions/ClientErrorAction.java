package com.facilio.bmsconsole.actions;

import java.util.Map;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import com.facilio.aws.util.AwsUtil;

public class ClientErrorAction extends FacilioAction {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ClientErrorAction.class.getName());
    private static Appender appender;
    private static final String APPENDER_NAME = "facilioAppender";

    public void init() {
        appender = logger.getAppender(APPENDER_NAME);
    }

    private String type;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
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

    public Map<String, Object> info;
    public Map<String, Object> getInfo() {
        return info;
    }
    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public Map<String, Object> ua;
    public Map<String, Object> getUa() {
        return ua;
    }
    public void setUa(Map<String, Object> ua) {
        this.ua = ua;
    }

    public boolean isNotNull(Map<String, Object> value) {
        return !(value == null || value.isEmpty());
    }

    public String logClientError() {
        if (AwsUtil.isProduction()) {
            return SUCCESS;
        }
        LoggingEvent event = new LoggingEvent(logger.getName(), logger, Level.INFO, message, null);

        event.setProperty("type", this.type);
        event.setProperty("stacktrace", this.stacktrace);
        event.setProperty("url", this.url);
        event.setProperty("browser", this.browser);
        event.setProperty("userId", this.userId);
        event.setProperty("orgId", this.orgId);

        if (this.isNotNull(this.info)) {
            event.setProperty("info", this.info.toString());
        }
        if (this.isNotNull(this.ua)) {
            event.setProperty("ua", this.ua.toString());
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