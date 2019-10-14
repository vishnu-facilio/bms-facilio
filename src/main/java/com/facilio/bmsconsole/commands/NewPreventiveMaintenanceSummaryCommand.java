package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class NewPreventiveMaintenanceSummaryCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
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

		Long nextExecution = PreventiveMaintenanceAPI.getNextExecutionTime(pmId);
		if (nextExecution != null) {
			pm.setNextExecutionTime(nextExecution);
		}

		pm.setTriggers(PreventiveMaintenanceAPI.getPMTriggers(pm));
		
		if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
			pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
			PreventiveMaintenanceAPI.populateResourcePlanner(pm);
		}
		
		WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
		List<TaskContext> listOfTasks = null;
		if ( template.getTaskTemplates() != null) {
			listOfTasks = template.getTaskTemplates().stream().map(taskTemplate -> taskTemplate.getTask()).collect(Collectors.toList());
			fillReadingFields(listOfTasks);
		}
		List<TaskContext> listOfPreRequests = null;
		if ( template.getPreRequestTemplates() != null) {
			listOfPreRequests = template.getPreRequestTemplates().stream().map(taskTemplate -> taskTemplate.getTask()).collect(Collectors.toList());
			fillReadingFields(listOfPreRequests);
		}
		WorkOrderContext workorder = template.getWorkorder();
		if(workorder.getAttachments() != null && !workorder.getAttachments().isEmpty()) {
			List<Long> fileIds = workorder.getAttachments().stream().map(file -> file.getFileId()).collect(Collectors.toList());
			FileStore fs = FacilioFactory.getFileStore();
			Map<Long, FileInfo> fileMap = fs.getFileInfoAsMap(fileIds, null);
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
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, template.getPreRequests());
		context.put(FacilioConstants.ContextNames.TASK_LIST, listOfTasks);
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_LIST, listOfPreRequests);
		context.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES, template.getPrerequisiteApproverTemplates());
		List<TaskSectionTemplate> sectionTemplate = template.getSectionTemplates();
		if (sectionTemplate != null) {
			sectionTemplate = sectionTemplate.stream().filter(sec -> !sec.getTypeEnum().equals(Type.PM_PRE_REQUEST_SECTION)).collect(Collectors.toList());
		}
		sectionTemplate = fillSectionTemplate(template,sectionTemplate);
		Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
		if (listOfTasks != null) {
			for (int i = 0; i < listOfTasks.size(); i++) {
				TaskContext task = listOfTasks.get(i);
				long readingFieldId = task.getReadingFieldId();
				List<ReadingRuleContext> readingRules = task.getReadingRules();
				if (readingFieldId > 0 && !fieldVsRules.containsKey(readingFieldId)) {
					fieldVsRules.put(readingFieldId, readingRules);
				}
			}
		}
		if (sectionTemplate != null) {
			for (int i = 0; i < sectionTemplate.size(); i++) {
				TaskSectionTemplate section = sectionTemplate.get(i);
				if (section.getTaskTemplates() != null) {
					for (int j = 0; j < section.getTaskTemplates().size(); j++) {
						TaskTemplate task = section.getTaskTemplates().get(j);
						task.setReadingRules(fieldVsRules.get(task.getReadingFieldId()));
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.TASK_SECTIONS, sectionTemplate);
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS, template.getPreRequestSectionTemplates());
		context.put(FacilioConstants.ContextNames.PM_TASK_SECTIONS, sectionTemplate);
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
		taskSectionTemplate.setId(-1);
		
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
