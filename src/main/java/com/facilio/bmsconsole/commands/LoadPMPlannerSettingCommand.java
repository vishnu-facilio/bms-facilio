package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.PMPlannerAPI;
import com.facilio.constants.FacilioConstants;

public class LoadPMPlannerSettingCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		context.put(FacilioConstants.ContextNames.PM_PLANNER_SETTINGS, PMPlannerAPI.getPMPlannerSettings());
		
		return false;
	}
}
