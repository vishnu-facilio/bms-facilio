package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;

public class ValidateTasksCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TaskContext> tasks = null;
		Map<String, List<TaskContext>> taskMap = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		if(taskMap == null) {
			tasks = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
			if(tasks == null) {
				TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
				if(task != null) {
					tasks = Collections.singletonList(task);
				}
			}
		}
		else {
			tasks = new ArrayList<>();
			for(Map.Entry<String, List<TaskContext>> entry : taskMap.entrySet()) {
				tasks.addAll(entry.getValue());
			}
		}
		
		for(TaskContext task : tasks) {
			if (task.getInputTypeEnum() == null) {
				task.setInputType(TaskContext.InputType.NONE);
			}
			else {
				switch(task.getInputTypeEnum()) {
					case READING:
						if((task.getAsset() == null || task.getAsset().getId() == -1) && (task.getSpace() == null || task.getSpace().getId() == -1)) {
							throw new IllegalArgumentException("Space/Asset cannot be null when reading is enabled for task");
						}
						if(task.getReadingFieldId() == -1) {
							throw new IllegalArgumentException("Reading ID cannot be null when reading is enabled for task");
						}
						break;
					case CHECKBOX:
					case RADIO:
						if(task.getOptions() == null || task.getOptions().size() < 2) {
							throw new IllegalArgumentException("Minimum two options has to be added for CHECKBOX/ RADIO task");
						}
						break;
					default:
						break;
				}
			}
		}
		return false;
	}

}
