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
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


public class UpdateFormulaFieldResourceStatusCommand extends FacilioCommand {
	
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
			if(parentIds == null || parentIds.isEmpty()) {
				throw new Exception("No resource is associated for the given formula id " +formula.getId());
			}
				
			FormulaFieldResourceStatusAPI.deleteFormulaFieldResourceStatusByFormulaId(formula.getId());
			
			List<FormulaFieldResourceStatusContext> formulaFieldResourceStatusContextList = new ArrayList<FormulaFieldResourceStatusContext>();
			for(Long parentId:parentIds)
			{
				FormulaFieldResourceStatusContext formulaFieldResourceStatusContext = new FormulaFieldResourceStatusContext();	
				formulaFieldResourceStatusContext.setFormulaFieldId(formula.getId());
				formulaFieldResourceStatusContext.setFieldId(formula.getReadingFieldId());;
				formulaFieldResourceStatusContext.setResourceId(parentId);
				formulaFieldResourceStatusContext.setStatus(FormulaFieldResourceStatusContext.Status.RESOLVED.getIntVal());
				formulaFieldResourceStatusContext.setTriggerType(formula.getTriggerType());
				formulaFieldResourceStatusContext.setFrequency(formula.getFrequency());
				formulaFieldResourceStatusContext.setInterval(formula.getInterval());
				
				FormulaFieldResourceStatusAPI.addFormulaFieldResourceStatus(formulaFieldResourceStatusContext);
				formulaFieldResourceStatusContextList.add(formulaFieldResourceStatusContext);		
			}
			context.put(FacilioConstants.ContextNames.FORMULA_RESOURCE_STATUS_LIST, formulaFieldResourceStatusContextList);
		}
		
		return false;
	}

}
