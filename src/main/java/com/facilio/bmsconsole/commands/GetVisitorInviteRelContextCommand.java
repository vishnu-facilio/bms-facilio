package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetVisitorInviteRelContextCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long inviteId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		Long visitorId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_ID);
		
		if(inviteId > 0 && visitorId > 0) {
			VisitorInviteContext invite = VisitorManagementAPI.getVisitorInvite(inviteId);
			if(invite != null) {
				InviteVisitorRelContext inviteVisitorRel = VisitorManagementAPI.getVisitorInviteRel(inviteId, visitorId);
				context.put(FacilioConstants.ContextNames.VISITOR_INVITE_REL, inviteVisitorRel);
			}
		}
		return false;
	}

}
