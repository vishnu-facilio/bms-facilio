package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateDashboardFolderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<DashboardFolderContext> folders = (List<DashboardFolderContext>) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDERS);
		if(folders != null) {
			DashboardUtil.updateDashboardFolder(folders);
		}
		return false;
	}

}