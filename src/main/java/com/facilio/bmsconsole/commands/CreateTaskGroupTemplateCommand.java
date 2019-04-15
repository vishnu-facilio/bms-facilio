package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class CreateTaskGroupTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
