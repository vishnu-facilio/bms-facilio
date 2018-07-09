package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.TenantsAPI;

public class CalculateFinalResult implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		
		double formulaValue = (double) context.get(TenantsAPI.FORMULA_SUM_VALUE);
		double utilityValue = (double) context.get(TenantsAPI.UTILITY_SUM_VALUE);
		
		context.put(TenantsAPI.FINAL_VALUES, formulaValue+utilityValue);
		return false;
	}

}
