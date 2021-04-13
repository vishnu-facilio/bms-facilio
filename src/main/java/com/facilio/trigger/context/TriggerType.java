package com.facilio.trigger.context;

import java.util.Map;

import com.facilio.chain.FacilioContext;

public enum TriggerType implements TriggerTypeInterface {
	
	SLA_DUE_DATE_TRIGGER {
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
	SCORING_RULE_TRIGGER {
		@Override
		public Map<String, Object> getParamsMap(FacilioContext context) {
			return null;
		}
	},
    TIMESERIES_COMPLETED_TRIGGER {
		@Override
		public Map<String, Object> getParamsMap(FacilioContext context) {
			return null;
		}
	}
	;
	
	public int getValue() {
		return ordinal()+1;
	}
	public static TriggerType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}