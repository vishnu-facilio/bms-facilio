package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class CreateWorkorderTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if(context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST) != null) {
			workorder.setAttachments(((List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST)));
		}
		
		TicketAPI.validateSiteSpecificData(workorder);
		
		Map<String, List<TaskContext>> tasks = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		WorkorderTemplate workorderTemplate = new WorkorderTemplate();
		workorderTemplate.setWorkorder(workorder);
		workorderTemplate.setTasks(tasks);
		
		String templateName = (String) context.get(FacilioConstants.ContextNames.TEMPLATE_NAME);
		if (templateName == null || !templateName.isEmpty()) {
			workorderTemplate.setName(workorder.getSubject());
		}
		else {
			workorderTemplate.setName(templateName);
		}
		Template.Type type = (Type) context.get(FacilioConstants.ContextNames.TEMPLATE_TYPE);
		
		long templateId = -1; 
		if (type == Template.Type.PM_WORKORDER) {
			templateId = TemplateAPI.addPMWorkOrderTemplate(workorderTemplate);		// all addition done here..
		}
		else {
			templateId = TemplateAPI.addWorkOrderTemplate(workorderTemplate);
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
