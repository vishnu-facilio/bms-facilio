package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.exception.ReadingValidationException;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ExecuteValidationRule extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(CalculatePreFormulaCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		long processStarttime = System.currentTimeMillis();
		Boolean doValidation = (Boolean) context.get(FacilioConstants.ContextNames.DO_VALIDTION);
		if (doValidation == null || !doValidation) {
			return false;
		}
		
		Map recordMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (recordMap == null) {
			return false;
		}
			
		fetchAndExecuteRules(recordMap, (FacilioContext) context);
		LOGGER.debug("Time taken for execute validation rule is " + (System.currentTimeMillis() - processStarttime) + ", keyset : " + recordMap.keySet());
		return false;
	}
	
	private void fetchAndExecuteRules(Map<String, List> recordMap, FacilioContext context) throws Exception {
		for (Map.Entry<String, List> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (moduleName == null || moduleName.isEmpty() || entry.getValue() == null || entry.getValue().isEmpty()) {
				continue;
			}
			
			List<EventType> activities = Collections.singletonList(EventType.CREATE);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
			FacilioField parentRule = fields.get("parentRuleId");
			FacilioField onSuccess = fields.get("onSuccess");
			Criteria parentCriteria = new Criteria();
			parentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, CommonOperators.IS_EMPTY));
			parentCriteria.addAndCondition(CriteriaAPI.getCondition(onSuccess, CommonOperators.IS_EMPTY));
			List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, activities, parentCriteria, RuleType.VALIDATION_RULE);
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
						Criteria childCriteria = executeWorkflows(currentWorkflows, moduleName, record, it, recordPlaceHolders, context);
						if (childCriteria == null) {
							break;
						}
						currentWorkflows = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(module, activities, childCriteria, RuleType.VALIDATION_RULE);
					}
				}
			}
		}
	}
	
	private static Criteria executeWorkflows(List<WorkflowRuleContext> workflowRules, String moduleName, Object record, Iterator itr, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
		if(workflowRules != null && !workflowRules.isEmpty()) {
			Map<String, FacilioField> fields = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleFields());
			FacilioField parentRule = fields.get("parentRuleId");
			FacilioField onSuccess = fields.get("onSuccess");
			Criteria criteria = new Criteria();
			
			for(WorkflowRuleContext workflowRule : workflowRules) {
				Map<String, Object> rulePlaceHolders = workflowRule.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
				boolean miscFlag = false, criteriaFlag = false, workflowFlag = false;
				miscFlag = workflowRule.evaluateMisc(moduleName, record, rulePlaceHolders, context);
				if (miscFlag) {
					criteriaFlag = workflowRule.evaluateCriteria(moduleName, record, rulePlaceHolders, context);
					if (criteriaFlag) {
						workflowFlag = workflowRule.evaluateWorkflowExpression(moduleName, record, rulePlaceHolders, context);
					}
				}
				
				boolean result = criteriaFlag && workflowFlag && miscFlag;
				if(result) {
					String resultEvaluator = workflowRule.getWorkflow().getResultEvaluator();
					ReadingRuleContext readingRule = (ReadingRuleContext) workflowRule;
					String msg;
					switch (resultEvaluator) {
					case "(b!=-1&&a<b)||(c!=-1&&a>c)":
						msg = readingRule.getReadingField().getDisplayName() + " should be within safe limit.";
						throw new ReadingValidationException(readingRule.getReadingFieldId(), msg, resultEvaluator);
					case "(b!=-1)&&(a<b)":
						msg = readingRule.getReadingField().getDisplayName() + " should be incremental.";
						throw new ReadingValidationException(readingRule.getReadingFieldId(), msg, resultEvaluator);
					case "(b!=-1)&&(a>b)":
						msg = readingRule.getReadingField().getDisplayName() + " should be decremental.";
						throw new ReadingValidationException(readingRule.getReadingFieldId(), msg, resultEvaluator);
					default:
						throw new IllegalStateException();
					}
				}
				
				Criteria currentCriteria = new Criteria();
				currentCriteria.addAndCondition(CriteriaAPI.getCondition(parentRule, String.valueOf(workflowRule.getId()), NumberOperators.EQUALS));
				currentCriteria.addAndCondition(CriteriaAPI.getCondition(onSuccess, String.valueOf(result), BooleanOperators.IS));
				criteria.orCriteria(currentCriteria);
			}
			return criteria;
		}
		return null;
	}

}
