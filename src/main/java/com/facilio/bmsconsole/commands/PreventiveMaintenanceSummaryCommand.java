package com.facilio.bmsconsole.commands;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class PreventiveMaintenanceSummaryCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Preventive_Maintenance")
				.andCustomWhere("Preventive_Maintenance.ORGID = ? AND Preventive_Maintenance.ID = ?", AccountUtil.getCurrentOrg().getOrgId(), pmId);

		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		Map<String, Object> pmProp = pmProps.get(0);
		
		PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(pmProp, PreventiveMaintenance.class);
		pm.setTriggers(PreventiveMaintenanceAPI.getPMTriggers(pm));
		
		if(pm.hasTriggers()) {
			for (PMTriggerContext trigger : pm.getTriggers()) {
				PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), Instant.now().getEpochSecond(), true);
				if (pmJob == null && pm.getTriggerTypeEnum() != TriggerType.NONE && trigger.getSchedule() != null && trigger.getSchedule().getFrequencyTypeEnum() != ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
					PMJobsContext lastPMJob = PreventiveMaintenanceAPI.getLastPMJob(trigger);
					if (lastPMJob != null) {
						pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, lastPMJob.getNextExecutionTime(),false);
					}
				}
				if(pmJob != null && (pm.getNextExecutionTime() == -1 || pmJob.getNextExecutionTime() <= pm.getNextExecutionTime())) {
					pm.setNextExecutionTime(pmJob.getNextExecutionTime()*1000);
				}
				if (trigger.getReadingRuleId() != -1) {
					ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(trigger.getReadingRuleId());
					trigger.setReadingFieldId(rule.getReadingFieldId());
					trigger.setReadingInterval(rule.getInterval());
					trigger.setStartReading(rule.getStartValue());
					trigger.setReadingRule(rule);
				}
			}
		}
		
		if(pm.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
			Map<Long, PMResourcePlannerContext> resourcePlanners = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
			
			List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(pm.getAssignmentType()),pm.getBaseSpaceId(),pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
			for(Long resourceId :resourceIds) {					// construct resource planner for default cases
				if(!resourcePlanners.containsKey(resourceId)) {
					PMResourcePlannerContext pmResourcePlannerContext = new PMResourcePlannerContext();
					pmResourcePlannerContext.setResourceId(resourceId);
					if(pmResourcePlannerContext.getResourceId() != null && pmResourcePlannerContext.getResourceId() > 0) {
						pmResourcePlannerContext.setResource(ResourceAPI.getResource(pmResourcePlannerContext.getResourceId()));
					}
					pmResourcePlannerContext.setPmId(pm.getId());
					pmResourcePlannerContext.setAssignedToId(-1l);
					pmResourcePlannerContext.setTriggerId(-1l);
					pmResourcePlannerContext.setPmResourcePlannerReminderContexts(Collections.emptyList());
					
					resourcePlanners.put(resourceId, pmResourcePlannerContext);
				}
			}
			
			if(resourcePlanners != null) {
				pm.setResourcePlanners(new ArrayList<>(resourcePlanners.values()));
			}
		}
		
		WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
		List<TaskContext> listOfTasks = null;
		if ( template.getTaskTemplates() != null) {
			listOfTasks = template.getTaskTemplates().stream().map(taskTemplate -> taskTemplate.getTask()).collect(Collectors.toList());
			fillReadingFields(listOfTasks);
		}
		
		WorkOrderContext workorder = template.getWorkorder();
		if(workorder.getAttachments() != null && !workorder.getAttachments().isEmpty()) {
			List<Long> fileIds = workorder.getAttachments().stream().map(file -> file.getFileId()).collect(Collectors.toList());
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			Map<Long, FileInfo> fileMap = fs.getFileInfoAsMap(fileIds);
			for(AttachmentContext attachment: workorder.getAttachments()) {
				FileInfo file = fileMap.get(attachment.getFileId());
				attachment.setFileName(file.getFileName());
				attachment.setFileSize(file.getFileSize());
			}
		}
		Map<String, List<TaskContext>> taskMap = template.getTasks();
		
		if (taskMap != null && !taskMap.isEmpty()) {
			List<Long> fieldIds = taskMap.entrySet().stream().map(Entry::getValue).flatMap(List::stream).map(TaskContext::getReadingFieldId).collect(Collectors.toList());
			
			StringJoiner j = new StringJoiner(",");
			fieldIds.stream().forEach(f -> j.add(String.valueOf(f)));
			
			Criteria criteria = new Criteria();
	        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", j.toString(), NumberOperators.EQUALS));
	        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
	        List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
	        
	        if (readingRules != null && !readingRules.isEmpty()) {
	        	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
	            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
	            Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
	            
	        	for (ReadingRuleContext r:  readingRules) {
	        		if (r.getReadingFieldId() != -1) { 
	        			List<ReadingRuleContext> rules = fieldVsRules.get(r.getReadingFieldId());
	        			if (rules == null) {
	        				rules = new ArrayList<>();
	        				fieldVsRules.put(r.getReadingFieldId(), rules);
	        			}
	        			rules.add(r);
	        		}
	        		long workflowId = r.getWorkflowId();
	        		if (workflowId != -1) {
	        			r.setWorkflow(workflowMap.get(workflowId));
	        		}
	        	}
	        	taskMap.entrySet().stream().map(Entry::getValue).flatMap(List::stream).forEach(t -> t.setReadingRules(fieldVsRules.get(t.getReadingFieldId())));
	        }
		}
        
		TicketAPI.loadTicketLookups(Arrays.asList(workorder));
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
		context.put(FacilioConstants.ContextNames.TASK_LIST, listOfTasks);
		List<TaskSectionTemplate> sectionTemplate = template.getSectionTemplates();
		sectionTemplate = fillSectionTemplate(template,sectionTemplate);
		context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sectionTemplate);
		PreventiveMaintenanceAPI.updateResourceDetails(workorder, taskMap);
		if (listOfTasks != null) {
			CommonCommandUtil.loadTaskLookups(listOfTasks);
		}
		
		List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(Collections.singletonList(pm.getId()));
		context.put(FacilioConstants.ContextNames.PM_REMINDERS, reminders);
		
		return false;
	}
	
	private List<TaskSectionTemplate> fillSectionTemplate(WorkorderTemplate template,List<TaskSectionTemplate> sectionTemplate) {
		
		TaskSectionTemplate taskSectionTemplate = new TaskSectionTemplate();
		taskSectionTemplate.setName("default");
		taskSectionTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
		taskSectionTemplate.setParentWOTemplateId(template.getId());
		List<TaskTemplate> defaultTask = new ArrayList<>();
		
		if(template.getTaskTemplates() != null) {
			for(TaskTemplate taskTemplates : template.getTaskTemplates()) {
				if(taskTemplates.getSectionId() < 0) {
					defaultTask.add(taskTemplates);
				}
			}
		}
		
		if(!defaultTask.isEmpty()) {
			taskSectionTemplate.setTaskTemplates(defaultTask);
			sectionTemplate = sectionTemplate != null ? sectionTemplate : new ArrayList<>();
			sectionTemplate.add(taskSectionTemplate);
		}
		return sectionTemplate;
	}

	private void fillReadingFields(List<TaskContext> listOfTasks) throws Exception {
		if (listOfTasks != null && !listOfTasks.isEmpty()) {
			List<Long> fieldIds = listOfTasks.stream().map(TaskContext::getReadingFieldId).collect(Collectors.toList());
			StringJoiner j = new StringJoiner(",");
			fieldIds.stream().forEach(f -> j.add(String.valueOf(f)));
			
			Criteria criteria = new Criteria();
		    criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", j.toString(), NumberOperators.EQUALS));
		    criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
		    List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
		    
		    if (readingRules != null && !readingRules.isEmpty()) {
		    	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
		        Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
		        Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
		        
		    	for (ReadingRuleContext r:  readingRules) {
		    		if (r.getReadingFieldId() != -1) { 
		    			List<ReadingRuleContext> rules = fieldVsRules.get(r.getReadingFieldId());
		    			if (rules == null) {
		    				rules = new ArrayList<>();
		    				fieldVsRules.put(r.getReadingFieldId(), rules);
		    			}
		    			rules.add(r);
		    		}
		    		long workflowId = r.getWorkflowId();
		    		if (workflowId != -1) {
		    			r.setWorkflow(workflowMap.get(workflowId));
		    		}
		    	}
		    	listOfTasks.stream().forEach(t -> t.setReadingRules(fieldVsRules.get(t.getReadingFieldId())));
		    }
		}
	}
}
