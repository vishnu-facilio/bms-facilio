package com.facilio.activity;

import java.util.Collections;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
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
		
		for (ActivityType type : AssetActivityType.values()) {
			activityMap.put(type.getValue(), type);
		}
		for (ActivityType type : WorkOrderActivityType.values()) {
			activityMap.put(type.getValue(), type);
		}
		for (ActivityType type : ItemActivityType.values()) {
			activityMap.put(type.getValue(), type);
		}
		for (ActivityType type: AlarmActivityType.values()) {
			activityMap.put(type.getValue(), type);
		}
		// Max number = 58

		return activityMap;
	}
}
