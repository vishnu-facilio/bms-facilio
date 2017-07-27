package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;

public class ValidateTasksFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TaskContext taskContext = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(taskContext.getSubject() == null || taskContext.getSubject().isEmpty()) {
			throw new IllegalArgumentException("Subject is invalid");
		}
		else {
			taskContext.setSubject(taskContext.getSubject().trim());
		}
		
		if(taskContext.getDescription() == null || taskContext.getDescription().isEmpty()) {
			throw new IllegalArgumentException("Descripton is invalid");
		}
		else {
			taskContext.setDescription(taskContext.getDescription().trim());
		}
		
		return false;
	}

}
