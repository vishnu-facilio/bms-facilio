package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlActionCommandContext.Control_Action_Execute_Mode;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.time.DateTimeUtil;

public class FillDefaultValuesForControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		Control_Action_Execute_Mode controlActionExecuteMode = (Control_Action_Execute_Mode)context.get(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM);
		
		for(ControlActionCommandContext command :commands) {
			if(command.getControlActionMode() <= 0) {
				command.setControlActionMode(command.getRdm().getControlActionMode());
			}
			command.setExecutedBy(AccountUtil.getCurrentUser());
			command.setExecutedTime(DateTimeUtil.getCurrenTime());
			command.setExecutedMode(controlActionExecuteMode.getIntVal());
			command.setStatus(ControlActionCommandContext.Status.SUCCESS.getIntVal());
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, commands);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		return false;
	}

}
