package com.facilio.bacnet;

public class BACNetUtil {
	
    public static enum InstanceType {
	    	ANALOG_INPUT,
	    	ANALOG_OUTPUT(true),
	    	ANALOG_VALUE,
	    	BINARY_INPUT,
	    	BINARY_OUTPUT(true),
	    	BINARY_VALUE,
	    	CALENDAR,
	    	COMMAND,
	    	DEVICE,
	    	EVENT_ENROLLMENT,
	    	FILE,
	    	GROUP,
	    	LOOP,
	    	MULTI_STATE_INPUT,
	    	MULTI_STATE_OUTPUT(true),
	    	NOTIFICATION_CLASS,
	    	PROGRAM,
	    	SCHEDULE,
	    	AVERAGING,
	    	MULTI_STATE_VALUE,
	    	TREND_LOG,
	    	LIFE_SAFETY_POINT,
	    	LIFE_SAFETY_ZONE,
	    	ACCUMULATOR,
	    	PULSE_CONVERTER,
	    	EVENT_LOG,
	    	GLOBAL_GROUP,
	    	TREND_LOG_MULTIPLE,
	    	LOAD_CONTROL,
	    	STRUCTURED_VIEW,
	    	ACCESS_DOOR,
	    	TIMER,
	    	ACCESS_CREDENTIAL,
	    	ACCESS_POINT,
	    	ACCESS_RIGHTS,
	    	ACCESS_USER,
	    	ACCESS_ZONE,
	    	CREDENTIAL_DATA_INPUT,
	    	NETWORK_SECURITY,
	    	BITSTRING_VALUE,
	    	CHARACTERSTRING_VALUE,
	    	DATE_PATTERN_VALUE,
	    	DATE_VALUE,
	    	DATETIME_PATTERN_VALUE,
	    	DATETIME_VALUE,
	    	INTEGER_VALUE,
	    	LARGE_ANALOG_VALUE,
	    	OCTETSTRING_VALUE,
	    	POSITIVE_INTEGER_VALUE,
	    	TIME_PATTERN_VALUE,
	    	TIME_VALUE,
	    	NOTIFICATION_FORWARDER,
	    	ALERT_ENROLLMENT,
	    	CHANNEL,
	    	LIGHTING_OUTPUT(true),
	    	BINARY_LIGHTING_OUTPUT(true),
	    	NETWORK_PORT,
	    	ELEVATOR_GROUP,
	    	ESCALATOR,
	    	LIFT
		;
    	
    		private boolean writable;
    		
    		InstanceType() {};
    		
    		InstanceType(boolean isWritable) {
    			this.writable = isWritable;
    		}
		
		public int getValue() {
			return ordinal();
		}
		
		public boolean isWritable() {
			return writable;
		}
		
		public static InstanceType valueOf (int value) {
			if (value >= 0 && value < values().length) {
				return values() [value];
			}
			return null;
		}
	}
}
