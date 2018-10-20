package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PMTriggerResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
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
			
			PMTriggerResourceContext pmTriggerResource = PreventiveMaintenanceAPI.getPMTriggerResource(pmId, pmTrigger.getId(), pmJob.getResourceId());
			if(pmTriggerResource != null) {
				woTemplate.setAssignedToId(pmTriggerResource.getAssignedToId());
			}
			else {
				woTemplate.setAssignedToId(pmTrigger.getAssignedTo());
			}
			
			List<TaskSectionTemplate> sectiontemplates = woTemplate.getSectionTemplates();
			
			for(TaskSectionTemplate sectiontemplate :sectiontemplates) {
				
			}
			
		}
		return false;
	}

}
