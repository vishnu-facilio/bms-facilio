package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class GetTaskInputDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetTaskInputDataCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		if(tasks == null) {
			TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			if(task != null) {
				tasks = Collections.singletonList(task);
			}
		}
		if(tasks != null && !tasks.isEmpty()) {
			TicketAPI.setTasksInputData(tasks);
		}
		return false;
	}
}
