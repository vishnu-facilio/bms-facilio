package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateDashboardsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
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
