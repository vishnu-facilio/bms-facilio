package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.cb.context.ChatBotConfirmContext;
import com.facilio.cb.context.ChatBotExecuteContext;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.util.FacilioUtil;

public enum FacilioChatBotFunctions implements FacilioWorkflowFunctionInterface {

	PARAM(1,"param") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			ChatBotParamContext params = new ChatBotParamContext();
			
			params.setParamName(objects[0].toString());
			
			if(objects[1] instanceof String) {
				params.setMessage(Collections.singletonList(objects[1].toString()));
			}
			else if (objects[1] instanceof List) {
				params.setMessage((List<String>)objects[1]);
			}
			if(objects.length > 2) {
				if(objects[2] instanceof List) {
					params.setOptions((List<JSONObject>) objects[2]);
				}
				else if (objects[2] instanceof Criteria) {
					params.setCriteria((Criteria)objects[2]);
					params.setModuleName((String)objects[3]);
					if(objects.length > 4) {
						params.setOrderByString((String)objects[4]);
					}
				}
				else if (objects[2] instanceof Map) {
					params.setDateSlot((JSONObject) objects[2]);
					if(objects.length > 3) {
						params.setPreviousValue(objects[3]);
					}
				}
				else {
					params.setPreviousValue(objects[2]);
				}
			}
			return params;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CONFIRM(2,"confirm") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			ChatBotConfirmContext params = new ChatBotConfirmContext();
			
			params.setParamMap((Map<String, Object>) objects[0]);
			if(objects.length > 1) {
				params.setInCardMessage((String)objects[1]);
				
				if(objects.length > 2) {
					if(objects[2] instanceof String) {
						params.setMessage(Collections.singletonList((String)objects[2]));
					}
					else if (objects[2] instanceof List) {
						params.setMessage((List<String>)objects[2]);
					}
				}
			}
			
			return params;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	EXECUTE(3,"execute") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
//			checkParam(objects);
			
			ChatBotExecuteContext params = new ChatBotExecuteContext();
			
			return params;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "chatBot";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CHAT_BOT;
	
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
	FacilioChatBotFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioChatBotFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioChatBotFunctions getFacilioChatBotFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioChatBotFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioChatBotFunctions> initTypeMap() {
		Map<String, FacilioChatBotFunctions> typeMap = new HashMap<>();
		for(FacilioChatBotFunctions type : FacilioChatBotFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}