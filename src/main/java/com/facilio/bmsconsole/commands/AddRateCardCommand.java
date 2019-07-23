package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.util.TenantsAPI;

public class AddRateCardCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		TenantsAPI.addRateCard((RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT));
		return false;
	}

}
