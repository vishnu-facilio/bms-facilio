package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class UpdateDashboardTabListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<DashboardTabContext> tabs = (List<DashboardTabContext>) context.get(FacilioConstants.ContextNames.DASHBOARD_TABS_LIST);
		if(tabs != null) {
			DashboardUtil.updateDashboardTabList(tabs);
		}
		return false;
	}

}
