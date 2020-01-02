package com.facilio.agentv2;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class JsonUtil {

    private static final Logger LOGGER = LogManager.getLogger(JsonUtil.class.getName());


    public static Long getLong(Object value){
        if(value == null){
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.valueOf((String) value);
            } else if (value instanceof Boolean) {
                return (long) ((Boolean) value ? 1 : 0);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
        LOGGER.info("Exception occurred, failed to cast it to Long , returning -1 ");
        return -1L;
    }

    public static Integer getInt(Object value){
        if(value == null){
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.valueOf((String) value);
            } else if (value instanceof Boolean) {
                return ((Boolean) value ? 1 : 0);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
        LOGGER.info("Exception occurred, failed to cast it to Integer , returning -1 ");
            return -1;
    }

    public static  Boolean getBoolean(Object value){
        if(value == null){
            return false;
        }
        try {
            if (value instanceof Boolean) {
                return (Boolean)value;
            } else if (value instanceof Long) {
                return (Long) value > 0;
            } else if (value instanceof Integer) {
                return (Integer) value > 0;
            }
            else if( value instanceof String){
                return Boolean.parseBoolean((String) value);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
        LOGGER.info("Exception occurred, failed to cast it to Integer , returning -1 ");
        return false;
    }
}
