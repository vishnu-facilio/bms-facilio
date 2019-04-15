package com.facilio.bmsconsole.activity;

import com.facilio.activity.ActivityType;
import org.json.simple.JSONObject;

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
			return " added the Comment ";
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
