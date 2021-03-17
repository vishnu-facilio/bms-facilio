package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum CustomModuleActivityType implements ActivityType {
	ADD(100) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added ";
		}
	},
	UPDATE(101) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the record";
		}
	},
	;

	private CustomModuleActivityType(int value) {
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
