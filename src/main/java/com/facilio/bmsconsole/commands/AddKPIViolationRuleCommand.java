package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
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
		if (formula.getTarget() == -1 && formula.getMinTarget() == -1 ) {
			return false;
		}
		
		FacilioField readingField = formula.getReadingField();
		if (readingField == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			readingField = modBean.getField(formula.getReadingFieldId());
		}
		
		ReadingRuleContext violationRule = new ReadingRuleContext();
		KPIUtil.setViolationCriteria(formula, readingField, violationRule);
		
		violationRule.setName("Violation Rule for " + formula.getName());
		violationRule.setThresholdType(ThresholdType.SIMPLE);
		violationRule.setReadingFieldId(readingField.getId());
		violationRule.setOnSuccess(true);
		violationRule.setClearAlarm(true);
		violationRule.setStatus(true);
		violationRule.setRuleType(RuleType.READING_VIOLATION_RULE);
		violationRule.setActivityType(EventType.CREATE);
		violationRule.setModule(readingField.getModule());
		violationRule.setAssetCategoryId(formula.getAssetCategoryId());
		violationRule.setIncludedResources(formula.getIncludedResources());
		violationRule.setResourceId(formula.getResourceId());
		
		long ruleId = WorkflowRuleAPI.addWorkflowRule(violationRule);
		
		List<ActionContext> actions = ActionAPI.addActions(violationRule.getActions(), violationRule);
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
