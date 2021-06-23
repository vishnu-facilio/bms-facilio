package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateDashboardsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<DashboardContext> dashboards = (List<DashboardContext>) context.get(FacilioConstants.ContextNames.DASHBOARDS);
		
		if(dashboards != null) {
			for(DashboardContext dashboard :dashboards) {
				DashboardUtil.updateDashboard(dashboard);
				if(dashboard.getDashboardSharingContext() != null) {
					DashboardUtil.applyDashboardSharing(dashboard.getId(), dashboard.getDashboardSharingContext());
				}
			}
		}
		return false;
	}

}
