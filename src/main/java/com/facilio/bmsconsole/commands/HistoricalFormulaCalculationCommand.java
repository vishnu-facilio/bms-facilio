package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.db.criteria.DateRange;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class HistoricalFormulaCalculationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long formulaId = (long) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		long resourceId = (long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		
		if (formulaId == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1 || resourceId == -1) {
			throw new IllegalArgumentException("In Sufficient paramaters for Historical formula calculation");
		}
		
		FormulaFieldContext formula = FormulaFieldAPI.getFormulaField(formulaId);
		if (formula == null) {
			throw new IllegalArgumentException("Invalid formula ID for historical calculation");
		}
		
		if (!formula.getMatchedResourcesIds().contains(resourceId)) {
			throw new IllegalArgumentException("The given formula : "+formulaId+" is not defined for resource : "+resourceId);
		}
		
		Boolean historicalAlarm = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_ALARM);
		if (historicalAlarm == null) {
			historicalAlarm = false;
		}
		Boolean skipOptimisedWorkflow = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_OPTIMISED_WF);
		if (skipOptimisedWorkflow == null) {
			skipOptimisedWorkflow = false;
		}
		
		FormulaFieldAPI.calculateHistoricalDataForSingleResource(formulaId, resourceId, range, false, historicalAlarm,skipOptimisedWorkflow);
		
		return false;
	}

}
