package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum AssetActivityType implements ActivityType {
	UPDATE(7) {
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
