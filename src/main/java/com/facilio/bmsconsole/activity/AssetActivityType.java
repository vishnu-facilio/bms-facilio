package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum AssetActivityType implements ActivityType {
	LOCATION(1) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Location was updated to "+json.get("value");
		}
	}
	;

	private AssetActivityType(int value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	private int value = -1;
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public abstract String constructMessage(JSONObject json);
}
