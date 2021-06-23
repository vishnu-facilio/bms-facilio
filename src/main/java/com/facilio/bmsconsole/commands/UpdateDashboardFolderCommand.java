package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateDashboardFolderCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<DashboardFolderContext> folders = (List<DashboardFolderContext>) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDERS);
		if(folders != null) {
			DashboardUtil.updateDashboardFolder(folders);
		}
		return false;
	}

}