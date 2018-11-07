package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class PreparePMForMultipleAsset implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PMTriggerContext pmTrigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER);
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		Long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		if(pmId != null && pmId != -1 && pmJob.getResourceId() > 0) {
			
			PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(pmId);
			
			ResourceContext resource = ResourceAPI.getResource(pmJob.getResourceId());
			
			Template template = TemplateAPI.getTemplate(pm.getTemplateId());
			WorkorderTemplate woTemplate = (WorkorderTemplate)template;
			
			woTemplate.setName(woTemplate.getName()+" - "+resource);
			
			woTemplate.setResourceId(pmJob.getResourceId());
			
			PMResourcePlannerContext pmResource = PreventiveMaintenanceAPI.getPMResourcePlanner(pmId, pmJob.getResourceId());
			if(pmResource != null) {
				if(pmResource.getAssignedToId() != null) {
					woTemplate.setAssignedToId(pmResource.getAssignedToId());
				}
			}
			
			List<TaskSectionTemplate> sectiontemplates = woTemplate.getSectionTemplates();
			
			JSONObject content = template.getTemplate(null);
			
			WorkOrderContext woContext = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);
			
			woContext.setResource(ResourceAPI.getResource(pmJob.getResourceId()));
			
			Map<String, List<TaskContext>> taskMap = new HashMap<>();
			for(TaskSectionTemplate sectiontemplate :sectiontemplates) {
				
				Template sectionTemplate = TemplateAPI.getTemplate(sectiontemplate.getId());
				sectiontemplate = (TaskSectionTemplate)sectionTemplate;
				
				 List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(sectiontemplate.getAssignmentType()), woContext.getResource().getId(), sectiontemplate.getSpaceCategoryId(), sectiontemplate.getAssetCategoryId(),sectiontemplate.getResourceId(),sectiontemplate.getPmIncludeExcludeResourceContexts());
				 
				 for(Long resourceId :resourceIds) {
					 ResourceContext sectionResource = ResourceAPI.getResource(resourceId);
					 String sectionName = sectionResource.getName() + " - " +sectiontemplate.getName();
					 
					 List<TaskTemplate> taskTemplates = sectiontemplate.getTaskTemplates();
					 
					 List<TaskContext> tasks = new ArrayList<TaskContext>();
					 for(TaskTemplate taskTemplate :taskTemplates) {
						 
						 List<Long> taskResourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(taskTemplate.getAssignmentType()), sectionResource.getId(), taskTemplate.getSpaceCategoryId(), taskTemplate.getAssetCategoryId(),taskTemplate.getResourceId(),taskTemplate.getPmIncludeExcludeResourceContexts());
						 
						 applySectionSettingsIfApplicable(sectiontemplate,taskTemplate);
						 for(Long taskResourceId :taskResourceIds) {
							 ResourceContext taskResource = ResourceAPI.getResource(taskResourceId);
							 TaskContext task = taskTemplate.getTask();
							 task.setResource(taskResource);
							 
							 tasks.add(task);
						 }
					 }
					 taskMap.put(sectionName, tasks);
				 }
			}
			woContext.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			woContext.setPm(pm);
			FacilioContext addWocontext = new FacilioContext();
			addWocontext.put(FacilioConstants.ContextNames.WORK_ORDER, woContext);
			addWocontext.put(FacilioConstants.ContextNames.REQUESTER, woContext.getRequester());
			addWocontext.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
			addWocontext.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
			addWocontext.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			addWocontext.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, woContext.getAttachments());
			
			//Temp fix. Have to be removed eventually
			PreventiveMaintenanceAPI.updateResourceDetails(woContext, taskMap);
			Chain addWOChain = TransactionChainFactory.getAddWorkOrderChain();
			addWOChain.execute(addWocontext);

			//incrementPMCount(pm);
		}
		return false;
	}

	private void applySectionSettingsIfApplicable(TaskSectionTemplate sectiontemplate, TaskTemplate taskTemplate) {
		
		if(!taskTemplate.isAttachmentRequired()) {
			taskTemplate.setAttachmentRequired(sectiontemplate.isAttachmentRequired());
		}
		if(taskTemplate.getInputType() <= InputType.NONE.getVal() && sectiontemplate.getInputType() > InputType.NONE.getVal()) {
			taskTemplate.setInputType(sectiontemplate.getInputType());
		}
	}

}
