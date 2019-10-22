package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetVisitorAndInviteForQrScanCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long inviteId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		Long visitorId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_ID);
		
		if(inviteId > 0 && visitorId > 0) {
			VisitorLoggingContext visitorLoggingContext = VisitorManagementAPI.getVisitorLogging(visitorId, inviteId);
			if(visitorLoggingContext != null) {
				throw new IllegalArgumentException("This visitor has already checked in for the registered invite");
			}
			VisitorInviteContext visitorInvite = VisitorManagementAPI.getVisitorInvite(inviteId);
			VisitorContext visitor = VisitorManagementAPI.getVisitor(visitorId, null);
			context.put(FacilioConstants.ContextNames.VISITOR_INVITE, visitorInvite);
			context.put(FacilioConstants.ContextNames.VISITOR, visitor);
			
			
		}
		else {
			throw new IllegalArgumentException("Invalid Invite/visitor ID");
		}
		return false;
	}

}
