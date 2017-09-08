package com.facilio.beans;

import org.apache.commons.chain.Command;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	@Override
	public long addWorkOrder(WorkOrderContext workorder) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET, workorder.getTicket());
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		return workorder.getId();
	}

}
