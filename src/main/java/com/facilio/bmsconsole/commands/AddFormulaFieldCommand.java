package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateRange;

public class AddFormulaFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null during addition");
		}
		long formulaId = FormulaFieldAPI.addFormulaField(formula);
		
		JSONObject props = null;
		DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		if (dateRange != null) {
			props = FieldUtil.getAsJSON(dateRange);
		}
		Boolean skipFromulaCalculation = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING);
		if(skipFromulaCalculation == null || skipFromulaCalculation.equals(Boolean.FALSE)) {
			
			FacilioChain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
			FacilioContext formulaFieldcontext = historicalCalculation.getContext();
			
			formulaFieldcontext.put(FacilioConstants.ContextNames.FORMULA_FIELD, formulaId);
			formulaFieldcontext.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);	
			historicalCalculation.execute();
		}
		return false;
	}

}
