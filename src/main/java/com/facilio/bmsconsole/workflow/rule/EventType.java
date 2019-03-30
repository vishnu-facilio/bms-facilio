package com.facilio.bmsconsole.workflow.rule;

import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventType {
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
			json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "created a new "+moduleName;
			return "created Work Order";
		}
	},
	EDIT(1 << 1) {
		@Override
		public String getMessage(JSONObject json) {
			json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "updated the "+moduleName;
			return "updated Work Order";
		}
	},
	DELETE(1 << 2) {
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
	APPROVE_WORK_ORDER_REQUEST(1 << 3) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "approved the request";
		}
	},
	ASSIGN_TICKET(1 << 4) {
		@Override
		public String getMessage(JSONObject json) {
			json.get(FacilioConstants.ContextNames.MODULE_NAME);
			//return "assigned the "+moduleName+" to ";
			return "assigned the Work Order to ";
		}
	},
	ADD_TICKET_NOTE(1 << 5) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added a comment";
		}
	},
	CLOSE_WORK_ORDER(1 << 6) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "closed the "+moduleName;
		}
	},
	ADD_TICKET_ATTACHMENTS(1 << 7) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "attached";
		}
	},
	ADD_TICKET_TASKS(1 << 8) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the task.";
		}
	},
	ADD_TASK_INPUT(1 << 9) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added the reading value";
		}
	},
	UPDATED_ALARM_SEVERITY(1 << 10) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "updated the alarm severity";
		}
		
	},
	ALARM_CLEARED(1 << 11) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "cleared the alarm";
		}
		
	},
	REJECT_WORK_ORDER_REQUEST(1 << 12) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "rejected the request";
		}
	},
	SOLVE_WORK_ORDER(1 << 13) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "solved the "+moduleName;
		}
	},
	UPDATE_TICKET_TASK(1 << 14) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Updated the task";
		}
		
	},
	CLOSE_WORK_ORDER_REQUEST(1 << 15) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Closed the request";
		}
	},
//	CREATE_WORK_REQUEST(1 << 16) {
//		@Override
//		public String getMessage(JSONObject json) {
//			// TODO Auto-generated method stub
//			return "Create work request";
//		}
//	},
	CLOSE_ALL_TASK(1 << 17) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "Close all task";
		}
	},
	CREATE_OR_UPDATE_ALARM_SEVERITY(CREATE.getValue() + UPDATED_ALARM_SEVERITY.getValue()) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	HOLD_WORK_ORDER(1 << 18) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			String moduleName = (String) json.get(FacilioConstants.ContextNames.MODULE_NAME);
			return "put on hold the "+moduleName;
		}
	},
	SCHEDULED (1 << 19) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
		
	},
	FIELD_CHANGE (1 << 20) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
		
	},
	SCHEDULED_READING_RULE (1 << 21) {

		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
		
	},
	;

    private int eventType;
    EventType(int eventType) {
        this.eventType = eventType;
    }
    public int getValue() {
        return eventType;
    }
    public static EventType valueOf(int eventTypeVal) {
    	return TYPE_MAP.get(eventTypeVal);
    }
    
    public boolean isPresent(long activity) {
    	return (activity & this.eventType) == this.eventType;
    }
   
    public abstract String getMessage(JSONObject json);
    
    private static final Map<Integer, EventType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, EventType> initTypeMap() {
		Map<Integer, EventType> typeMap = new HashMap<>();
		
		for(EventType type : values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	public Map<Integer, EventType> getAllTypes() {
		return TYPE_MAP;
	}
}
