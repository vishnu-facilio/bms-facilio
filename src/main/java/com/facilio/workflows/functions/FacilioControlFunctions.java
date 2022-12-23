package com.facilio.workflows.functions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.*;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CONTROLS_FUNCTION)
public class FacilioControlFunctions {
	public Object setValue(Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> value = new HashMap<>();
		value.put(AgentConstants.RESOURCE_ID, objects[0]);
		value.put(AgentConstants.FIELD_ID, objects[1]);
		value.put(AgentConstants.VALUE, objects[2]);

		setValues(Collections.singletonList(value), null, true);

		return null;
	}

	public Object setValues(Map<String, Object> globalParam, Object... objects) throws Exception {
		setValues((List<Map<String, Object>>) objects[0], null, true);
		return null;
	}

	public Object auto(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String, Object> value = new HashMap<>();
		value.put(AgentConstants.RESOURCE_ID, objects[0]);
		value.put(AgentConstants.FIELD_ID, objects[1]);
		value.put(AgentConstants.ACTION_NAME, "auto");

		setValues(Collections.singletonList(value), "auto", false);

		return null;
	}

	public Object emergencyAuto(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String, Object> value = new HashMap<>();
		value.put(AgentConstants.RESOURCE_ID, objects[0]);
		value.put(AgentConstants.FIELD_ID, objects[1]);
		value.put(AgentConstants.ACTION_NAME, "emergencyAuto");

		setValues(Collections.singletonList(value), "emergencyAuto", false);

		return null;
	}

	public Object override(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String, Object> value = new HashMap<>();
		value.put(AgentConstants.RESOURCE_ID, objects[0]);
		value.put(AgentConstants.FIELD_ID, objects[1]);
		value.put(AgentConstants.VALUE, objects[2]);
		value.put(AgentConstants.ACTION_NAME, "override");

		setValues(Collections.singletonList(value), "override", true);

		return null;
	}

	public Object emergencyOverride(Map<String, Object> globalParam, Object... objects) throws Exception {

		Map<String, Object> value = new HashMap<>();
		value.put(AgentConstants.RESOURCE_ID, objects[0]);
		value.put(AgentConstants.FIELD_ID, objects[1]);
		value.put(AgentConstants.VALUE, objects[2]);
		value.put(AgentConstants.ACTION_NAME, "emergencyOverride");

		setValues(Collections.singletonList(value), "emergencyOverride", true);

		return null;
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