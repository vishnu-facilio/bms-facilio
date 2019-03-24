package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.serializable.SerializableCommand;
import com.google.common.collect.Lists;

public class ExecuteAllWorkflowsCommand implements SerializableCommand
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
	public boolean execute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
		if(recordMap != null && !recordMap.isEmpty()) {
			if (recordsPerThread == -1) {
				fetchAndExecuteRules(recordMap, changeSetMap, (FacilioContext) context);
			}
			else {
				new ParallalWorkflowExecution(AccountUtil.getCurrentAccount(), recordMap, changeSetMap, (FacilioContext) context).invoke();
			}
			LOGGER.debug("Time taken to Execute workflows for modules : "+recordMap.keySet()+" is "+(System.currentTimeMillis() - startTime));
		}
		return false;
	}
	
	private void fetchAndExecuteRules(Map<String, List> recordMap, Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap, FacilioContext context) throws Exception {
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
				continue;
			}
			List<EventType> activities = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
			if (activities == null) {
				EventType activityType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
				if (activityType != null) {
					activities = new ArrayList<>();
					activities.add(activityType);
				}
			}
			if(activities != null) {
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
				if (currentChangeSet != null && !currentChangeSet.isEmpty()) {
					activities.add(EventType.FIELD_CHANGE);
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				
				Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
				FacilioField parentRule = fields.get("parentRuleId");
				Criteria parentCriteria = new Criteria();
				parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));
				List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, activities, parentCriteria, ruleTypes);
				
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
				
				if (workflowRules != null && !workflowRules.isEmpty()) {
					Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
					List records = new LinkedList<>(entry.getValue());
					Iterator it = records.iterator();
					while (it.hasNext()) {
						Object record = it.next();
						List<UpdateChangeSet> changeSet = currentChangeSet == null ? null : currentChangeSet.get( ((ModuleBaseWithCustomFields)record).getId() );
						Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, placeHolders);
						WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record, changeSet, it, recordPlaceHolders, context,propagateError, activities);
					}
				}
			}
		}
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
							fetchAndExecuteRules(recordMap, changeSetMap, context);
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
