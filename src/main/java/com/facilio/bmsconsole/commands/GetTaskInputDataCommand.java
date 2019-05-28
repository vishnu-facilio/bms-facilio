package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GetTaskInputDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetTaskInputDataCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		List<TaskContext> tasks = null;
		Map<TaskContext, TaskTemplate> taskvsTemplateMap = null;
		if(context.get(FacilioConstants.ContextNames.PM_TASK_SECTIONS) != null) {
			List<TaskSectionTemplate> sectionTemplates =  (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.PM_TASK_SECTIONS);
			tasks = new ArrayList<>();
			taskvsTemplateMap = new HashMap<>();
			for(TaskSectionTemplate sectionTemplate :sectionTemplates) {
				for(TaskTemplate taskTemplate:sectionTemplate.getTaskTemplates()) {
					TaskContext task = taskTemplate.getTask();
					tasks.add(task);
					taskvsTemplateMap.put(task, taskTemplate);
				}
			}
		} else {
			tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
			if(tasks == null) {
				TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
				if(task != null) {
					tasks = Collections.singletonList(task);
				}
			}
		}
		
		if(tasks != null && !tasks.isEmpty()) {
			TicketAPI.setTasksInputData(tasks);
		}
		
		PreventiveMaintenanceAPI.updateTaskTemplateFromTaskContext(taskvsTemplateMap);
		return false;
	}
}
