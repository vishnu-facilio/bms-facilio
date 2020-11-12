package com.facilio.trigger.context;

import java.util.Map;

import com.facilio.chain.FacilioContext;

public enum Trigger_Type implements TriggerTypeInterface {
	
	SCHEDULED {
		@Override
		public Map<String, Object> getParamsMap(FacilioContext context) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	MODULE_TRIGGER {
		@Override
		public Map<String, Object> getParamsMap(FacilioContext context) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	CHILD_MODULE_TRIGGER {
		@Override
		public Map<String, Object> getParamsMap(FacilioContext context) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	;
	
	public int getValue() {
		return ordinal()+1;
	}
	public static Trigger_Type valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}