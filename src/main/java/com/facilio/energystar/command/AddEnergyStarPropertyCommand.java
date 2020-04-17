package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarPropertyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		
		propContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		//addproperyHere
		
		EnergyStarUtil.addEnergyStarProperty(propContext);
		return false;
	}

}
