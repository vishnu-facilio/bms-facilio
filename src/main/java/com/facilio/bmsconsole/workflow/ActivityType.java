package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;

public enum ActivityType {
	/* Bitwise calculation
	 * 
	 * var workflowRule = currentWorkflowRule;
	 * 
	 * If ((workflowRule.eventType & CREATE) == CREATE) {
	 * 		Workflow rule needs to be executed for create action
	 * } 
	 * 
	 */
	CREATE(1) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "created a new "+moduleName;
			return "created Work Order";
		}
	},
	EDIT(2) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "updated the "+moduleName;
			return "updated Work Order";
		}
	},
	DELETE(4) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	CREATE_OR_EDIT(CREATE.getValue() + EDIT.getValue()) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	APPROVE_WORK_ORDER_REQUEST(8) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "approved the request";
		}
	},
	ASSIGN_TICKET(16) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "assigned the "+moduleName+" to ";
			return "assigned the Work Order to ";
		}
	},
	ADD_TICKET_NOTE(32) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added a comment";
		}
	},
	CLOSE_WORK_ORDER(64) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "closed the "+moduleName;
		}
	},
	ADD_TICKET_ATTACHMENTS(128) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "attached";
		}
	},
	ADD_TICKET_TASKS(256) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the task.";
		}
	},
	ADD_TASK_INPUT(512) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the reading value";
		}
	},
	UPDATED_ALARM_SEVERITY(1024) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "updated the alarm severity";
		}
		
	},
	ALARM_CLEARED(2048) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "cleared the alarm";
		}
		
	},
	REJECT_WORK_ORDER_REQUEST(4096) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "rejected the request";
		}
	},
	SOLVE_WORK_ORDER(8192) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "solved the "+moduleName;
		}
	},
	UPDATE_TICKET_TASK(16384) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Updated the task";
		}
		
	},
	CLOSE_WORK_ORDER_REQUEST(32768) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Closed the request";
		}
	},
	CREATE_WORK_REQUEST(65536) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Create work request";
		}
	},
	CLOSE_ALL_TASK(131072) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Close all task";
		}
	}
	;

    private int eventType;

    ActivityType(int eventType) {
        this.eventType = eventType;
    }

    public int getValue() {
        return eventType;
    }
    
    public static ActivityType valueOf(int eventTypeVal) {
    	return TYPE_MAP.get(eventTypeVal);
    }
    
    public abstract String getMessage(JSONObject json);
    
    private static final Map<Integer, ActivityType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, ActivityType> initTypeMap() {
		Map<Integer, ActivityType> typeMap = new HashMap<>();
		
		for(ActivityType type : values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	public Map<Integer, ActivityType> getAllTypes() {
		return TYPE_MAP;
	}
}
