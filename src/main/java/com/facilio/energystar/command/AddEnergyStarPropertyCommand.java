package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarPropertyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		propContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		propContext.setBuildingContext(SpaceAPI.getBuildingSpace(propContext.getBuildingId()));
		
		String propertId = EnergyStarSDK.addProperty(customer, propContext);
		
		propContext.setEnergyStarPropertyId(propertId);
		
		EnergyStarUtil.addEnergyStarProperty(propContext);
		return false;
	}

}
