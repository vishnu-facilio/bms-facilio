package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;

public enum FacilioControlFunctions implements FacilioWorkflowFunctionInterface  {

	SET_VALUE(1,"setValue") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			ResourceContext resourceContext = new ResourceContext();
			resourceContext.setId((long) Double.parseDouble(objects[0].toString()));
			
			ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
			controlActionCommand.setResource(resourceContext);
			controlActionCommand.setFieldId((long) Double.parseDouble(objects[1].toString()));
			controlActionCommand.setValue(objects[2].toString());
			
			FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();
			
			FacilioContext context = executeControlActionCommandChain.getContext();
			
			context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, Collections.singletonList(controlActionCommand));
			context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.SCRIPT);
			
			executeControlActionCommandChain.execute();
			return null;
		}
		
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "control";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CONTROLS;
	
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
	FacilioControlFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioControlFunctions> getAllFunctions() {
		return CONTROL_FUNCTIONS;
	}
	public static FacilioControlFunctions getFacilioControlFunctions(String functionName) {
		return CONTROL_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioControlFunctions> CONTROL_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioControlFunctions> initTypeMap() {
		Map<String, FacilioControlFunctions> typeMap = new HashMap<>();
		for(FacilioControlFunctions type : FacilioControlFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}