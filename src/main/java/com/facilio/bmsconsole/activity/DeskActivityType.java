package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum DeskActivityType implements ActivityType {

    ASSIGN_EMPLOYEE(109) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " assigned the desk to ";
		}
	},
    
    UNASSIGN_EMPLOYEE(110) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " unassigned the desk to ";
		}
	}
    ;


	private int value;

	private DeskActivityType(int value) {
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
