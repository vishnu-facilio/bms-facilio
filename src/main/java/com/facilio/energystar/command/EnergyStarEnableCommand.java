package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarEnableCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer == null) {
			
			boolean createAccount = (boolean) context.get(EnergyStarUtil.IS_CREATE_ACCOUNT);
			
			customer = EnergyStarUtil.getEnergyStarCustomerContext(createAccount);
			
			if(createAccount) {
				
				String id = EnergyStarSDK.createCustomer(customer);
				
				customer.setEnergyStarCustomerId(id);
				
				customer.setType(EnergyStarCustomerContext.Type.CREATED.getIntVal());
			}
			else {
				customer.setType(EnergyStarCustomerContext.Type.SHARED.getIntVal());
			}
			customer = EnergyStarUtil.addEnergyStarCustomer(customer);
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, customer);
		
		return false;
	}

}
