package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.constants.FacilioConstants;

public class LoadInviteIdAfterAdditionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorInviteContext visitorInviteRecords = (VisitorInviteContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(visitorInviteRecords != null) {
			context.put(FacilioConstants.ContextNames.VISITOR_INVITE_ID, visitorInviteRecords.getId());
		}
		return false;
	}

}
