package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateWoIdInAlarmCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (wo != null && wo.getId() != -1) {
			Long alarmId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, AlarmAPI.updateWoIdInAlarm(wo.getId(), alarmId));
		}
		
		return false;
	}

}
