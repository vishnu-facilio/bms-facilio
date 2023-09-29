package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.modules.FacilioStatus;

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
	ASSET_NOTES_UPDATE(119) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated a Comment ";
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
	RESET_READING(39) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the asset";
		}
	},
	CREATE_MOVEMENT(40) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " initiated a move request #" + json.get("movementId");
		}
	},
	UPDATE_MOVEMENT(41) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				FacilioStatus status = StateFlowRulesAPI.getStateContext((long)json.get("newStatus"));
				return " updated the move request #" + json.get("movementId") +" status to " + status.getDisplayName();
			}
			catch(Exception e) {
				return " updated the move request #" + json.get("movementId") ;
			}
		}
	},
	ISSUE(43) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " issued asset to " + json.get("issuedTo");
		}
	},
	RETURN(44) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " marked as Returned by " + json.get("returnedBy");
		}
	},
	USE(45) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String history = "issued the asset to WO #" + json.get("woId") + ", moved to " + json.get("site");
			if(json.get("space")!=null){
				history += ", " + json.get("space");
			}
			return history;
		}
	},
	ADDED_TO_INVENTORY(46) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added as rotating asset to the Storeroom - " + json.get("storeroom");
		}
	},
	MOVE_TO_STOREROOM(121){
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "moved asset to the Storeroom - " + json.get("storeroom");
		}
	},
	USE_IN_SO(150) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String history = "issued the asset to SO #" + json.get("soId") + ", moved to " + json.get("site");
			if(json.get("space")!=null){
				history += ", " + json.get("space");
			}
			return history;
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
