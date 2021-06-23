package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class RollUpWorkOrderFieldOnWorkPermitApprovalCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkPermitContext> workPermits = (List<WorkPermitContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<Long> wos = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(workPermits)) {
			WorkOrderContext newWo = new WorkOrderContext();
			newWo.setWorkPermitIssued(true);
			
			for(WorkPermitContext wp: workPermits) {
				if(wp.getTicket() != null && wp.getTicket().getId() > 0 && wp.getModuleState().getStatus().trim().equals("Active")) {
					wos.add(wp.getTicket().getId());
				}
			}
			
			if(CollectionUtils.isNotEmpty(wos)) {
				FacilioChain c = TransactionChainFactory.getUpdateWorkOrderChain();
				c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, wos);
				c.getContext().put(FacilioConstants.ContextNames.WORK_ORDER, newWo);
				c.execute();
			}
		
		}
		return false;
	}

}
