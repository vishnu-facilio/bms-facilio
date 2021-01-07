package com.facilio.bmsconsole.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ParallelRuleExecutionProp;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.tasker.FacilioTimer;

public class ExecuteReadingRuleCommand extends ExecuteAllWorkflowsCommand {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ExecuteReadingRuleCommand.class.getName());
	
	public ExecuteReadingRuleCommand(boolean propagateError, RuleType... ruleTypes) {			
        super(propagateError,ruleTypes);
    }
	
    @Override
	protected void fetchAndExecuteRules(FacilioContext context, boolean isPostExecute) throws Exception 
    {	
		ParallelRuleExecutionProp isParallelRuleExecutionEnum = null; 
		Boolean parallelRuleExecutionProp = (Boolean) context.get(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION);
		parallelRuleExecutionProp = parallelRuleExecutionProp != null ? parallelRuleExecutionProp : Boolean.TRUE;
		if(FacilioProperties.isProduction() && parallelRuleExecutionProp) { 
			Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
			if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
				String isParallelRuleExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
				if (isParallelRuleExecutionProp != null && !isParallelRuleExecutionProp.isEmpty() && StringUtils.isNotEmpty(isParallelRuleExecutionProp) && Integer.valueOf(isParallelRuleExecutionProp) != null) {
					isParallelRuleExecutionEnum = ParallelRuleExecutionProp.valueOf(Integer.valueOf(isParallelRuleExecutionProp));
				}
			}
		}	
		
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
				continue;
			}
			List<EventType> activities = CommonCommandUtil.getEventTypes(context);
			if(activities != null) {
				
				boolean isReadingRuleWorkflowExecution = false;
	        	Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_READING_RULE_WORKFLOW_EXECUTION);
				if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
					String isReadingRuleWorkflowExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_READING_RULE_WORKFLOW_EXECUTION);
					if (isReadingRuleWorkflowExecutionProp != null && !isReadingRuleWorkflowExecutionProp.isEmpty() && StringUtils.isNotEmpty(isReadingRuleWorkflowExecutionProp) && Boolean.valueOf(isReadingRuleWorkflowExecutionProp) != null && Boolean.parseBoolean(isReadingRuleWorkflowExecutionProp)){
						isReadingRuleWorkflowExecution = true;
					}	
	        	}			
				context.put(FacilioConstants.ContextNames.IS_READING_RULE_WORKFLOW_EXECUTION, isReadingRuleWorkflowExecution);
				
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
				if (currentChangeSet != null && !currentChangeSet.isEmpty()) {
					activities.add(EventType.FIELD_CHANGE);
				}
				
				if(isParallelRuleExecutionEnum !=null && isParallelRuleExecutionEnum == ParallelRuleExecutionProp.MODULE_BASED) {	
					System.out.println("moduleName Jobcreated"+moduleName);
					FacilioTimer.scheduleInstantJob("rule","ParallelModuleBasedWorkflowRuleExecutionJob", WorkflowRuleAPI.addAdditionalPropsForModuleBasedInstantJob(moduleName, new LinkedList<>(entry.getValue()), currentChangeSet, activities, context, ruleTypes));				
				}
				else 
				{	
					System.out.println("moduleName JobNotcreated"+moduleName);
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(moduleName);
					
					long currentTime = System.currentTimeMillis();
					List<WorkflowRuleContext> workflowRules;
					if (isPostExecute ) {
						if (postRules.containsKey(moduleName)) {
							workflowRules = postRules.get(moduleName);
						}
						else {
							continue;
						}
					}
					else {
						workflowRules = getWorkflowRules(module, activities, entry.getValue(), context);
					}

					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
						LOGGER.info("Time taken to fetch workflow: " + (System.currentTimeMillis() - currentTime));
					}
					currentTime = System.currentTimeMillis();
					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
						LOGGER.info(MessageFormat.format("Number of (rules, records, parallalExecution) : ({0}, {1}, {2})",
								(workflowRules == null ? 0 : workflowRules.size()),
								(entry.getValue() == null ? 0 : entry.getValue().size()),
								isParallelRuleExecutionEnum
								)
						);
					}

					Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
					if (workflowRules != null && !workflowRules.isEmpty()) {
						long processStartTime = System.currentTimeMillis();
						
						List<WorkflowRuleContext> postRulesList = null;
						if (!isPostExecute) {
							postRulesList = postRules.get(moduleName);
							if (postRulesList == null) {
								postRulesList = new ArrayList<>();
							}
						}
						
						LinkedHashMap<RuleType, List<WorkflowRuleContext>> ruleTypeVsWorkflowRules = WorkflowRuleAPI.groupWorkflowRulesByRuletype(workflowRules, postRulesList);
						if (postRulesList != null && !postRulesList.isEmpty()) {
							postRules.put(moduleName, postRulesList);
						}						
						LOGGER.debug(MessageFormat.format("Time taken for splitting rules : {0}", System.currentTimeMillis() - processStartTime));
						
						List records = new LinkedList<>(entry.getValue());
						Iterator it = records.iterator();
						long totalInstantJobAddTime = 0;
						int jobs = 0;

						while (it.hasNext()) {
							Object record = it.next();		
							if(isParallelRuleExecutionEnum!=null && isParallelRuleExecutionEnum == ParallelRuleExecutionProp.RECORD_BASED) {
								processStartTime = System.currentTimeMillis();	
								List<WorkflowRuleContext> workflowRulesExcludingReadingRule = new LinkedList<WorkflowRuleContext>();
								List<WorkflowRuleContext> readingRules = new LinkedList<WorkflowRuleContext>();
								WorkflowRuleAPI.groupWorkflowRulesByInstantJobs(ruleTypeVsWorkflowRules, workflowRulesExcludingReadingRule, readingRules);
								if(readingRules != null && !readingRules.isEmpty())  {
									FacilioTimer.scheduleInstantJob("rule","ParallelRecordBasedWorkflowRuleExecutionJob", ReadingRuleAPI.addAdditionalPropsForRecordBasedInstantJob(module, record, currentChangeSet, activities, context, WorkflowRuleAPI.getAllowedInstantJobWorkflowRuleTypes()));
									jobs++;
									long timeTaken = System.currentTimeMillis() - processStartTime;
									totalInstantJobAddTime += timeTaken;
								}
								if(workflowRulesExcludingReadingRule != null && !workflowRulesExcludingReadingRule.isEmpty())  {
									FacilioTimer.scheduleInstantJob("rule","ParallelRecordBasedWorkflowRuleExecutionJob", WorkflowRuleAPI.addAdditionalPropsForNonReadingRuleRecordBasedInstantJob(module, record, currentChangeSet, activities, context, WorkflowRuleAPI.getNonReadingRuleWorkflowRuleTypes(ruleTypes)));
									jobs++;
									long timeTaken = System.currentTimeMillis() - processStartTime;
									totalInstantJobAddTime += timeTaken;
								}			
							}
							else {
								List<UpdateChangeSet> changeSet = currentChangeSet == null ? null : currentChangeSet.get( ((ModuleBaseWithCustomFields)record).getId() );
								Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, getOrgPlaceHolders());
								WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record, changeSet, recordPlaceHolders, context,propagateError, workflowRuleCacheMap, false, activities);
							}		
						}
						if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
							LOGGER.info(MessageFormat.format("Total Time taken for adding {0} instant jobs : {1}", jobs, totalInstantJobAddTime));
						}
					}
					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
						LOGGER.info("Time taken to execute workflow: " + (System.currentTimeMillis() - currentTime));
					}
					
				}	
			}
		}
	}
		
}
