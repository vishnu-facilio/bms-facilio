package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AddRequesterForServiceRequestCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3ServiceRequestContext> request = (List<V3ServiceRequestContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (request != null && !request.isEmpty()) {
			for (V3ServiceRequestContext serviceRequestContext : request) {
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
