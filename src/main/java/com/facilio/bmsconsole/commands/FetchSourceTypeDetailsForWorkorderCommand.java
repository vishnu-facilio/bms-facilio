package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class FetchSourceTypeDetailsForWorkorderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (workOrder != null) {
			switch(workOrder.getSourceTypeEnum()) {
				case THRESHOLD_ALARM:
				case ALARM:
				case ANOMALY_ALARM: 
				case ML_ALARM :
					List<AlarmContext> alarms = AlarmAPI.getActiveAlarmsFromWoId(Collections.singletonList(workOrder.getId()));
					if (alarms != null && !alarms.isEmpty()) {
						workOrder.setAlarm(alarms.get(0));
					}
					break;
			}
		}
		return false;
	}

}
