package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum WorkOrderActivityType implements ActivityType {
	CLOSE_ALL_TASK(2) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " closed all the tasks ";
		}
	},
	ASSIGN(3) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " assigned ";
		}
	},
	ADD(4) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added ";
		}
	},
	UPDATE(5) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated ";
		}
	},
	APPROVED(6) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " approved Workorder ";
		}
	},
	REJECTED(7) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " rejected Workorder ";
		}
	};
	private int value;

	private WorkOrderActivityType(int value) {
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
