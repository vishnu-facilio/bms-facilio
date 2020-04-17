package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class GetEnergyStarCustomerCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarCustomerContext customer = EnergyStarUtil.getEnergyStarCustomer();
		context.put(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, customer);
		return false;
	}

}
