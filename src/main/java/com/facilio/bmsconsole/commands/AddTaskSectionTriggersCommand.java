package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

public class AddTaskSectionTriggersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if (pm == null || pm.getTriggerMap() == null) {
			return false;
		}
		
		List<TaskSectionTemplate> sectionTemplates =  (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES);
		if (sectionTemplates == null) {
			return false;
		}

		PreventiveMaintenanceAPI.addTaskSectionTrigger(pm, sectionTemplates);

		return false;
	}

}
