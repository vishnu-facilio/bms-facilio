package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetInviteesListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VisitorInviteContext visitorEvent = (VisitorInviteContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(visitorEvent != null) {
			visitorEvent.setInvitees(VisitorManagementAPI.getEventInvitees(visitorEvent.getId()));
			if(visitorEvent.isRecurring()) {
				List<BusinessHoursContext> businessHours = BusinessHoursAPI.getBusinessHours(Collections.singletonList(visitorEvent.getVisitingHoursId()));
				if(CollectionUtils.isNotEmpty(businessHours)) {
					visitorEvent.setRecurringVisitTime(businessHours.get(0));
				}
			}
		}
		return false;
	}

}
