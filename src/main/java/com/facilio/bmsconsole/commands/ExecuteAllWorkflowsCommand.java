package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.google.common.collect.Lists;

public class ExecuteAllWorkflowsCommand implements Command 
{
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllWorkflowsCommand.class.getName());
	private RuleType[] ruleTypes;
	private int recordsPerThread = -1;
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
	
	@Override
	public boolean execute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}
		Map<String, List> recordMap = getRecordMap((FacilioContext) context);
		if(recordMap != null && !recordMap.isEmpty()) {
			if (recordsPerThread == -1) {
				fetchAndExecuteRules(recordMap, (FacilioContext) context);
			}
			else {
				new ParallalWorkflowExecution(AccountUtil.getCurrentAccount(), recordMap, (FacilioContext) context).invoke();
			}
			LOGGER.info("Time taken to Execute workflows for modules : "+recordMap.keySet()+" is "+(System.currentTimeMillis() - startTime));
		}
		return false;
	}
	
	private void fetchAndExecuteRules(Map<String, List> recordMap, FacilioContext context) throws Exception {
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+entry.getValue());
				continue;
			}
			
			ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			if(activityType != null) {
				List<ActivityType> activities = Collections.singletonList(activityType);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				
				Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
				FacilioField parentRule = fields.get("parentRuleId");
				FacilioField onSuccess = fields.get("onSuccess");
				Criteria parentCriteria = new Criteria();
				parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));
				parentCriteria.addAndCondition(CriteriaAPI.getCondition(onSuccess, CommonOperators.IS_EMPTY));
				List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, activities, parentCriteria, ruleTypes);
				if (workflowRules != null && !workflowRules.isEmpty()) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					
					List records = new LinkedList<>(entry.getValue());
					Iterator it = records.iterator();
					while (it.hasNext()) {
						Object record = it.next();
						Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), recordPlaceHolders);
						List<WorkflowRuleContext> currentWorkflows = workflowRules;
						while (currentWorkflows != null && !currentWorkflows.isEmpty()) {
							Criteria childCriteria = executeWorkflows(currentWorkflows, moduleName, record, it, recordPlaceHolders, (FacilioContext) context);
							if (childCriteria == null) {
								break;
							}
							currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, activities, childCriteria, ruleTypes);
						}
					}
				}
			}
		}
	}
	
	private Criteria executeWorkflows(List<WorkflowRuleContext> workflowRules, String moduleName, Object record, Iterator itr, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		if(workflowRules != null && !workflowRules.isEmpty()) {
			Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
			FacilioField parentRule = fields.get("parentRuleId");
			FacilioField onSuccess = fields.get("onSuccess");
			Criteria criteria = new Criteria();
			
			for(WorkflowRuleContext workflowRule : workflowRules) {
				try {
					Map<String, Object> rulePlaceHolders = workflowRule.constructPlaceHolders(moduleName, record, recordPlaceHolders, (FacilioContext) context);
					boolean miscFlag = false, criteriaFlag = false, workflowFlag = false;
					miscFlag = workflowRule.evaluateMisc(moduleName, record, rulePlaceHolders, (FacilioContext) context);
					if (miscFlag) {
						criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, rulePlaceHolders, (FacilioContext) context);
						if (criteriaFlag) {
							workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, rulePlaceHolders, (FacilioContext) context);
						}
					}
					
					boolean result = criteriaFlag && workflowFlag && miscFlag;
					if(result) {
						executeWorkflowActions(workflowRule, record, context, rulePlaceHolders);
						if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
							itr.remove();
							break;
						}
					}
					
					Criteria currentCriteria = new Criteria();
					currentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, String.valueOf(workflowRule.getId()), NumberOperators.EQUALS));
					currentCriteria.addAndCondition(CriteriaAPI.getCondition(onSuccess, String.valueOf(result), BooleanOperators.IS));
					criteria.orCriteria(currentCriteria);
				}
				catch (Exception e) {
					StringBuilder builder = new StringBuilder("Error during execution of rule : ");
					builder.append(workflowRule.getId());
					if (record instanceof ModuleBaseWithCustomFields) {
						builder.append(" for Record : ")
								.append(((ModuleBaseWithCustomFields)record).getId())
								.append(" of module : ")
								.append(moduleName);
					}
					LOGGER.log(Level.ERROR, builder.toString(), e);
					throw e;
				}
			}
			return criteria;
		}
		return null;
	}
	
	private void executeWorkflowActions(WorkflowRuleContext rule, Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = rule.getId();
		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), ruleId);
		if(actions != null) {
			for(ActionContext action : actions)
			{
				action.executeAction(placeHolders, context, rule, record);
			}
		}
	}
	
	private Map<String, List> getRecordMap(FacilioContext context) {
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		if (recordMap == null) {
			List records = (List) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if(records == null) {
				Object record = context.get(FacilioConstants.ContextNames.RECORD);
				if(record != null) {
					records = Collections.singletonList(record);
				}
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+records);
				return null;
			}
			
			recordMap = Collections.singletonMap(moduleName, records);
		}
		return recordMap;
	}
	
	private class ParallalWorkflowExecution extends RecursiveAction {

		private Account account;
		private Map<String, List> recordMap = null;
		private FacilioContext context = null;
		
		public ParallalWorkflowExecution(Account account, Map<String, List> recordMap, FacilioContext context) {
			// TODO Auto-generated constructor stub
			this.account = account;
			this.recordMap = recordMap;
			this.context = context;
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
							subTasks.add(new ParallalWorkflowExecution(account, Collections.singletonMap(name, entry.getValue()), context));
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
							fetchAndExecuteRules(recordMap, context);
						}
						else {
							List<List> recordLists = Lists.partition(records, recordsPerThread);
							List<ParallalWorkflowExecution> subTasks  = new ArrayList<>();
							for (List recordList : recordLists) {
								subTasks.add(new ParallalWorkflowExecution(account, Collections.singletonMap(moduleName, recordList), context));
							}
							ForkJoinTask.invokeAll(subTasks);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error occurred during execution of Workflows for record map "+recordMap, e);
			}
		}
		
	}
}
