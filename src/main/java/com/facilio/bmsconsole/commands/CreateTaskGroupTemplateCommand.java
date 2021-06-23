package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class CreateTaskGroupTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TaskSectionTemplate template = (TaskSectionTemplate) context.get(FacilioConstants.ContextNames.TEMPLATE);
		if (template != null) {
			context.put(FacilioConstants.ContextNames.RECORD_ID, TemplateAPI.addTaskGroupTemplate(template));
		}
		else {
			throw new IllegalArgumentException("Task Group Template cannot be null during addition");
		}
		
		return false;
	}

}
