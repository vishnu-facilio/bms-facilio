package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.modules.FieldUtil;
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
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		if(pm != null && pmJob.getResourceId() > 0) {
			
			Template template = TemplateAPI.getTemplate(pm.getTemplateId());
			WorkorderTemplate woTemplate = (WorkorderTemplate)template;
			
			woTemplate.setResourceId(pmJob.getResourceId());
			
			PMResourcePlannerContext pmResource = PreventiveMaintenanceAPI.getPMResourcePlanner(pm.getId(), pmJob.getResourceId());
			if(pmResource != null) {
				if(pmResource.getAssignedToId() != null) {
					woTemplate.setAssignedToId(pmResource.getAssignedToId());
				}
			}
			
			List<TaskSectionTemplate> sectiontemplates = woTemplate.getSectionTemplates();
			
			JSONObject content = template.getTemplate(null);
			
			WorkOrderContext woContext = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);
			
			woContext.setResource(ResourceAPI.getResource(pmJob.getResourceId()));
			
			Map<String, List<TaskContext>> taskMap = getTaskMap(sectiontemplates,woContext.getResource().getId());

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
			
			context.put(FacilioConstants.ContextNames.PM_TO_WO, Collections.singletonMap(pm.getId(), woContext));
			PreventiveMaintenanceAPI.updatePMJobStatus(pmJob.getId(), PMJobsStatus.COMPLETED);

			//incrementPMCount(pm);
		}
		return false;
	}

	public static void applySectionSettingsIfApplicable(TaskSectionTemplate sectiontemplate, TaskTemplate taskTemplate) {
		
		if(!taskTemplate.isAttachmentRequired()) {
			taskTemplate.setAttachmentRequired(sectiontemplate.isAttachmentRequired());
		}
		if(taskTemplate.getInputType() <= InputType.NONE.getVal() && sectiontemplate.getInputType() > InputType.NONE.getVal()) {
			taskTemplate.setInputType(sectiontemplate.getInputType());
		}
	}
	public static Map<String, List<TaskContext>> getTaskMap(List<TaskSectionTemplate> sectiontemplates,Long woResourceId) throws Exception {
		Map<String, List<TaskContext>> taskMap = new HashMap<>();
		for(TaskSectionTemplate sectiontemplate :sectiontemplates) {
			
			Template sectionTemplate = TemplateAPI.getTemplate(sectiontemplate.getId());
			sectiontemplate = (TaskSectionTemplate)sectionTemplate;
			
			 List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(sectiontemplate.getAssignmentType()), woResourceId, sectiontemplate.getSpaceCategoryId(), sectiontemplate.getAssetCategoryId(),sectiontemplate.getResourceId(),sectiontemplate.getPmIncludeExcludeResourceContexts());
			 
			 for(Long resourceId :resourceIds) {
				 if(resourceId == null || resourceId < 0) {
					 continue;
				 }
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
		return taskMap;
	}
}
