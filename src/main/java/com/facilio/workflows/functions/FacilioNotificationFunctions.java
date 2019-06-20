package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.util.SMSUtil;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflowv2.util.WorkflowV2Util;

public enum FacilioNotificationFunctions implements FacilioWorkflowFunctionInterface {

	SEND_EMAIL(1,"sendMail") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];
			sendMailMap.put("sender", "noreply@facilio.com");
			Map<String,String> attachements = (Map<String,String>)sendMailMap.get("attachments");
			AwsUtil.sendEmail(WorkflowV2Util.getAsJSONObject(sendMailMap),attachements);
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
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			Map<String,Object> sendMailMap =  (Map<String, Object>) objects[0];
			
			SMSUtil.sendSMS(WorkflowV2Util.getAsJSONObject(sendMailMap));
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
	public Object execute(Object... objects) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}