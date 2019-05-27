package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteDashboardFolderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		DashboardFolderContext folders = (DashboardFolderContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDER);
		if(folders != null) {
			DashboardUtil.deleteDashboardFolder(folders);
		}
		return false;
	}

}
