package com.facilio.trigger.context;

import java.util.Map;

import com.facilio.chain.FacilioContext;

public interface TriggerTypeInterface {

	public Map<String,Object> getParamsMap(FacilioContext context);
}
