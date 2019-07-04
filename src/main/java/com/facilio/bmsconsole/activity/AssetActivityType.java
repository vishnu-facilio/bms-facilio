package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;

public enum AssetActivityType implements ActivityType {
	LOCATION(1) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Location was updated to "+json.get("value");
		}
	},
	ADD(27) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added the asset";
		}
	},
	UPDATE(28) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the asset";
		}
	},
	ASSET_NOTES(29) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added a Comment ";
		}
	},
	ADD_ATTACHMENT(30) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added the Attachment ";
		}	
	},
	UPDATE_STATUS(33) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated ";
		}
	},
	ASSET_DOWNTIME(34) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the asset";
		}
	},
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
