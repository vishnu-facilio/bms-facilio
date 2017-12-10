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
			return "created a new Work Order";
		}
	},
	EDIT(2) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "updated the "+moduleName;
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
			return "assigned the "+moduleName+" to ";
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
			return "added the following file(s).";
		}
	},
	ADD_TICKET_TASKS(256) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the following task.";
		}
	},
	ADD_TASK_READING_VALUE(512) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the reading value";
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
