package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class AddEnPICommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext enpi = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (enpi == null) {
			throw new IllegalArgumentException("EnPI cannot be null during addition");
		}
		long enpiId = FormulaFieldAPI.addEnPI(enpi);
		FacilioTimer.scheduleOneTimeJob(enpiId, "HistoricalENPICalculator", 30, "priority");
		return false;
	}
}
