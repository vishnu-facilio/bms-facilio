package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlActionCommandContext.Control_Action_Execute_Mode;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class FillDefaultValuesForControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		Control_Action_Execute_Mode controlActionExecuteMode = (Control_Action_Execute_Mode)context.get(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM);
		boolean isFromScript = controlActionExecuteMode == Control_Action_Execute_Mode.SCRIPT;
		
		for(ControlActionCommandContext command :commands) {
			if(command.getControlActionMode() <= 0) {
				command.setControlActionMode(command.getRdm().getControlActionMode());
			}
			command.setExecutedBy(AccountUtil.getCurrentUser());
			command.setExecutedTime(DateTimeUtil.getCurrenTime());
			command.setExecutedMode(controlActionExecuteMode.getIntVal());
			command.setStatus(ControlActionCommandContext.Status.SUCCESS.getIntVal());
			
			if (isFromScript) {
				// Assuming control value will be given in display unit and needs to be send as input unit if any
				FacilioField field = command.getRdm().getField();
				Unit inputUnit = command.getRdm().getUnitEnum();
				if (field instanceof NumberField && inputUnit != null) {
					Unit fromUnit = null;
					if (((NumberField)field).getUnitEnum() != null) {
						fromUnit = ((NumberField)field).getUnitEnum();
					}
					else {
						// Metric should be available if input unit is there
						fromUnit = Unit.valueOf(((NumberField)field).getMetricEnum().getSiUnitId());
					}
					
					if (fromUnit != inputUnit) {
						Double valueInInputUnit = UnitsUtil.convert(command.getValue(), fromUnit, inputUnit);
						command.setValue(valueInInputUnit.toString());
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, commands);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		return false;
	}

}
