package com.facilio.logging;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FacilioLogHandler extends Handler {

    private static final HashMap<String, Logger> LOGGER_MAP = new HashMap<>();
    private static final HashMap<Level, Priority> LEVEL_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put(Level.INFO, org.apache.log4j.Level.INFO);
        LEVEL_MAP.put(Level.FINE, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.FINER, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.FINEST, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.ALL, org.apache.log4j.Level.DEBUG);
        LEVEL_MAP.put(Level.WARNING, org.apache.log4j.Level.WARN);
        LEVEL_MAP.put(Level.SEVERE, org.apache.log4j.Level.ERROR);
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

    private static final File ROOT_FILE = new File("/");
    private static long lastFreeSpaceCheckedTime = System.currentTimeMillis();
    private static final long FREE_SPACE_THRESHOLD = 10000000000L;
    private static long freeSpace = ROOT_FILE.getFreeSpace();
    private static final String DEFAULT_ORG_USER_ID = "-1";
    public static boolean isLoggable(LoggingEvent event) {
        if((lastFreeSpaceCheckedTime + 300_000L) < System.currentTimeMillis()) {
            lastFreeSpaceCheckedTime = System.currentTimeMillis();
            freeSpace = ROOT_FILE.getFreeSpace();
        }

        return (
                (   (event.getLevel().toInt() > org.apache.log4j.Level.INFO_INT)
                        && (AccountUtil.getCurrentAccount() == null || (event.getLevel().toInt() > AccountUtil.getCurrentAccount().getLevel().toInt()))
                )
                        && (freeSpace > FREE_SPACE_THRESHOLD)
        );
    }

    public static LoggingEvent addEventProps(LoggingEvent event) {
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
        return event;
    }
}
