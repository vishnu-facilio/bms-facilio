package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.util.TenantsAPI;

public class AddRateCardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		TenantsAPI.addRateCard((RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT));
		return false;
	}

}
