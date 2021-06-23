package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class ComputeScheduleForVisitorInviteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorInviteContext> inviteList = (List<VisitorInviteContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(inviteList)) {
			for(VisitorInviteContext invite : inviteList) {
				BusinessHoursContext visitTime = invite.getRecurringVisitTime();
				if(invite.isRecurring() && visitTime != null) {
					if(visitTime.getId() > 0) {
						BusinessHoursAPI.updateBusinessHours(visitTime);
						BusinessHoursAPI.deleteSingleBusinessHour(visitTime.getId());
						BusinessHoursAPI.addSingleBusinessHours(visitTime);
					}
					else {
						long id = BusinessHoursAPI.addBusinessHours(invite.getRecurringVisitTime());
						invite.setVisitingHoursId(id);
					}
				}
			}
		}
		return false;
	}

}
