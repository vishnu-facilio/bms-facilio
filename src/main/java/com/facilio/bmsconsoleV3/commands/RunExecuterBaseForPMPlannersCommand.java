package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;

public class RunExecuterBaseForPMPlannersCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<PMPlanner> planners = (List<PMPlanner>) context.get(FacilioConstants.PM_V2.PLANNER_LIST);
		
		if(CollectionUtils.isNotEmpty(planners)) {
			
			for(PMPlanner planner : planners) {
				PlannedMaintenanceAPI.runNightlyPlanner(planner.getId());
			}
		}
		return false;
	}

}
