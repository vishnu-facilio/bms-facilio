package com.facilio.beans;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	@Override
	public long addWorkOrder(WorkOrderContext workorder) throws Exception {
		// TODO Auto-generated method stub
		if(workorder != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.TICKET, workorder.getTicket());
			context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
			
			Command addWorkOrder = FacilioChainFactory.getAddWorkOrderWithTicketChain();
			addWorkOrder.execute(context);
			return workorder.getId();
		}
		return -1;
	}

	@Override
	public long addAlarm(AlarmContext alarm) throws Exception {
		// TODO Auto-generated method stub
		if(alarm != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.TICKET, alarm.getTicket());
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			
			Chain addAlarmChain = FacilioChainFactory.getAddAlarmChain();
			addAlarmChain.execute(context);
			return alarm.getId();
		}
		return -1;
	}

}
