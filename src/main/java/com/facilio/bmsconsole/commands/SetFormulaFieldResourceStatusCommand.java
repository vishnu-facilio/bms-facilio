package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class SetFormulaFieldResourceStatusCommand extends FacilioCommand {
	
	private boolean isUpdate = false;
	public SetFormulaFieldResourceStatusCommand(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public SetFormulaFieldResourceStatusCommand() {
	}
	
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean calculateVmThroughFormula = (Boolean) context.get(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA);
		calculateVmThroughFormula = calculateVmThroughFormula != null ? calculateVmThroughFormula : Boolean.FALSE;

		boolean isParallelFormulaExecution = false;
		Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_PARALLEL_FORMULA_EXECUTION);
		if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
			String isParallelFormulaExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_PARALLEL_FORMULA_EXECUTION);
			if (isParallelFormulaExecutionProp != null && !isParallelFormulaExecutionProp.isEmpty() && StringUtils.isNotEmpty(isParallelFormulaExecutionProp)) {
				isParallelFormulaExecution = Boolean.parseBoolean(isParallelFormulaExecutionProp);
			}
		}
		isParallelFormulaExecution = isParallelFormulaExecution || calculateVmThroughFormula;

		if(isParallelFormulaExecution) 
		{
			FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
			List<Long> parentIds = formula.getIncludedResources();
			
			if(parentIds == null || parentIds.isEmpty()){
				parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
				if (parentIds == null) {
					Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
					if (parentId != null) {
						parentIds = Collections.singletonList(parentId);
					}
				}
			}
			if(parentIds == null || parentIds.isEmpty()){
				if(!isUpdate) {
					throw new IllegalArgumentException("No resource is associated for the given formula id " +formula.getId());
				}
				else {
					return false;
				}
			}
			
			//delete resourceIds present
			if(isUpdate) {
				FormulaFieldResourceStatusAPI.deleteFormulaFieldResourceStatusByFormulaId(formula.getId());
				setDefaultFormulaProps(formula);
			}
			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = new ArrayList<FormulaFieldResourceStatusContext>();
			for(Long parentId:parentIds)
			{
				FormulaFieldResourceStatusContext formulaFieldResourceStatusContext = new FormulaFieldResourceStatusContext();	
				formulaFieldResourceStatusContext.setFormulaFieldId(formula.getId());
				formulaFieldResourceStatusContext.setResourceId(parentId);
				formulaFieldResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal());

				formulaFieldResourceStatusContext.setFieldId(formula.getReadingFieldId());;
				formulaFieldResourceStatusContext.setTriggerType(formula.getTriggerType());
				formulaFieldResourceStatusContext.setFrequency(formula.getFrequency());
				formulaFieldResourceStatusContext.setInterval(formula.getInterval());
					
				FormulaFieldResourceStatusAPI.addFormulaFieldResourceStatus(formulaFieldResourceStatusContext, FieldFactory.getFormulaFieldResourceStatusModuleFields());
				formulaFieldResourceStatusContextList.add(formulaFieldResourceStatusContext);		
			}
			context.put(FacilioConstants.ContextNames.FORMULA_RESOURCE_STATUS_LIST, formulaFieldResourceStatusContextList);	
		}	
		
		return false;
	}
	
	private void setDefaultFormulaProps(FormulaFieldContext formula) throws Exception {	
		FormulaFieldContext oldFormula = FormulaFieldAPI.getFormulaField(formula.getId());
		formula.setReadingFieldId(oldFormula.getReadingFieldId());;
		formula.setTriggerType(oldFormula.getTriggerType());
		formula.setFrequency(oldFormula.getFrequency());
		formula.setInterval(oldFormula.getInterval());		
	}

}
