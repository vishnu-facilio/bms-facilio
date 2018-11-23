package com.facilio.bacnet;

import java.util.HashMap;

public class BACNetUtil {
    
    private static final HashMap<Integer, String> OBJECT_TYPES = new HashMap<>();
    
    static {
        loadObjectTypes();
    }

    private static void loadObjectTypes() {
        OBJECT_TYPES.put(0, "ANALOG_INPUT");
        OBJECT_TYPES.put(1, "ANALOG_OUTPUT");
        OBJECT_TYPES.put(2, "ANALOG_VALUE");
        OBJECT_TYPES.put(3, "BINARY_INPUT");
        OBJECT_TYPES.put(4, "BINARY_OUTPUT");
        OBJECT_TYPES.put(5, "BINARY_VALUE");
        OBJECT_TYPES.put(6, "CALENDAR");
        OBJECT_TYPES.put(7, "COMMAND");
        OBJECT_TYPES.put(8, "DEVICE");
        OBJECT_TYPES.put(9, "EVENT_ENROLLMENT");
        OBJECT_TYPES.put(10, "FILE");
        OBJECT_TYPES.put(11, "GROUP");
        OBJECT_TYPES.put(12, "LOOP");
        OBJECT_TYPES.put(13, "MULTI_STATE_INPUT");
        OBJECT_TYPES.put(14, "MULTI_STATE_OUTPUT");
        OBJECT_TYPES.put(15, "NOTIFICATION_CLASS");
        OBJECT_TYPES.put(16, "PROGRAM");
        OBJECT_TYPES.put(17, "SCHEDULE");
        OBJECT_TYPES.put(18, "AVERAGING");
        OBJECT_TYPES.put(19, "MULTI_STATE_VALUE");
        OBJECT_TYPES.put(20, "TREND_LOG");
        OBJECT_TYPES.put(21, "LIFE_SAFETY_POINT");
        OBJECT_TYPES.put(22, "LIFE_SAFETY_ZONE");
        OBJECT_TYPES.put(23, "ACCUMULATOR");
        OBJECT_TYPES.put(24, "PULSE_CONVERTER");
        OBJECT_TYPES.put(25, "EVENT_LOG");
        OBJECT_TYPES.put(26, "GLOBAL_GROUP");
        OBJECT_TYPES.put(27, "TREND_LOG_MULTIPLE");
        OBJECT_TYPES.put(28, "LOAD_CONTROL");
        OBJECT_TYPES.put(29, "STRUCTURED_VIEW");
        OBJECT_TYPES.put(30, "ACCESS_DOOR");
        OBJECT_TYPES.put(31, "TIMER");
        OBJECT_TYPES.put(32, "ACCESS_CREDENTIAL");
        OBJECT_TYPES.put(33, "ACCESS_POINT");
        OBJECT_TYPES.put(34, "ACCESS_RIGHTS");
        OBJECT_TYPES.put(35, "ACCESS_USER");
        OBJECT_TYPES.put(36, "ACCESS_ZONE");
        OBJECT_TYPES.put(37, "CREDENTIAL_DATA_INPUT");
        OBJECT_TYPES.put(38, "NETWORK_SECURITY");
        OBJECT_TYPES.put(39, "BITSTRING_VALUE");
        OBJECT_TYPES.put(40, "CHARACTERSTRING_VALUE");
        OBJECT_TYPES.put(41, "DATE_PATTERN_VALUE");
        OBJECT_TYPES.put(42, "DATE_VALUE");
        OBJECT_TYPES.put(43, "DATETIME_PATTERN_VALUE");
        OBJECT_TYPES.put(44, "DATETIME_VALUE");
        OBJECT_TYPES.put(45, "INTEGER_VALUE");
        OBJECT_TYPES.put(46, "LARGE_ANALOG_VALUE");
        OBJECT_TYPES.put(47, "OCTETSTRING_VALUE");
        OBJECT_TYPES.put(48, "POSITIVE_INTEGER_VALUE");
        OBJECT_TYPES.put(49, "TIME_PATTERN_VALUE");
        OBJECT_TYPES.put(50, "TIME_VALUE");
        OBJECT_TYPES.put(51, "NOTIFICATION_FORWARDER");
        OBJECT_TYPES.put(52, "ALERT_ENROLLMENT");
        OBJECT_TYPES.put(53, "CHANNEL");
        OBJECT_TYPES.put(54, "LIGHTING_OUTPUT");
        OBJECT_TYPES.put(55, "BINARY_LIGHTING_OUTPUT");
        OBJECT_TYPES.put(56, "NETWORK_PORT");
        OBJECT_TYPES.put(57, "ELEVATOR_GROUP");
        OBJECT_TYPES.put(58, "ESCALATOR");
        OBJECT_TYPES.put(59, "LIFT");
    }

    public static String getObjectType(int objectType) {
        return OBJECT_TYPES.get(objectType);
    }
}
