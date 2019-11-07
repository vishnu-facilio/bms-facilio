package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ComputeScheduleForVisitorInviteCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorInviteContext> inviteList = (List<VisitorInviteContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(inviteList)) {
			for(VisitorInviteContext invite : inviteList) {
				if(invite.isRecurring() && invite.getSchedule() != null) {
				
				}
			}
		}
		return false;
	}

}
