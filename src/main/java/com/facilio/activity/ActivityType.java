package com.facilio.activity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.AsssetActivityType;
import com.facilio.collections.UniqueMap;

public interface ActivityType {
	public int getValue();
	
	public String constructMessage(JSONObject json);
	
	public static ActivityType getActivityType (int value) {
		return ACTIVITY_MAP.get(value);
	}
	
	static final Map<Integer, ActivityType> ACTIVITY_MAP = Collections.unmodifiableMap(initActivityMap());
	static Map<Integer, ActivityType> initActivityMap() {
		Map<Integer, ActivityType> activityMap = new UniqueMap<>();
		
		for (ActivityType type : AsssetActivityType.values()) {
			activityMap.put(type.getValue(), type);
		}
		
		return activityMap;
	}
}
