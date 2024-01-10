package com.facilio.common.reading.add;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
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
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Log4j
public class ExecuteValidationRule extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {

		Boolean doValidation = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.DO_VALIDTION, Boolean.FALSE);
		if (!doValidation) {
			return false;
		}

		long processStartTime = System.currentTimeMillis();

		Map<String, List<ReadingContext>> recordMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		if (recordMap == null) {
			return false;
		}

		fetchAndExecuteRules(recordMap, (FacilioContext) context);
		LOGGER.debug("Time taken for execute validation rule is " + (System.currentTimeMillis() - processStartTime) + ", keyset : " + recordMap.keySet());
		return false;
	}

	private void fetchAndExecuteRules(Map<String, List<ReadingContext>> recordMap, FacilioContext context) throws Exception {
		for (Map.Entry<String, List<ReadingContext>> entry : recordMap.entrySet()) {
			String moduleName = entry.getKey();
			if (StringUtils.isEmpty(moduleName) || CollectionUtils.isEmpty(entry.getValue())) {
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
			if (CollectionUtils.isNotEmpty(workflowRules)) {
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);

				List<ReadingContext> records = new LinkedList<>(entry.getValue());
				Iterator<ReadingContext> it = records.iterator();
				while (it.hasNext()) {
					ReadingContext record = it.next();
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
