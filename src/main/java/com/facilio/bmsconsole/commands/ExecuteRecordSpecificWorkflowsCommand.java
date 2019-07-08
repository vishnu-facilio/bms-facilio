package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
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
import com.facilio.serializable.SerializableCommand;

public class ExecuteRecordSpecificWorkflowsCommand implements SerializableCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(ExecuteRecordSpecificWorkflowsCommand.class.getName());
	private RuleType[] ruleTypes;
	private int recordsPerThread = -1;
	private boolean propagateError = true;

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, List> recordMap = null;
		try {
			long startTime = System.currentTimeMillis();
			recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil
					.getChangeSetMap((FacilioContext) context);
			if (recordMap != null && !recordMap.isEmpty()) {
				if (recordsPerThread == -1) {
					fetchAndExecuteRules(recordMap, changeSetMap, (FacilioContext) context);
				} 
				LOGGER.debug("Time taken to Execute workflows for modules : " + recordMap.keySet() + " is "
						+ (System.currentTimeMillis() - startTime));
			}
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder("Error during execution of rule : ");
			builder.append(" for Record : " + recordMap).append(" this.propagateError " + this.propagateError)
					.append(" for this.ruleTypes " + Arrays.toString(ruleTypes));
			LOGGER.error(builder.toString(), e);
			CommonCommandUtil.emailException("RULE EXECUTION FAILED - " + AccountUtil.getCurrentOrg().getId(),
					builder.toString(), e);
			if (propagateError) {
				throw e;
			}
		}
		return false;
	}

	private void fetchAndExecuteRules(Map<String, List> recordMap,
			Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap, FacilioContext context) throws Exception {
		
		
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				LOGGER.log(Level.WARN,
						"Module Name / Records is null/ empty ==> " + moduleName + "==>" + entry.getValue());
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
			if (activities != null) {
				Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null
						: changeSetMap.get(moduleName);
				if (currentChangeSet != null && !currentChangeSet.isEmpty()) {
					activities.add(EventType.FIELD_CHANGE);
				}

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);

				Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
				FacilioField parentRule = fields.get("parentRuleId");
				Criteria parentCriteria = new Criteria();
				parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));

				Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
				List records = new LinkedList<>(entry.getValue());
				Iterator it = records.iterator();
				while (it.hasNext()) {
					{
						Object record = it.next();
						List<WorkflowRuleContext> workflowRules = (List<WorkflowRuleContext>) SingleRecordRuleAPI
								.getAllWorkFlowRule(((ModuleBaseWithCustomFields) record).getId(), module, activities);
						if (workflowRules != null && !workflowRules.isEmpty()) {
							List<UpdateChangeSet> changeSet = currentChangeSet == null ? null
									: currentChangeSet.get(((ModuleBaseWithCustomFields) record).getId());
							Map<String, Object> recordPlaceHolders = WorkflowRuleAPI
									.getRecordPlaceHolders(module.getName(), record, placeHolders);
							WorkflowRuleAPI.executeWorkflowsAndGetChildRuleCriteria(workflowRules, module, record,
									changeSet, it, recordPlaceHolders, context, propagateError, activities);
						}
					}
				}
			}
		}

	}
}
