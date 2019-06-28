package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.agent.AgentKeys;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants.ContextNames;

public class GetControllerListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long agentId = (Long) context.get(AgentKeys.AGENT_ID);
		List<ControllerContext> controllers = ControllerAPI.getControllers(agentId != null && agentId > 0 ? agentId : -1);
		context.put(ContextNames.CONTROLLER_LIST, controllers);

		return false;
	}

}
