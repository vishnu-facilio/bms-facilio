package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class DeleteDashboardFolderCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardFolderContext folders = (DashboardFolderContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDER);
		if(folders != null) {
			DashboardUtil.deleteDashboardFolder(folders);
		}
		return false;
	}

}
