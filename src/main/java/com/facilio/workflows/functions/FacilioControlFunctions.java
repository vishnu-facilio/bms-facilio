package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import com.facilio.agentv2.AgentConstants;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.List;
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
			
			Map<String, Object> value = new HashMap<>();
			value.put(AgentConstants.RESOURCE_ID, objects[0]);
			value.put(AgentConstants.FIELD_ID, objects[1]);
			value.put(AgentConstants.VALUE, objects[2]);

			setValues(Collections.singletonList(value), null, true);
			
			return null;
		}
		
	},
	SET_VALUES(1,"setValues") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			setValues((List<Map<String, Object>>) objects[0], null, true);
			return null;
		}
		
	},

	AUTO(2,"auto") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			Map<String, Object> value = new HashMap<>();
			value.put(AgentConstants.RESOURCE_ID, objects[0]);
			value.put(AgentConstants.FIELD_ID, objects[1]);
			value.put(AgentConstants.ACTION_NAME, AUTO.functionName);

			setValues(Collections.singletonList(value), AUTO.functionName, false);

			return null;
		}

	},

	EMERGENCY_AUTO(3,"emergencyAuto") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			Map<String, Object> value = new HashMap<>();
			value.put(AgentConstants.RESOURCE_ID, objects[0]);
			value.put(AgentConstants.FIELD_ID, objects[1]);
			value.put(AgentConstants.ACTION_NAME, EMERGENCY_AUTO.functionName);

			setValues(Collections.singletonList(value), EMERGENCY_AUTO.functionName, false);

			return null;
		}

	},

	OVERRIDE(4,"override") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			Map<String, Object> value = new HashMap<>();
			value.put(AgentConstants.RESOURCE_ID, objects[0]);
			value.put(AgentConstants.FIELD_ID, objects[1]);
			value.put(AgentConstants.VALUE, objects[2]);
			value.put(AgentConstants.ACTION_NAME, OVERRIDE.functionName);

			setValues(Collections.singletonList(value), OVERRIDE.functionName, true);

			return null;
		}

	},

	EMERGENCY_OVERRIDE(3,"emergencyOverride") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			Map<String, Object> value = new HashMap<>();
			value.put(AgentConstants.RESOURCE_ID, objects[0]);
			value.put(AgentConstants.FIELD_ID, objects[1]);
			value.put(AgentConstants.VALUE, objects[2]);
			value.put(AgentConstants.ACTION_NAME, EMERGENCY_OVERRIDE.functionName);

			setValues(Collections.singletonList(value), EMERGENCY_OVERRIDE.functionName, true);

			return null;
		}

	}
	;;
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

	private static void setValues(List<Map<String, Object>> values, String actionName, boolean setValue) throws Exception {
		List<ControlActionCommandContext> commands = new ArrayList<>();
		for (Map<String, Object> value: values) {
			ResourceContext resourceContext = new ResourceContext();
			resourceContext.setId((long) Double.parseDouble(value.get(AgentConstants.RESOURCE_ID).toString()));

			ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
			controlActionCommand.setResource(resourceContext);
			controlActionCommand.setFieldId((long) Double.parseDouble(value.get(AgentConstants.FIELD_ID).toString()));
			if (actionName != null) {
				controlActionCommand.setActionName(actionName);
			}
			if (setValue) {
				controlActionCommand.setValue(value.get(AgentConstants.VALUE) != null ? value.get(AgentConstants.VALUE).toString() : null );
			}

			commands.add(controlActionCommand);
		}


		FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandChain();

		FacilioContext context = executeControlActionCommandChain.getContext();

		context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, commands);
		context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.SCRIPT);

		executeControlActionCommandChain.execute();
	}
}