package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum QAndAActivityType implements ActivityType {

    ANSWERED(111) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " Answered question(q) as value ";
		}
	},
    
    ANSWER_UPDATED(112) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated Answered question(q) as value ";
		}
	},
    ;


	private int value;

	private QAndAActivityType(int value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public abstract String constructMessage(JSONObject json);
}
