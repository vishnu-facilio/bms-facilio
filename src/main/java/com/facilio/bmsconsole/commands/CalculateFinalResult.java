package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TenantsAPI;

public class CalculateFinalResult extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		double formulaValue = (double) context.get(TenantsAPI.FORMULA_SUM_VALUE);
		double utilityValue = (double) context.get(TenantsAPI.UTILITY_SUM_VALUE);
		
		context.put(TenantsAPI.FINAL_VALUES, formulaValue+utilityValue);
		return false;
	}

}
