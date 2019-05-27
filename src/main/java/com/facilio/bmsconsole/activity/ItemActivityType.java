package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

public enum ItemActivityType implements ActivityType {
	ADD_ITEM(13) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added item ";
		}
	},
	ISSUED(14) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "issued item ";
		}
	},
	RETURN(15) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "issued item ";
		}
	},
	REQUESTED(16) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "requested item ";
		}
	},
	ITEM_APPROVED(17) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "approved item ";
		}
	},
	ITEM_REJECTED(18) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "rejected item ";
		}
	},
	ITEM_ATTACHMENT(19) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added item attachment";
		}
	},
	ITEM_NOTES(20) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added item notes";
		}
	},
	ITEMTYPES_UPDATE(21) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "updated item types";
		}
	},
	ITEMTYPES_ADD(22) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added item types";
		}
	};

	private ItemActivityType(int value) {
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
