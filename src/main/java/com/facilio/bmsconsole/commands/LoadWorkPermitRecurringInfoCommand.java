package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.WorkPermitContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;

public class LoadWorkPermitRecurringInfoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkPermitContext permit = (WorkPermitContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(permit != null) {
			if(permit.isRecurring()) {
				List<BusinessHoursContext> businessHours = BusinessHoursAPI.getBusinessHours(Collections.singletonList(permit.getRecurringInfoId()));
				if(CollectionUtils.isNotEmpty(businessHours)) {
					permit.setRecurringInfo(businessHours.get(0));
				}
			}
		}
		return false;
	}

}
