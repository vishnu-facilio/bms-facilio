package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateRateCardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		TenantsAPI.updateRateCard((RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT));
		return false;
	}

}
