package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.PrerequisiteApproversTemplate;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;

public class CreateWorkorderTemplateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		PreventiveMaintenance preventivemaintenance = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST) != null) {
			workorder.setAttachments(((List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST)));
		}
		
		TicketAPI.validateSiteSpecificData(workorder);
		
		Map<String, List<TaskContext>> tasks = (Map<String, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);	// will be null during pm Operations
		WorkorderTemplate workorderTemplate = new WorkorderTemplate();
		workorderTemplate.setWorkorder(workorder);
		workorderTemplate.setTasks(tasks);
		List<PrerequisiteApproversTemplate> prerequisiteApproverTemplates = (List<PrerequisiteApproversTemplate>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES);
		workorderTemplate.setPrerequisiteApproverTemplates(prerequisiteApproverTemplates);
		
		if(context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES) != null) {
			List<TaskSectionTemplate> sectionTemplates =  (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES);
			workorderTemplate.setSectionTemplates(sectionTemplates);
			if (preventivemaintenance.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.SINGLE && workorderTemplate.getResourceIdVal() > 0) {
				workorderTemplate.setResource(ResourceAPI.getResource(workorderTemplate.getResourceIdVal()));
				if (workorderTemplate.getResource().getResourceTypeEnum() == ResourceType.ASSET) {
					if (workorderTemplate.getSectionTemplates() != null) {
						for (int i = 0; i < workorderTemplate.getSectionTemplates().size(); i++) {
							if (workorderTemplate.getSectionTemplates().get(i).getAssignmentType() > 0) {
								continue;
							}
							workorderTemplate.getSectionTemplates().get(i).setAssignmentType(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
							if (workorderTemplate.getSectionTemplates().get(i).getTaskTemplates() != null) {
								for (int j = 0; j < workorderTemplate.getSectionTemplates().get(i).getTaskTemplates().size(); j++) {
									if (workorderTemplate.getSectionTemplates().get(i).getTaskTemplates().get(j).getAssignmentType() > 0) {
										continue;
									}
									workorderTemplate.getSectionTemplates().get(i).getTaskTemplates().get(j).setAssignmentType(PreventiveMaintenance.PMAssignmentType.CURRENT_ASSET.getVal());
								}
							}
						}
					}
				}
			}
		}
		
		String templateName = (String) context.get(FacilioConstants.ContextNames.TEMPLATE_NAME);
		if (templateName == null || templateName.isEmpty()) {
			workorderTemplate.setName(workorder.getSubject());
		}
		else {
			workorderTemplate.setName(templateName);
		}
		Template.Type type = (Type) context.get(FacilioConstants.ContextNames.TEMPLATE_TYPE);
		
		long templateId = -1; 
		if (type == Template.Type.PM_WORKORDER) {
			
			templateId = TemplateAPI.addNewWorkOrderTemplate(workorderTemplate, Type.PM_WORKORDER, Type.PM_TASK, Type.PM_TASK_SECTION, Type.PM_PRE_REQUEST, Type.PM_PRE_REQUEST_SECTION);// all addition done here..
		}
		else {
			templateId = TemplateAPI.addWorkOrderTemplate(workorderTemplate);
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID, templateId);
		return false;
	}
	
}
