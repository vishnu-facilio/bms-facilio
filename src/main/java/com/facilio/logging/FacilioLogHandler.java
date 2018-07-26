package com.facilio.logging;

import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;

public class FacilioLogHandler extends Handler {

    private static final HashMap<String, Logger> LOGGER_MAP = new HashMap<>();
    private static final HashMap<Level, Priority> LEVEL_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put(Level.INFO, Priority.INFO);
        LEVEL_MAP.put(Level.FINE, Priority.DEBUG);
        LEVEL_MAP.put(Level.FINER, Priority.DEBUG);
        LEVEL_MAP.put(Level.FINEST, Priority.DEBUG);
        LEVEL_MAP.put(Level.ALL, Priority.DEBUG);
        LEVEL_MAP.put(Level.WARNING, Priority.WARN);
        LEVEL_MAP.put(Level.SEVERE, Priority.ERROR);
    }

    public void publish(LogRecord record) {
        if(record.getLevel().intValue() > Level.INFO.intValue()) {
            String loggerName = record.getLoggerName();
            Logger logger = LOGGER_MAP.getOrDefault(loggerName, LogManager.getLogger(loggerName));
            logger.log(LEVEL_MAP.getOrDefault(record.getLevel(), Priority.INFO), record.getMessage(), record.getThrown());
            LOGGER_MAP.put(loggerName, logger);
        }
    }

    public void flush() {

    }

    public void close() throws SecurityException {

    }
}
