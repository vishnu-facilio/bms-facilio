package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMPlannerSettingsContext;
import com.facilio.bmsconsole.util.PMPlannerAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePMPlannerSettingsCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		//context.put(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS, PMPlannerAPI.getPMPlannerSettings());
		PMPlannerAPI.updatePMPlannerSettings((PMPlannerSettingsContext) context.get(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS));

		return false;
	}
}
