package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;

public class PMSettingsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (wo == null || wo.getPm() == null) {
			return false;
		}
		
		if (wo.getPm().isPreventOnNoTask()) {
			Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			if (taskMap == null || taskMap.isEmpty()) {
				LOGGER.log(Level.SEVERE, "Skipping because of no task " + "PM ID " + wo.getPm().getId());
				return true;
			}
		} else {
			Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
			if (taskMap == null || taskMap.isEmpty()) {
				LOGGER.log(Level.SEVERE, "No Task Generated In this workorder", "PM ID " + wo.getPm().getId());
//				CommonCommandUtil.emailAlert("No Task Generated In this workorder", "PM ID " + wo.getPm().getId());
			}
		}
		
		return false;
	}

}
