package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateFormulaCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext newFormula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		String formulaFieldUnit = (String) context.get(FacilioConstants.ContextNames.FORMULA_UNIT_STRING);
		validateUpdate(newFormula);
		FormulaFieldContext oldFormula = FormulaFieldAPI.getFormulaField(newFormula.getId());
		context.put(FacilioConstants.ContextNames.READING_FIELD_ID,oldFormula.getReadingFieldId());
		if (newFormula.getWorkflow() != null && hasCyclicDependency(newFormula.getWorkflow(), oldFormula.getReadingField(), oldFormula.getTriggerTypeEnum())) {
			throw new IllegalArgumentException("Formula as cyclic dependency and so cannot be updated");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		if ((newFormula.getName() != null && !newFormula.getName().isEmpty()) || (formulaFieldUnit != null)) {
			FacilioField field = new NumberField();
			
			if((newFormula.getName() != null && !newFormula.getName().isEmpty())) {
				field.setDisplayName(newFormula.getName());
			}
			if(formulaFieldUnit != null) {
				((NumberField)field).setUnit(formulaFieldUnit);
			}
			
			field.setId(oldFormula.getReadingFieldId());
			modBean.updateField(field);
			
			module = new FacilioModule();
			module.setDisplayName(newFormula.getName());
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
		
		newFormula.setResourceId(-1);
		newFormula.setAssetCategoryId(-1);
		newFormula.setSpaceCategoryId(-1);
		newFormula.setIncludedResources(null);
		newFormula.setFrequency(null);
		newFormula.setInterval(-1);
		
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
			FormulaFieldAPI.recalculateHistoricalData(newFormula.getId(), dateRange);
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
}
