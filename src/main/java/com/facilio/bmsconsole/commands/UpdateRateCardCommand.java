package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.util.TenantsAPI;

public class UpdateRateCardCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		TenantsAPI.updateRateCard((RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT));
		return false;
	}

}
