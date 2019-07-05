package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.mv.util.MVUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AddFormulaFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
			BmsJobUtil.scheduleOneTimeJobWithProps(formulaId, "HistoricalFormulaFieldCalculator", 30, "priority", props);
		}
		return false;
	}

}
