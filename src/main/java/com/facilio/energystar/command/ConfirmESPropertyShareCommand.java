package com.facilio.energystar.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class ConfirmESPropertyShareCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(customer.getType() == EnergyStarCustomerContext.Type.SHARED.getIntVal() && customer.getEnergyStarCustomerId() != null &&customer.getShareStatus() == EnergyStarCustomerContext.Share_Status.SHARED.getIntVal()) {
			
			List<EnergyStarPropertyContext> properties = EnergyStarSDK.confirmPropertyShare(customer.getEnergyStarCustomerId());
			
			for(EnergyStarPropertyContext property : properties) {
				
				FacilioChain chain = TransactionChainFactory.addEnergyStarProperyOnlyChain();
				
				FacilioContext newContext = chain.getContext();
				
				newContext.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
				
				newContext.putAll(context);
				
				chain.execute();
				
			}
		}
		return false;
	}

}
