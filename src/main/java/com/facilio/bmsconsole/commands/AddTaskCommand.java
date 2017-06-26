package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.util.TaskAPI;

public class AddTaskCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TaskContext taskContext = (TaskContext) context;
		long taskId = TaskAPI.addTask(taskContext);
		taskContext.setTaskId(taskId);
		
		return false;
	}
}
