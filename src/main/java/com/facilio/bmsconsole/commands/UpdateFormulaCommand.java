package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class UpdateFormulaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext newFormula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		String formulaFieldUnit = (String) context.get(FacilioConstants.ContextNames.FORMULA_UNIT_STRING);
		int formulaUnit = (int) context.getOrDefault(FacilioConstants.ContextNames.FORMULA_UNIT, -1);
		Boolean calculateVmThroughFormula = (Boolean) context.get(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA);		

		validateUpdate(newFormula);
		FormulaFieldContext oldFormula = FormulaFieldAPI.getFormulaField(newFormula.getId());
		context.put(FacilioConstants.ContextNames.READING_FIELD_ID,oldFormula.getReadingFieldId());
		if (newFormula.getWorkflow() != null && hasCyclicDependency(newFormula.getWorkflow(), oldFormula.getReadingField(), oldFormula.getTriggerTypeEnum())) {
			throw new IllegalArgumentException("Formula has cyclic dependency and so cannot be updated");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		if ((newFormula.getName() != null && !newFormula.getName().isEmpty()) || (formulaFieldUnit != null || formulaUnit > 0)) {
			FacilioField field = new NumberField();
			module = new FacilioModule();
			
			if((newFormula.getName() != null && !newFormula.getName().isEmpty())) {
				if(calculateVmThroughFormula == null || !calculateVmThroughFormula) {
					field.setDisplayName(newFormula.getName());
					module.setDisplayName(newFormula.getName());
				}
			}
			if(formulaFieldUnit != null) {
				((NumberField)field).setUnit(formulaFieldUnit);
			}
			else if (formulaUnit > 0) {
				((NumberField) field).setUnitId(formulaUnit);
				((NumberField) field).setMetric((int)context.get(FacilioConstants.ContextNames.FORMULA_METRIC));
			}
			
			field.setId(oldFormula.getReadingFieldId());
			modBean.updateField(field);
		}
		
		if (newFormula.getInterval() != -1) {
			newFormula.setInterval(FormulaFieldAPI.getDataInterval(newFormula));
			if (module == null) {
				module = new FacilioModule();
			}
			module.setDataInterval(newFormula.getInterval());
		}
		
		if (module != null) {
			module.setModuleId(oldFormula.getReadingField().getModuleId());
			modBean.updateModule(module);
		}
		
		if (newFormula.getWorkflow() != null) {
			long workflowId = WorkflowUtil.addWorkflow(newFormula.getWorkflow());
			newFormula.setWorkflowId(workflowId);
		}
		
		if (newFormula.getTarget() != -1 && oldFormula.getTarget() != -1) {
			if (oldFormula.getTarget() != newFormula.getTarget()) {
				updateTarget(newFormula, oldFormula);
			}
			// newFormula.setTarget(-1);
		}
		
		newFormula.setSiteId(-1);
		newFormula.setResourceId(-1);
		newFormula.setAssetCategoryId(-1);
		newFormula.setSpaceCategoryId(-1);
		newFormula.setFrequency(null);
		newFormula.setInterval(-1);
		
		if(calculateVmThroughFormula == null || !calculateVmThroughFormula) {
			newFormula.setIncludedResources(null);
		}
		
		newFormula.setModifiedTime(System.currentTimeMillis());
		
		FacilioModule formulaModule = ModuleFactory.getFormulaFieldModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(formulaModule.getTableName())
														.fields(FieldFactory.getFormulaFieldFields())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formulaModule))
														.andCondition(CriteriaAPI.getIdCondition(newFormula.getId(), formulaModule));
		updateBuilder.update(FieldUtil.getAsProperties(newFormula));
		
		if (newFormula.getWorkflow() != null) {
			WorkflowUtil.deleteWorkflow(oldFormula.getWorkflowId());
			
			DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
			dateRange = getRange(oldFormula,FieldUtil.getAsJSON(dateRange));
			
			Boolean skipFormulaCalculation = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING);
			if(skipFormulaCalculation == null || skipFormulaCalculation.equals(Boolean.FALSE)) {
				if(dateRange != null)
				{
					FacilioChain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
					FacilioContext formulaFieldcontext = historicalCalculation.getContext();
					
					formulaFieldcontext.put(FacilioConstants.ContextNames.FORMULA_FIELD, newFormula.getId());
					formulaFieldcontext.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);	
					historicalCalculation.execute();					
				}
			}
		}
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
	private void validateUpdate(FormulaFieldContext formula) {
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null for updation.");
		}
		if (formula.getId() == -1) {
			throw new IllegalArgumentException("Invalid ID for formula updation.");
		}
		if (formula.getFormulaFieldTypeEnum() != null) {
			throw new IllegalArgumentException("Formula Field Type cannot be changed for Formula.");
		}
		if (formula.getTriggerTypeEnum() != null) {
			throw new IllegalArgumentException("Trigger type cannot be changed for Formula.");
		}
		if (formula.getResourceTypeEnum() != null) {
			throw new IllegalArgumentException("Resource Type cannot be changed for Formula.");
		}
		if (formula.getResourceId() != -1 || formula.getAssetCategoryId() != -1 || formula.getSpaceCategoryId() != -1) {
			throw new IllegalArgumentException("Resource(s) cannot be changed for Formula.");
		}
	}
	
	private boolean hasCyclicDependency(WorkflowContext workflow, FacilioField formulaField, TriggerType triggerType) throws Exception {
		List<WorkflowFieldContext> workflowFields = WorkflowUtil.getWorkflowField(workflow);
		ModuleType type = FormulaFieldAPI.getModuleTypeFromTrigger(triggerType);
		if (workflowFields == null || workflowFields.isEmpty()) {
			return false;
		}
		List<FacilioField> fields = workflowFields.stream().map(WorkflowFieldContext::getField).collect(Collectors.toList());
		return checkIfFieldIsPresent(fields, formulaField, type);
	}
	
	private boolean checkIfFieldIsPresent(List<FacilioField> fields, FacilioField formulaField, ModuleType type) throws Exception {
		if (fields == null || fields.isEmpty()) {
			return false;
		}
		if (fields.contains(formulaField)) {
			return true;
		}
		for (FacilioField field : fields) {
			if (field.getModule().getTypeEnum() == type) {
				List<FacilioField> dependentFields = fetchDependentFields(field);
				boolean isDependent = checkIfFieldIsPresent(dependentFields, formulaField, type);
				if (isDependent) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Map<Long, FormulaFieldContext> formulaCache = new HashMap<>();
	private List<FacilioField> fetchDependentFields(FacilioField field) throws Exception {
		FormulaFieldContext formula = formulaCache.get(field.getFieldId());
		if (formula == null) {
			formula = FormulaFieldAPI.getFormulaField(field);
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> fieldIds = formula.getWorkflow().getDependentFieldIds();
		if (fieldIds != null && !fieldIds.isEmpty()) {
			List<FacilioField> fields = new ArrayList<>();
			for (Long id : fieldIds) {
				fields.add(modBean.getField(id));
			}
			return fields;
		}
		return null;
	}
	
	private void updateTarget(FormulaFieldContext newFormula, FormulaFieldContext oldFormula) throws Exception {
		
		FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
		FacilioContext updateWorkflowContext = chain.getContext();
		ReadingRuleContext oldReadingRule = (ReadingRuleContext) oldFormula.getViolationRule();
		KPIUtil.setViolationCriteria(newFormula, oldFormula.getReadingField(),oldReadingRule);
		
		updateWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, oldReadingRule);
		chain.execute();
	}
	
	private DateRange getRange(FormulaFieldContext formula, JSONObject props) throws Exception {
		long currentTime = DateTimeUtil.getCurrenTime();
		DateRange range = null;
		switch (formula.getTriggerTypeEnum()) {
			case PRE_LIVE_READING:
				return null;
			case POST_LIVE_READING:
				if (props == null || props.isEmpty()) {
					return null;
				}
				range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
				if (range.getStartTime() == -1) {
					return null;
				}
				if (range.getEndTime() == -1) {
					range.setEndTime(currentTime);
				}
				break;
			case SCHEDULE:
				if (props == null || props.isEmpty()) {
					range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
					if((range.getStartTime() == -1) || (range.getEndTime() == -1))
					{
						range = null;
					}
				}
				else {
					range = FieldUtil.getAsBeanFromJson(props, DateRange.class);
					if (range.getStartTime() == -1) {
						range = new DateRange(FormulaFieldAPI.getStartTimeForHistoricalCalculation(formula), currentTime);
					}
					if (range.getEndTime() == -1) {
						range.setEndTime(currentTime);
					}
					if((range.getStartTime() == -1) || (range.getEndTime() == -1))
					{
						range = null;
					}
				}
				break;
		}
		return range;
	}
}
