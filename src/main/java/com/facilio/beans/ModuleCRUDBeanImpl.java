package com.facilio.beans;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
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

	@Override
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception {
		// TODO Auto-generated method stub
		if(workOrderRequest != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.TICKET, workOrderRequest.getTicket());
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrderRequest.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);
			
			Command addWorkOrderRequest = FacilioChainFactory.getAddWorkOrderRequestChain();
			addWorkOrderRequest.execute(context);
			return workOrderRequest.getId();
		}
		return -1;
	}
	
	@Override
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		if(alarm != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
			
			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
			updateAlarm.execute(context);
			
			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return -1;
	}

}
