package com.facilio.logging;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;

public class FacilioLogHandler extends Handler {

    private static final HashMap<String, Logger> LOGGER_MAP = new HashMap<>();
    private static final HashMap<Level, Priority> LEVEL_MAP = new HashMap<>();
    private static final HashMap<org.apache.log4j.Level, Integer> LEVEL_POS_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put(Level.INFO, org.apache.log4j.Level.INFO);
        LEVEL_MAP.put(Level.FINE, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.FINER, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.FINEST, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.ALL, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.WARNING, org.apache.log4j.Level.WARN);
        LEVEL_MAP.put(Level.SEVERE, org.apache.log4j.Level.ERROR);

        LEVEL_POS_MAP.put(org.apache.log4j.Level.ALL, 0);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.TRACE, 1);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.DEBUG, 2);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.INFO, 3);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.WARN, 4);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.ERROR, 5);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.FATAL, 6);
        LEVEL_POS_MAP.put(org.apache.log4j.Level.OFF, 7);
    }

    public void publish(LogRecord record) {
        if(record.getLevel().intValue() > Level.FINE.intValue()) {
            String loggerName = record.getLoggerName();
            Logger logger = LOGGER_MAP.getOrDefault(loggerName, LogManager.getLogger(loggerName));
            logger.log(LEVEL_MAP.getOrDefault(record.getLevel(), org.apache.log4j.Level.INFO), record.getMessage(), record.getThrown());
            LOGGER_MAP.put(loggerName, logger);
        } else {
            // forget
        }
    }

    public void flush() {

    }

    public void close() throws SecurityException {

    }

    private static final File ROOT_FILE = new File(File.separator);
    private static long lastFreeSpaceCheckedTime = System.currentTimeMillis();
    private static final long FREE_SPACE_THRESHOLD = 5_000_000_000L;
    private static long freeSpace = ROOT_FILE.getFreeSpace();
    private static final String DEFAULT_ORG_USER_ID = "-1";
    private static final String DEFAULT_IAM_USER_ID="-1";

    
    static boolean isLoggable(LoggingEvent event) {
    	return isLoggable(event,true);
    }
    
    static boolean isLoggable(LoggingEvent event,boolean checkDiskSize) {
        if((lastFreeSpaceCheckedTime + 300_000L) < System.currentTimeMillis()) {
            lastFreeSpaceCheckedTime = System.currentTimeMillis();
            freeSpace = ROOT_FILE.getFreeSpace();
        }
        boolean isLoggable = true;
        if (AccountUtil.getCurrentAccount() != null) {
            isLoggable = event.getLevel().toInt() >= AccountUtil.getCurrentAccount().getLevel().toInt();
        }
        else {
            isLoggable = event.getLevel().toInt() >= org.apache.log4j.Level.INFO_INT;
        }
        if (checkDiskSize) {
            return isLoggable && freeSpace > FREE_SPACE_THRESHOLD;
        }
        return isLoggable;
    }

    static LoggingEvent addEventProps(LoggingEvent event) {
       Organization org = AccountUtil.getCurrentOrg();
        if(org != null) {
            event.setProperty("orgId", String.valueOf(org.getOrgId()));
        } else {
            event.setProperty("orgId", DEFAULT_ORG_USER_ID);
        }
        User user = AccountUtil.getCurrentUser();
        if (user != null) {
            event.setProperty("userId", String.valueOf(user.getOuid()));
            event.setProperty("uid",String.valueOf(user.getUid()));
            if (StringUtils.isNotEmpty(user.getProxy())) {
                event.setProperty("proxy", user.getProxy());
            }
        } else {
            event.setProperty("userId", DEFAULT_ORG_USER_ID);
            event.setProperty("uid",DEFAULT_IAM_USER_ID);
        }

        String reqUri = event.getProperty(RequestUtil.REQUEST_URL);
        if (StringUtils.isEmpty(reqUri)) {
            if (AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getRequestUri() != null) {
                event.setProperty(RequestUtil.REQUEST_URL, AccountUtil.getCurrentAccount().getRequestUri());
            } else {
                event.setProperty(RequestUtil.REQUEST_URL, "-");
            }
        }
        try {
            WebTabContext currentTab = AccountUtil.getCurrentTab();
            if (currentTab != null) {
                event.setProperty("tab", String.valueOf(currentTab.getId()));
                if (currentTab.getTypeEnum() != null && currentTab.getTypeEnum().getTabType() != null) {
                    event.setProperty("tabTypeEnum", currentTab.getTypeEnum().getTabType().name());
                } else {
                    event.setProperty("tabTypeEnum", "-");
                }
            } else {
                event.setProperty("tab", "-");
                event.setProperty("tabTypeEnum", "-");
            }
        } catch (Exception e) {
            event.setProperty("exception", "LogAppenderException Tabs");
        }
        event.setProperty("region", FacilioProperties.getRegionCountryCode());
        
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
        return event;
    }

    public static int getLevelInt(org.apache.log4j.Level lev){
        return LEVEL_POS_MAP.getOrDefault(lev, -1);
    }

    public static org.apache.log4j.Level getLevelByInt(int loggerLevel) {

        for(Map.Entry<org.apache.log4j.Level, Integer> entry : LEVEL_POS_MAP.entrySet()){
            if (entry.getValue().equals(loggerLevel)){
                return entry.getKey();
            }
        }
        return null;
    }
}
