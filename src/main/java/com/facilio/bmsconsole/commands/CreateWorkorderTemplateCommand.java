package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class CreateWorkorderTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, List<TaskContext>> tasks = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		WorkorderTemplate workorderTemplate = new WorkorderTemplate();
		workorderTemplate.setWorkorder(workorder);
		workorderTemplate.setTasks(tasks);
		Template.Type type = (Type) context.get(FacilioConstants.ContextNames.TEMPLATE_TYPE);
		
		long templateId = -1; 
		if (type == Template.Type.PM_WORKORDER) {
			templateId = TemplateAPI.addPMWorkOrderTemplate(workorderTemplate);
		}
		else {
			templateId = TemplateAPI.addWorkOrderTemplate(workorderTemplate);
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
