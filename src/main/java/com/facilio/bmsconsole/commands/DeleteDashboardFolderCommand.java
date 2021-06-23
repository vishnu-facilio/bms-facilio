package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class DeleteDashboardFolderCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardFolderContext folders = (DashboardFolderContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDER);
		if(folders != null) {
			List<DashboardContext> dbs = DashboardUtil.getDashboardFromFolderId(folders.getId());
			if(dbs != null && !dbs.isEmpty()) {
				context.put(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE, "Folder contains dashboard");
			}
			else {
				DashboardUtil.deleteDashboardFolder(folders);
			}
		}
		return false;
	}

}
