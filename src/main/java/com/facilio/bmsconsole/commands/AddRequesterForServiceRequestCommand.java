package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddRequesterForServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ServiceRequestContext> request = (List<ServiceRequestContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (request != null && !request.isEmpty()) {
			for (ServiceRequestContext serviceRequestContext : request) {
				User requester = serviceRequestContext.getRequester();
				if (requester != null) {
					FacilioChain c = TransactionChainFactory.getAddRequesterChain();
					c.getContext().put(FacilioConstants.ContextNames.REQUESTER, requester);
					c.execute();
				}
			}
		}
		return false;
	}

}
