package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteVisitorInviteRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorInviteContext invite = (VisitorInviteContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(invite != null) {
			VisitorManagementAPI.deleteVisitorInviteRel(invite.getId());
		}
		return false;
	}

}
