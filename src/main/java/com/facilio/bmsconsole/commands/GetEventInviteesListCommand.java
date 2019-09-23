package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorEventContext;
import com.facilio.bmsconsole.util.VisitorEventsAPI;
import com.facilio.constants.FacilioConstants;

public class GetEventInviteesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorEventContext visitorEvent = (VisitorEventContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(visitorEvent != null) {
			visitorEvent.setInvitees(VisitorEventsAPI.getEventInvitees(visitorEvent.getId()));
		}
		return false;
	}

}
