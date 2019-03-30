package com.facilio.logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

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
}
