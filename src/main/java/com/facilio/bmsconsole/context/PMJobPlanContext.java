package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tiles.request.collection.CollectionUtil;

import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;

public class PMJobPlanContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long id;
	long orgId;
	long pmId;
	long jobPlanId;
	JobPlanContext jobPlanContext;
	List<PmJobPlanTriggerContext> pmjobPlanTriggers;
	
	public List<TaskSectionTemplate> prepareAndGetJobPlanSections() throws Exception {
		
		List<TaskSectionTemplate> sections = jobPlanContext.getSectionTemplates();
		
		if(pmjobPlanTriggers != null && !pmjobPlanTriggers.isEmpty()) {
			for(TaskSectionTemplate section : sections) {
				for(PmJobPlanTriggerContext pmjobPlanTrigger : pmjobPlanTriggers) {
					
					PMTriggerContext trigger = PreventiveMaintenanceAPI.getPMTriggersByTriggerIds(Collections.singletonList(pmjobPlanTrigger.getTriggerId())).get(0);
					
					pmjobPlanTrigger.setPmTrigger(trigger);
					
					PMTaskSectionTemplateTriggers pmTaskSectionTemplateTrigger = pmjobPlanTrigger.getpmTaskSectionTemplateTrigger();
					pmTaskSectionTemplateTrigger.setSectionId(section.getId());
					section.addPmTaskSectionTemplateTriggers(pmTaskSectionTemplateTrigger);
					section.addPmTriggerContext(pmjobPlanTrigger.getPmTrigger());
				}
			}
		}
		
		return sections;
	}
	
	public List<PmJobPlanTriggerContext> getPmjobPlanTriggers() {
		return pmjobPlanTriggers;
	}
	public void setPmjobPlanTriggers(List<PmJobPlanTriggerContext> pmjobPlanTriggers) {
		this.pmjobPlanTriggers = pmjobPlanTriggers;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}
	public long getJobPlanId() {
		return jobPlanId;
	}
	public void setJobPlanId(long jobPlanId) {
		this.jobPlanId = jobPlanId;
	}
	public JobPlanContext getJobPlanContext() {
		return jobPlanContext;
	}
	public void setJobPlanContext(JobPlanContext jobPlanContext) {
		this.jobPlanContext = jobPlanContext;
	}
}
