package com.facilio.bmsconsole.commands;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
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

public class ExecuteAllWorkflowsCommand extends FacilioCommand implements PostTransactionCommand,Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ExecuteAllWorkflowsCommand.class.getName());
	protected RuleType[] ruleTypes;
	protected boolean propagateError = true;

	protected Map<String, List> recordMap;
	protected Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap;
	private Context context;
	protected Map<String,List<WorkflowRuleContext>> postRules;

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
	@WithSpan
	public boolean executeCommand(Context context) throws Exception {
		try {
			long startTime = System.currentTimeMillis();
			Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);

			if (historyReading != null && historyReading==true) {
				return false;
			}

			recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);

			if(recordMap != null && !recordMap.isEmpty()) {
				postRules = new HashMap<>();
				this.context = context;
				fetchAndExecuteRules((FacilioContext) context, false);
				if (AccountUtil.getCurrentOrg() != null && recordMap.values() != null) {
					LOGGER.debug("Time taken to Execute complete workflows for modules: " + recordMap.keySet().size() + " with moduleRecords : " + recordMap.values().size() + " is " + (System.currentTimeMillis() - startTime) + " : " + getPrintDebug());
				}
			}
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

	protected Criteria getCriteria(List<? extends ModuleBaseWithCustomFields> records) {
		Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
		FacilioField parentRule = fields.get("parentRuleId");

		Criteria parentCriteria = new Criteria();
		parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));
		return parentCriteria;
	}

	protected void fetchAndExecuteRules(FacilioContext context, boolean isPostExecute) throws Exception {
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
					if (workflowRules != null) {
						List<WorkflowRuleContext> postRulesList = new ArrayList<>();
						handlePostRules(workflowRules, postRulesList);
						if (!postRulesList.isEmpty()) {
							postRules.put(moduleName, postRulesList);
						}
					}
					if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
						LOGGER.debug("Time taken to fetch workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
					}

				}

				Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = new HashMap<String, List<WorkflowRuleContext>>();
				if (workflowRules != null && !workflowRules.isEmpty()) {

					List records = new LinkedList<>(entry.getValue());
					Iterator it = records.iterator();

					while (it.hasNext()) {
						Object record = it.next();
						List<UpdateChangeSet> changeSet = currentChangeSet == null ? null : currentChangeSet.get( ((ModuleBaseWithCustomFields)record).getId() );
						Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, getOrgPlaceHolders());
						WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record, changeSet, recordPlaceHolders, context,propagateError, workflowRuleCacheMap, false, activities);

					}
				}
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 343) {
					LOGGER.info("Time taken to execute workflow: " + (System.currentTimeMillis() - currentTime) + " : " + getPrintDebug());
				}
			}
		}
	}

	private Map<String, Object> orgPlaceHolders = null;
	protected Map<String, Object> getOrgPlaceHolders() throws Exception {
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

	private void handlePostRules(List<WorkflowRuleContext> workflowRules, List<WorkflowRuleContext> postRules) {
		for (Iterator<WorkflowRuleContext> iterator = workflowRules.iterator(); iterator.hasNext(); ) {
			 WorkflowRuleContext workflowRule = iterator.next();
			 if (workflowRule.getRuleTypeEnum().isPostExecute()) {
				 if (workflowRule.getRuleTypeEnum() == RuleType.MODULE_RULE) {
					 if (!workflowRule.isPreCommit()) {
						 postRules.add(workflowRule);
						 iterator.remove();
					 }
				 } else {
					 postRules.add(workflowRule);
					 iterator.remove();
				 }
			 }

		}
	}

	@Override
	public boolean postExecute() throws Exception {
		long time = System.currentTimeMillis();
		try {
			if (postRules != null && !postRules.isEmpty()) {
				fetchAndExecuteRules((FacilioContext) context, true);
			}
		}
		catch (Exception e) {
			LOGGER.error("OnPostExecuteRule:: Error occurred on post execution of workflow rule", e);
		}
		long executionTime = System.currentTimeMillis() - time;
		if (executionTime > 50) {
			LOGGER.debug("### time taken in postExecute: " + this.getClass().getSimpleName() + ": " + executionTime);
		}

		return false;
	}

}
