package com.facilio.bmsconsole.activity;

import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;
import com.facilio.constants.FacilioConstants;

public enum WorkOrderActivityType implements ActivityType {
	CLOSE_ALL_TASK(2) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " closed all the tasks ";
		}
	},
	CLOSE_FILTERED_TASK(32) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String filteredName = (String) json.get(FacilioConstants.ContextNames.FILTERED_NAME);
			return " closed the tasks for " + filteredName;
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
			return " approved the Workorder ";
		}
	},
	REJECTED(7) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " rejected the Workorder ";
		}
	},
	ADD_TASK(8) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added the Task ";
		}	
	},
	ADD_COMMENT(9) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added a Comment ";
		}	
	},
	ADD_ATTACHMENT(10) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added the Attachment ";
		}	
	},
	ADD_TASK_ATTACHMENT(11) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added Task Attachment";
		}	
	},
	UPDATE_TASK(12) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the Task";
		}	
	},
	CLOSE_TASK(23) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " closed a task ";
		}
	},
	REOPEN_TASK(25) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " reopen the task ";
		}
	},
	ADD_PM_WO(24) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " added workorder through PM ";
		}
	},
	UPDATE_STATUS(26) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated ";
		}
	},
	UPDATE_PREREQUISITE(35) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated the Prerequisite";
		}	
	},ADD_PREERQUISITE_PHOTO(36) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "  added Prerequisite Photo";
		}	
	},PREREQUISITE_APPROVE(37) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " approved Prerequisite";
		}	
	},
	REMOVE_PREERQUISITE_PHOTO(38){
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " removed Prerequisite Photo";
		}	
	},
	READING_CORRECTION (42) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " corrected reading";
		}
	},
	VENDOR_ASSIGNED(59) {

		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " assigned to vendor ";
		}
		
	},CREATION (89) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " Created ";
		}
	},
	SLA_MEET(60) {
		@Override
		public String constructMessage(JSONObject json) {
			return "SLA Met";
		}
	},
	SLA_ESCALATION_TRIGGERED(61) {
		@Override
		public String constructMessage(JSONObject json) {
			return "SLA Escalation Triggered";
		}
	},
	SLA_ACTIVATED(62) {
		@Override
		public String constructMessage(JSONObject json) {
			return "Service-level agreement has been activated";
		}
	},
	DELETE_TASK_ATTACHMENT(82) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " deleted Task Attachment";
		}	
	},
	UPDATE_COMMENT(118) {
		@Override
		public String constructMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return " updated a Comment ";
		}
	},
	;
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
