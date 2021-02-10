package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class updateDashboardPublishCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<DashboardContext> dashboards = (List<DashboardContext>) context.get(FacilioConstants.ContextNames.DASHBOARDS);
		
		if(dashboards != null) {
			for(DashboardContext dashboard :dashboards) {
				if(dashboard.getDashboardPublishingContext() != null) {
					DashboardUtil.applyDashboardPublishing(dashboard.getId(), dashboard.getDashboardPublishingContext());
				}
			}
		}
		return false;
	}

}