package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;

public class ClearAlarmOnWOCloseCommand implements Command, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		List<WorkOrderContext> workOrders = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(workOrders != null && !workOrders.isEmpty()) {
			TicketStatusContext closeStatus = TicketAPI.getStatus("Closed");
			List<Long> woIds = new ArrayList<>();
			for (WorkOrderContext workOrder : workOrders) {
				if (workOrder.getStatus() != null && workOrder.getStatus().getId() == closeStatus.getId()) {
					switch(workOrder.getSourceTypeEnum()) {
						case ALARM:
						case THRESHOLD_ALARM:
						case ANOMALY_ALARM:
							woIds.add(workOrder.getId());
							break;
						default:
							break;
					}
				}
			}
			
			if (!woIds.isEmpty()) {
				List<AlarmContext> alarms = AlarmAPI.getActiveAlarmsFromWoId(woIds);
				
				if (alarms != null && !alarms.isEmpty()) {
					for (AlarmContext alarm : alarms) {
						FacilioContext addEventContext = new FacilioContext();
						addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, AlarmAPI.constructClearEvent(alarm, "System auto cleared Alarm because associated Workorder was closed"));
						Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
						getAddEventChain.execute(addEventContext);
					}
				}
			}
		}
		return false;
	}

}
