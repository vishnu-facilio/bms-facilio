package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.serializable.SerializableCommand;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ClearAlarmOnWOCloseCommand implements SerializableCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(ClearAlarmOnWOCloseCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
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
						case ML_ALARM:
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
						if (AccountUtil.getCurrentOrg().getId() == 135) {
							LOGGER.info("Auto Clearing alarm "+alarm.getId()+" since WO was closed");
						}
						
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
