package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateBusinessHoursCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		BusinessHoursContext businessHours = (BusinessHoursContext) context
				.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		if (businessHours != null) {
			BusinessHoursAPI.updateBusinessHours(businessHours);
			context.put(FacilioConstants.ContextNames.ID, businessHours.getId());
		}
		return false;

	}
}
