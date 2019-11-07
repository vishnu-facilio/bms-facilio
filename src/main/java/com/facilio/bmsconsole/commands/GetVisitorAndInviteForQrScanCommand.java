package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;

public class GetVisitorAndInviteForQrScanCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long inviteId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		Long visitorId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_ID);
		
		if(inviteId > 0 && visitorId > 0) {
			VisitorInviteContext invite = VisitorManagementAPI.getVisitorInvite(inviteId);
			if(invite != null) {
				if(invite.isRecurring()) {
					if(System.currentTimeMillis() > invite.getEndExecutionTime()) {
						throw new IllegalArgumentException("This invite is expired for this visitor");
					}
				}
			}
			VisitorLoggingContext visitorLoggingContext = VisitorManagementAPI.getVisitorLogging(visitorId, inviteId);
			if(visitorLoggingContext != null) {
				throw new IllegalArgumentException("This visitor has already checked in for the registered invite");
			}
			InviteVisitorRelContext inviteVisitor = VisitorManagementAPI.getInviteVisitorRel(visitorId, inviteId);
			context.put(FacilioConstants.ContextNames.VISITOR_INVITE_REL, inviteVisitor);
			
		}
		else {
			throw new IllegalArgumentException("Invalid Invite/visitor ID");
		}
		return false;
	}

}
