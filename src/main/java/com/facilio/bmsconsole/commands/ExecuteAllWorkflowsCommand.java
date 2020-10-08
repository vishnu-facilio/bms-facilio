package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Context;
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

public class ExecuteAllWorkflowsCommand extends FacilioCommand implements PostTransactionCommand,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllWorkflowsCommand.class.getName());
	private RuleType[] ruleTypes;
	private boolean propagateError = true;
	
	private Map<String, List> recordMap;
	private Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap;
	private boolean isParallelRuleExecution;
	private Context context;
	private Map<String,List<WorkflowRuleContext>> postRules;
	
	public ExecuteAllWorkflowsCommand(RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.ruleTypes = ruleTypes;
	}
	
	public ExecuteAllWorkflowsCommand(boolean propogateError, RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.propagateError = propogateError;
		this.ruleTypes = ruleTypes;
	}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		try {
			long startTime = System.currentTimeMillis();
			Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
			if (historyReading != null && historyReading==true) {
				return false;
			}
			Boolean parallelRecordBasedRuleExecution = (Boolean) context.get(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION);
			parallelRecordBasedRuleExecution = parallelRecordBasedRuleExecution != null ? parallelRecordBasedRuleExecution : Boolean.FALSE;
			Boolean stopParallelRuleExecution = (Boolean) context.get(FacilioConstants.ContextNames.STOP_PARALLEL_RULE_EXECUTION);
			stopParallelRuleExecution = stopParallelRuleExecution != null ? stopParallelRuleExecution : Boolean.FALSE;

			if(FacilioProperties.isProduction() && !stopParallelRuleExecution) {
				Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
				if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
					String isParallelRuleExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_PARALLEL_RULE_EXECUTION);
					if (isParallelRuleExecutionProp != null && !isParallelRuleExecutionProp.isEmpty() && StringUtils.isNotEmpty(isParallelRuleExecutionProp)) {
						isParallelRuleExecution = Boolean.parseBoolean(isParallelRuleExecutionProp) && parallelRecordBasedRuleExecution;
					}
				}
			}
			
			recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
			if(recordMap != null && !recordMap.isEmpty()) {
				postRules = new HashMap<>();
				this.context = context;
				fetchAndExecuteRules(recordMap, changeSetMap, isParallelRuleExecution, (FacilioContext) context, false);
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
					LOGGER.info("Time taken to Execute workflows for modules : " + recordMap.keySet() + " is " + (System.currentTimeMillis() - startTime) + " : " + getPrintDebug());
				}
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

	private void fetchAndExecuteRules(Map<String, List> recordMap, Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap, boolean isParallelRuleExecution, FacilioContext context, boolean isPostExecute) throws Exception {
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
					LOGGER.info("Time taken to fetch workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
				}
				currentTime = System.currentTimeMillis();
				
//				if (AccountUtil.getCurrentOrg().getId() == 134l && "supplyairtemperature".equals(moduleName)) {
//					LOGGER.error("EMMAR RULE DEBUGGING");
//					LOGGER.error("Records : "+entry.getValue());
//					LOGGER.error("Matching Rules : "+workflowRules);
//					LOGGER.error("Rule Types : "+Arrays.toString(ruleTypes));
//				}

				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
					LOGGER.info(MessageFormat.format("Number of (rules, records, parallalExecution) : ({0}, {1}, {2})",
							(workflowRules == null ? 0 : workflowRules.size()),
							(entry.getValue() == null ? 0 : entry.getValue().size()),
							isParallelRuleExecution
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
					List<WorkflowRuleContext> workflowRulesExcludingReadingRule = new LinkedList<WorkflowRuleContext>();
					List<WorkflowRuleContext> readingRules = new LinkedList<WorkflowRuleContext>();
					WorkflowRuleAPI.groupWorkflowRulesByInstantJobs(ruleTypeVsWorkflowRules, workflowRulesExcludingReadingRule, readingRules);
					LOGGER.debug(MessageFormat.format("Time taken for splitting rules : {0}", System.currentTimeMillis() - processStartTime));
					
					List records = new LinkedList<>(entry.getValue());
					Iterator it = records.iterator();
					long totalInstantJobAddTime = 0;
					int jobs = 0;

					while (it.hasNext()) {
						Object record = it.next();		
						if(isParallelRuleExecution) {
							processStartTime = System.currentTimeMillis();
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
							WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record, changeSet, recordPlaceHolders, context,propagateError, workflowRuleCacheMap, isParallelRuleExecution, activities);
						}		
					}
					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
						LOGGER.info(MessageFormat.format("Total Time taken for adding {0} instant jobs : {1}", jobs, totalInstantJobAddTime));
					}
				}
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
					LOGGER.info("Time taken to execute workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
				}
			}
		}
	}

	private Map<String, Object> orgPlaceHolders = null;
	private Map<String, Object> getOrgPlaceHolders() throws Exception {
		if (orgPlaceHolders == null) {
			orgPlaceHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		}
		return orgPlaceHolders;
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

	@Override
	public boolean postExecute() throws Exception {
		try {
			if (postRules != null && !postRules.isEmpty()) {
				fetchAndExecuteRules(recordMap, changeSetMap, isParallelRuleExecution, (FacilioContext) context, true);
			}
		}
		catch (Exception e) {
			LOGGER.error("OnPostExecuteRule:: Error occurred on post execution of workflow rule", e);
		}
		
		return false;
	}
	
}
