package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarEnableCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer == null) {
			customer = EnergyStarUtil.getEnergyStarCustomerContext();
			
			//add customer here
			
			customer = EnergyStarUtil.addEnergyStarCustomer(customer);
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, customer);
		
		return false;
	}

}
