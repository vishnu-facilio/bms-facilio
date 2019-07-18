package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePrerequisiteStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if (FacilioConstants.ContextNames.PREREQUISITE_PHOTOS.equalsIgnoreCase(moduleName) && parentId > 0) {
			WorkOrderAPI.updatePreRequisiteStatus(parentId);
		}
		return false;
	}
}