package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class AddFormulaFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FormulaFieldContext formula = (FormulaFieldContext) context.get(FacilioConstants.ContextNames.FORMULA_FIELD);
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null during addition");
		}
		long formulaId = FormulaFieldAPI.addFormulaField(formula);
		
		if (formula.getTriggerTypeEnum() == TriggerType.SCHEDULE) {
			FacilioTimer.scheduleOneTimeJob(formulaId, "HistoricalFormulaFieldCalculator", 30, "priority");
		}
		return false;
	}
}
