package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldResourceStatusContext;
import com.facilio.bmsconsole.util.FormulaFieldResourceStatusAPI;
import com.facilio.constants.FacilioConstants;

public class SetFormulaFieldResourceStatusCommand extends FacilioCommand {
	
	public boolean executeCommand(Context context) throws Exception {
		
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		
		List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
		if (parentIds == null) {
			Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			if (parentId != null) {
				parentIds = Collections.singletonList(parentId);
			}
		}
		
		if(parentIds == null || parentIds.isEmpty())
		{
			throw new Exception("No resource is associated for the given formula id " +formula.getId());
		}
		
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
		
		return false;
	}

}
