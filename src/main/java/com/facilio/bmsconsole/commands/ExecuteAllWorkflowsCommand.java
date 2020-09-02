package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Account;
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
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.google.common.collect.Lists;

public class ExecuteAllWorkflowsCommand extends FacilioCommand implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllWorkflowsCommand.class.getName());
	private RuleType[] ruleTypes;
	private int recordsPerThread = -1;
	private boolean propagateError = true;
	public ExecuteAllWorkflowsCommand(RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.ruleTypes = ruleTypes;
	}
	public ExecuteAllWorkflowsCommand(int recordsPerThread, RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		if (recordsPerThread > 0) {
			this.recordsPerThread = recordsPerThread;
		}
		this.ruleTypes = ruleTypes;
	}
	
	public ExecuteAllWorkflowsCommand(boolean propogateError, RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.propagateError = propogateError;
		this.ruleTypes = ruleTypes;
	}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List> recordMap = null;
		try {
			long startTime = System.currentTimeMillis();
			Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
			if (historyReading != null && historyReading==true) {
				return false;
			}
			Boolean isParallelRuleExecution = (Boolean) context.get(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION);
			isParallelRuleExecution = isParallelRuleExecution != null ? isParallelRuleExecution : Boolean.FALSE;
			Boolean stopParallelRuleExecution = (Boolean) context.get(FacilioConstants.ContextNames.STOP_PARALLEL_RULE_EXECUTION);
			stopParallelRuleExecution = stopParallelRuleExecution != null ? stopParallelRuleExecution : Boolean.FALSE;

			if(FacilioProperties.isProduction() && !stopParallelRuleExecution) {
				Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
				if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
					String isParallelRuleExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
					if (isParallelRuleExecutionProp != null && !isParallelRuleExecutionProp.isEmpty() && StringUtils.isNotEmpty(isParallelRuleExecutionProp)) {
						isParallelRuleExecution = Boolean.parseBoolean(isParallelRuleExecutionProp) || isParallelRuleExecution;
					}
				}
			}
			
			recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
			if(recordMap != null && !recordMap.isEmpty()) {
				if (recordsPerThread == -1) {
					fetchAndExecuteRules(recordMap, changeSetMap, isParallelRuleExecution, (FacilioContext) context);
				}
				else {
					new ParallalWorkflowExecution(AccountUtil.getCurrentAccount(), recordMap, changeSetMap, (FacilioContext) context).invoke();
				}
				LOGGER.debug("Time taken to Execute workflows for modules : "+recordMap.keySet()+" is "+(System.currentTimeMillis() - startTime) + " : " + getPrintDebug());
			}
//			if (AccountUtil.getCurrentOrg().getId() == 78l) {
//				LOGGER.info("ExecuteAllWorkflowsCommand Time taken to Execute workflows for modules : "+recordMap.keySet()+" is "+(System.currentTimeMillis() - startTime) + " : " + getPrintDebug());	
//			}
		}
		catch(Exception e) {
			StringBuilder builder = new StringBuilder("Error during execution of rules of type : ")
											.append(Arrays.toString(ruleTypes))
											.append(" for Record : "+recordMap)
											.append(" this.propagateError " +this.propagateError)
											.append(" for this.ruleTypes "+Arrays.toString(ruleTypes))
											;
			LOGGER.error(builder.toString(), e);
//			CommonCommandUtil.emailException("RULE EXECUTION FAILED - "+AccountUtil.getCurrentOrg().getId(),builder.toString(), e);
			if (propagateError) {
				throw e;
			}
		}
		return false;
	}
	public RuleType[] getRuleTypes() {
		return ruleTypes;
	}

	protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
		Criteria parentCriteria = getCriteria(records);

		// don't take any record if criteria
		if (parentCriteria == null) {
			return null;
		}
		List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, activities, parentCriteria, ruleTypes);
		return workflowRules;
	}

	protected Criteria getCriteria(List<? extends ModuleBaseWithCustomFields> value) {
		Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
		FacilioField parentRule = fields.get("parentRuleId");

		Criteria parentCriteria = new Criteria();
		parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));
		return parentCriteria;
	}

	private void fetchAndExecuteRules(Map<String, List> recordMap, Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap, boolean isParallelRuleExecution, FacilioContext context) throws Exception {
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
				continue;
			}
			List<EventType> activities = CommonCommandUtil.getEventTypes(context);
			if(activities != null) {
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
				if (currentChangeSet != null && !currentChangeSet.isEmpty()) {
					activities.add(EventType.FIELD_CHANGE);
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				
				long currentTime = System.currentTimeMillis();
				List<WorkflowRuleContext> workflowRules = getWorkflowRules(module, activities, entry.getValue(), context);
				LOGGER.debug("Time taken to fetch workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
				currentTime = System.currentTimeMillis();
				
				if (AccountUtil.getCurrentOrg().getId() == 186 && "statusupdation".equals(moduleName)) {
					LOGGER.info("Records : "+entry.getValue());
					LOGGER.info("Matching Rules : "+workflowRules);
					LOGGER.info("Rule Types : "+Arrays.toString(ruleTypes));
				}
				
				if (AccountUtil.getCurrentOrg().getId() == 134l && "supplyairtemperature".equals(moduleName)) {
					LOGGER.error("EMMAR RULE DEBUGGING");
					LOGGER.error("Records : "+entry.getValue());
					LOGGER.error("Matching Rules : "+workflowRules);
					LOGGER.error("Rule Types : "+Arrays.toString(ruleTypes));
				}
				if (AccountUtil.getCurrentOrg().getId() == 88l && "alarm".equals(moduleName)) {
					LOGGER.error("ALSEEF DEBUGGING");
					LOGGER.error("Records : "+entry.getValue());
					LOGGER.error("Matching Rules : "+workflowRules);
					LOGGER.error("Rule Types : "+Arrays.toString(ruleTypes));
				}

				LOGGER.debug("Number of entry: " + (workflowRules == null ? 0 : workflowRules.size()));

				Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
				if (workflowRules != null && !workflowRules.isEmpty()) {
					Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
					LinkedHashMap<RuleType, List<WorkflowRuleContext>> ruleTypeVsWorkflowRules = WorkflowRuleAPI.groupWorkflowRulesByRuletype(workflowRules);
					List<WorkflowRuleContext> workflowRulesExcludingReadingRule = new LinkedList<WorkflowRuleContext>();
					List<WorkflowRuleContext> readingRules = new LinkedList<WorkflowRuleContext>();
					WorkflowRuleAPI.groupWorkflowRulesByInstantJobs(ruleTypeVsWorkflowRules, workflowRulesExcludingReadingRule, readingRules);
					
					List records = new LinkedList<>(entry.getValue());
					Iterator it = records.iterator();
					while (it.hasNext()) {
						Object record = it.next();		
						if(isParallelRuleExecution) {							
							if(readingRules != null && !readingRules.isEmpty())  {
								FacilioTimer.scheduleInstantJob("rule","ParallelRecordBasedWorkflowRuleExecutionJob", ReadingRuleAPI.addAdditionalPropsForRecordBasedInstantJob(module, record, currentChangeSet, activities, context, WorkflowRuleAPI.getAllowedInstantJobWorkflowRuleTypes()));			
							}
							if(workflowRulesExcludingReadingRule != null && !workflowRulesExcludingReadingRule.isEmpty())  {
								FacilioTimer.scheduleInstantJob("rule","ParallelRecordBasedWorkflowRuleExecutionJob", WorkflowRuleAPI.addAdditionalPropsForNonReadingRuleRecordBasedInstantJob(module, record, currentChangeSet, activities, context, WorkflowRuleAPI.getNonReadingRuleWorkflowRuleTypes(ruleTypes)));
							}
						}
						else {
							List<UpdateChangeSet> changeSet = currentChangeSet == null ? null : currentChangeSet.get( ((ModuleBaseWithCustomFields)record).getId() );
							Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, placeHolders);
							WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record, changeSet, recordPlaceHolders, context,propagateError, workflowRuleCacheMap, isParallelRuleExecution, activities);
						}		
					}
				}
				LOGGER.debug("Time taken to execute workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
			}
		}
	}

	private String getPrintDebug() {
		StringJoiner sb = new StringJoiner(",");
		if (ruleTypes != null && ruleTypes.length > 0) {
			for (RuleType r : ruleTypes) {
				sb.add(r.name());
			}
		}
		return sb.toString();
	}

	private class ParallalWorkflowExecution extends RecursiveAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Account account;
		private Map<String, List> recordMap = null;
		private Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = null;
		private FacilioContext context = null;
		
		public ParallalWorkflowExecution(Account account, Map<String, List> recordMap, Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap, FacilioContext context) {
			// TODO Auto-generated constructor stub
			this.account = account;
			this.recordMap = recordMap;
			this.context = context;
			this.changeSetMap = changeSetMap;
		}
		
		@Override
		protected void compute() {
			// TODO Auto-generated method stub
			try {
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(account);
				
				if (recordMap.size() > 1) {
					List<ParallalWorkflowExecution> subTasks  = new ArrayList<>();
					for (Map.Entry<String, List> entry : recordMap.entrySet()) {
						String name = entry.getKey();
						if (name != null && !name.isEmpty()) {
							subTasks.add(new ParallalWorkflowExecution(account, Collections.singletonMap(name, entry.getValue()), changeSetMap, context));
						}
					}
					ForkJoinTask.invokeAll(subTasks);
				}
				else if (recordMap.size() == 1) {
					Map.Entry<String, List> entry = recordMap.entrySet().iterator().next();
					List records = entry.getValue();
					if (records != null && !records.isEmpty()) {
						String moduleName = entry.getKey();
						if (records.size() <= recordsPerThread) {
							fetchAndExecuteRules(recordMap, changeSetMap, false, context);
						}
						else {
							List<List> recordLists = Lists.partition(records, recordsPerThread);
							List<ParallalWorkflowExecution> subTasks  = new ArrayList<>();
							for (List recordList : recordLists) {
								subTasks.add(new ParallalWorkflowExecution(account, Collections.singletonMap(moduleName, recordList), changeSetMap, context));
							}
							ForkJoinTask.invokeAll(subTasks);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CommonCommandUtil.emailException("ParallalWorkflowForkJoinThread", "Error occurred during parallal execution of Workflows", e, recordMap.toString());
				LOGGER.error("Error occurred during parallal execution of Workflows for record map "+recordMap, e);
			}
		}
		
	}
}
