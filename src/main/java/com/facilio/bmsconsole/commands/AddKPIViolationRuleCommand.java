package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddKPIViolationRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formula.getTarget() == -1) {
			return false;
		}
		
		FacilioField readingField = formula.getReadingField();
		if (readingField == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			readingField = modBean.getField(formula.getReadingFieldId());
		}
		
		Criteria criteria = KPIUtil.getViolationCriteria(formula, readingField);
		
		ActionContext action = new ActionContext();
		action.setActionType(ActionType.ADD_VIOLATION_ALARM);
		
		List<Map<String, Object>> fieldMatchers = new ArrayList<>();
		
		JSONObject templateJson = new JSONObject();
		Map<String, Object> fieldObj = new HashMap<>();
		fieldObj.put("field", "message");
		fieldObj.put("value", formula.getName() + " is more than " + formula.getTarget());
		fieldMatchers.add(fieldObj);
		
		fieldObj = new HashMap<>();
		fieldObj.put("field", "formulaId");
		fieldObj.put("value", formula.getId());
		
		fieldMatchers.add(fieldObj);
		templateJson.put("fieldMatcher", fieldMatchers);
		
		action.setTemplateJson(templateJson);
		
		ReadingRuleContext violationRule = new ReadingRuleContext();
		violationRule.setName("violationRule");
		violationRule.setThresholdType(ThresholdType.SIMPLE);
		violationRule.setReadingFieldId(readingField.getId());
		violationRule.setCriteria(criteria);
		violationRule.setOperatorId(NumberOperators.GREATER_THAN.getOperatorId());
		violationRule.setOnSuccess(true);
		violationRule.setClearAlarm(true);
		violationRule.setStatus(true);
		violationRule.setActions(Collections.singletonList(action));
		violationRule.setRuleType(RuleType.READING_RULE);
		violationRule.setActivityType(EventType.CREATE);
		violationRule.setModule(readingField.getModule());
		violationRule.setAssetCategoryId(formula.getAssetCategoryId());
		violationRule.setIncludedResources(formula.getIncludedResources());
		violationRule.setResourceId(formula.getResourceId());
		
		long ruleId = WorkflowRuleAPI.addWorkflowRule(violationRule);
		
		List<ActionContext> actions = ActionAPI.addActions(Collections.singletonList(action), violationRule);
		ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
		violationRule.setActions(actions);
		
		FormulaFieldContext newFormula = new FormulaFieldContext();
		newFormula.setViolationRuleId(ruleId);
		
		FacilioModule formulaModule = ModuleFactory.getFormulaFieldModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(formulaModule.getTableName())
														.fields(FieldFactory.getFormulaFieldFields())
														.andCondition(CriteriaAPI.getIdCondition(formula.getId(), formulaModule));
		updateBuilder.update(FieldUtil.getAsProperties(newFormula));
		
		return false;
	}

}
