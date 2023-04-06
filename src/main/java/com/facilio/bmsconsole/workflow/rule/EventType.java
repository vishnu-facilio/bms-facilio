package com.facilio.bmsconsole.workflow.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;

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
	STATE_TRANSITION (1 << 22) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	READING_CORRECTION (1 << 23) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	CUSTOM_BUTTON (1 << 24) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	ASSET_LOCATION_CHANGE (1 << 25) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	SLA (1 << 25) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	ADD_NOTE_REQUESTER(1 << 26) {
		@Override
		public String getMessage(JSONObject json) {
			// TODO Auto-generated method stub
			return "added a comment";
		}
	},
	APPROVAL (1 << 27) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	TRANSACTION (1 << 28) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	SCORING_RULE(1 << 29) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	INVOKE_TRIGGER(1 << 30) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	TIMESERIES_COMPLETE(1 << 31) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_REPLY_RECIEVED(4294967296l) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_NOTE_ADDITION(8589934592l) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_NOTE_UPDATION(17179869184l) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_NOTE_DELETION(34359738368l) { // 2^35
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_ADMIN_REPLY(68719476736l) { //2^36
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	EMAIL_CONVERSATION_ON_ADMIN_NOTE_ADDITION(137438953472l) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	ANNOUNCEMENT_PUBLISH(274877906944l) {
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	ALARM_OCCURRENCE_CREATED(549755813888l){ //2^39
		@Override
		public String getMessage(JSONObject json) {
			return null;
		}
	},
	;

    private long eventType;
    EventType(long eventType) {
        this.eventType = eventType;
    }
    public long getValue() {
        return eventType;
    }
    public static EventType valueOf(long eventTypeVal) {
    	return TYPE_MAP.get(eventTypeVal);
    }
    
    public boolean isPresent(long activity) {
    	if(activity > 0) {
    		return (activity & this.eventType) == this.eventType;
    	}
    	return false;
    }
   
    public abstract String getMessage(JSONObject json);
    
    private static final Map<Long, EventType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Long, EventType> initTypeMap() {
		Map<Long, EventType> typeMap = new HashMap<>();
		
		for(EventType type : values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	public Map<Long, EventType> getAllTypes() {
		return TYPE_MAP;
	}
}
