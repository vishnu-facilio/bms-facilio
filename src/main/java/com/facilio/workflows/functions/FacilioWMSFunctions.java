package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

public enum FacilioWMSFunctions implements FacilioWorkflowFunctionInterface {

	SEND_MESSAGE_TO_USER(1,"sendMessageToUser") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> msgMap = (Map<String,Object>) objects[0];
			
			Message msg = FieldUtil.getAsBeanFromMap(msgMap, Message.class);
			
			msg.setTopic("__custom__/user/"+msg.getTopic());
			
			BaseHandler handler = Processor.getInstance().getHandler(msg.getTopic());

			if (handler.getDeliverTo() != TopicHandler.DELIVER_TO.USER) {
				throw new FunctionParamException("Topic handler will not send to user");
			}
			if ((msg.getTo() == null || msg.getTo() < 0)) {
				throw new FunctionParamException("To cannot be null here for delivery type USER");
			}

			SessionManager.getInstance().sendMessage(msg);
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SEND_MESSAGE_TO_ORG(2,"sendMessageToOrg") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> msgMap = (Map<String,Object>) objects[0];
			
			Message msg = FieldUtil.getAsBeanFromMap(msgMap, Message.class);
			
			msg.setTopic("__custom__/org/"+msg.getTopic());
			
			BaseHandler handler = Processor.getInstance().getHandler(msg.getTopic());
			if (handler.getDeliverTo() != TopicHandler.DELIVER_TO.ORG) {
				throw new FunctionParamException("Topic handler will not send to org");
			}
			// no need to check orgId.. we replace the orgId in sendMessage
//			if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.ORG && (msg.getOrgId() == null || msg.getOrgId() < 0)) {
//				throw new FunctionParamException("Orgid cannot be null for delivery type ORG");
//			}

			SessionManager.getInstance().sendMessage(msg);
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SEND_MESSAGE(3,"sendMessage") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> msgMap = (Map<String,Object>) objects[0];
			
			Message msg = FieldUtil.getAsBeanFromMap(msgMap, Message.class);
			
			BaseHandler handler = Processor.getInstance().getHandler(msg.getTopic());
			
			if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.ORG && (msg.getOrgId() == null || msg.getOrgId() < 0)) {
				throw new FunctionParamException("Orgid cannot be null for delivery type ORG");
			}
			
			if( handler.getDeliverTo() == TopicHandler.DELIVER_TO.USER && (msg.getTo() == null || msg.getTo() < 0)) {
				throw new FunctionParamException("To cannot be null here for delivery type USER");
			}

			SessionManager.getInstance().sendMessage(msg);
			
			return null;
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
	private String namespace = "wms";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.WMS;
	
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
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioWMSFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioWMSFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioWMSFunctions getFacilioWMSFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioWMSFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioWMSFunctions> initTypeMap() {
		Map<String, FacilioWMSFunctions> typeMap = new HashMap<>();
		for(FacilioWMSFunctions type : FacilioWMSFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
