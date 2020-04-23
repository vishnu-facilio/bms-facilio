package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarMeterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		EnergyStarCustomerContext customer = (EnergyStarCustomerContext) context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT);
		
		if(propContext.getMeterContexts() != null) {
			
			
			for(EnergyStarMeterContext meter : propContext.getMeterContexts()) {
				
				meter.setMeterContext(AssetsAPI.getAssetInfo(meter.getMeterId()));
				
				String energyStarMeterId = EnergyStarSDK.addPropertyMeter(customer, propContext, meter);
				
				meter.setEnergyStarMeterId(energyStarMeterId);
				
				meter.setProperyId(propContext.getId());
				EnergyStarUtil.addEnergyStarMetercontext(meter);
			}
		}
		
		return false;
	}

}
