package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.email.EmailFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflowv2.util.WorkflowV2Util;

public enum FacilioNotificationFunctions implements FacilioWorkflowFunctionInterface {

	SEND_EMAIL(1,"sendMail") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];
			sendMailMap.put("sender", EmailFactory.getEmailClient().getNoReplyFromEmail());
			Map<String,String> attachements = (Map<String,String>)sendMailMap.get("attachments");
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.EMAIL_NOTIFICATION);
			context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(sendMailMap));
			context.put(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST, attachements);
			
			FacilioTimer.scheduleInstantJob("SendNotificationJob", context);
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SEND_SMS(2,"sendSms") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.SMS_NOTIFICATION);
			context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(sendMailMap));
			
			FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SEND_MOBILE_NOTIFICATION(3,"sendNotification") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Long userId =  (Long) objects[0];
			Map<String,Object> sendMailMap =  (Map<String, Object>) objects[1];
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.PUSH_NOTIFICATION);
			context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(sendMailMap));
			
			FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	MAKE_CALL(4,"makeCall") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Map<String,Object> callMap =  (Map<String, Object>) objects[0];
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.NOTIFICATION_TYPE, ActionType.MAKE_CALL);
			context.put(FacilioConstants.ContextNames.NOTIFICATION_OBJECT, WorkflowV2Util.getAsJSONObject(callMap));
			
			FacilioTimer.scheduleInstantJob("default","SendNotificationJob", context);
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	private Integer value;
	private String functionName;
	private String namespace = "notification";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.NOTIFICATION;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioNotificationFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioNotificationFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioNotificationFunctions getFacilioNotificationFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioNotificationFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioNotificationFunctions> initTypeMap() {
		Map<String, FacilioNotificationFunctions> typeMap = new HashMap<>();
		for(FacilioNotificationFunctions type : FacilioNotificationFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
	@Override
	public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}