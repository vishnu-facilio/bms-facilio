package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;

public enum FacilioEventFunctions implements FacilioWorkflowFunctionInterface {

	ADD(1,"add") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			
			String moduleName = (String) objects[0];
			
			boolean isHistorical = (boolean) objects[1];
			
			List<Map<String,Object>> events = new ArrayList<>();
			if(objects[2] instanceof Map) {
				events.add((Map<String,Object>) objects[2]);
			}
			else if (objects[2] instanceof List) {
				events = (List<Map<String,Object>>) objects[2];
			}
			else {
				return null;
			}
			
			FacilioContext context = new FacilioContext();
			
			switch(moduleName) {
			
				case FacilioConstants.ContextNames.READING_EVENT: {
					List<ReadingEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, ReadingEventContext.class);
				    context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
				}
				break;
				
				case FacilioConstants.ContextNames.BMS_EVENT: {
					List<BMSEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, BMSEventContext.class);
				    context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
				}
				break;
				default: {
					List<BaseEventContext> eventList = FieldUtil.getAsBeanListFromMapList(events, BaseEventContext.class);
				    context.put(EventConstants.EventContextNames.EVENT_LIST,eventList);
				}
				break;
			}
			
			Chain chain = TransactionChainFactory.getV2AddEventChain(isHistorical);
		    chain.execute(context);
			
			return true;
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
	private String namespace = "event";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.EVENT;
	
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
	FacilioEventFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioEventFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioEventFunctions getFacilioEventFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioEventFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioEventFunctions> initTypeMap() {
		Map<String, FacilioEventFunctions> typeMap = new HashMap<>();
		for(FacilioEventFunctions type : FacilioEventFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
