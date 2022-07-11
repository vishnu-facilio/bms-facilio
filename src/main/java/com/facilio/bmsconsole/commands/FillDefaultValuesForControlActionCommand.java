package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlActionCommandContext.Control_Action_Execute_Mode;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class FillDefaultValuesForControlActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ControlActionCommandContext> commands = (List<ControlActionCommandContext>)context.get(ControlActionUtil.CONTROL_ACTION_COMMANDS);
		Control_Action_Execute_Mode controlActionExecuteMode = (Control_Action_Execute_Mode)context.get(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM);
		
		List<Long> rdmIds = commands.stream().map(c -> c.getRdm().getId()).collect(Collectors.toList());
		Map<Long, Map<Integer, String>> readingInputValuesMap = ReadingsAPI.getReadingInputValuesMapFromIndex(rdmIds);
		
		for(ControlActionCommandContext command :commands) {
			if(command.getControlActionMode() <= 0) {
				command.setControlActionMode(command.getRdm().getControlActionMode());
			}
			command.setExecutedBy(AccountUtil.getCurrentUser());
			command.setExecutedTime(DateTimeUtil.getCurrenTime());
			command.setExecutedMode(controlActionExecuteMode.getIntVal());
			command.setStatus(ControlActionCommandContext.Status.SENT.getIntVal());
			
			if (command.getValue() != null) {
				setConvertedValue(command, readingInputValuesMap);
			}
			
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, commands);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		return false;
	}
	
	private void setConvertedValue(ControlActionCommandContext command, Map<Long, Map<Integer, String>> readingInputValuesMap) {
		FacilioField field = command.getRdm().getField();
		Unit inputUnit = command.getRdm().getUnitEnum();
		String convertedValue = null;
		// Assuming control value will be given in display unit and needs to be send as input unit if any
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
				convertedValue = valueInInputUnit.toString();
			}
		}
		else if (field.getDataTypeEnum() == FieldType.BOOLEAN || field.getDataTypeEnum() == FieldType.ENUM) {
			// Converting from enum index to the enum value in agent
			convertedValue = convertInputValue(command.getRdm(), readingInputValuesMap, command.getValue());
		}
		
		if (convertedValue != null) {
			command.setConvertedValue(convertedValue);			
		}
	}
	
	private String convertInputValue(ReadingDataMeta readingDataMeta, Map<Long, Map<Integer, String>> rdmValueMap, String value) {
		if (rdmValueMap != null && rdmValueMap.get(readingDataMeta.getId()) != null && readingDataMeta.getInputTypeEnum() == ReadingInputType.CONTROLLER_MAPPED) {
			Map<Integer, String> valueMap = rdmValueMap.get(readingDataMeta.getId());
			int valIndex = FacilioUtil.parseInt(value);
			if (valueMap.containsKey(valIndex)) {
				return valueMap.get(valIndex);
			}
		}
		return null;
	}

}
