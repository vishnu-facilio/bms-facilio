package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetInviteesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorInviteContext visitorEvent = (VisitorInviteContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(visitorEvent != null) {
			visitorEvent.setInvitees(VisitorManagementAPI.getEventInvitees(visitorEvent.getId()));
		}
		return false;
	}

}
