package com.facilio.mv.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class ScheduleMVFormulaCalculationJob extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		MVProjectContext mvProject = mvProjectWrapper.getMvProject();
		BmsJobUtil.deleteJobWithProps(mvProject.getId(), "mvFormulaFieldCalulationJob");
		BmsJobUtil.scheduleOneTimeJobWithProps(mvProject.getId(), "mvFormulaFieldCalulationJob", 10, "priority", null);
		
		return false;
	}
}