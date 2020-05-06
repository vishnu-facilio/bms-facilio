package com.facilio.energystar.command;

import java.util.List;

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
		
		List<EnergyStarMeterContext> meters = (List<EnergyStarMeterContext>) context.get(EnergyStarUtil.ENERGY_STAR_METER_CONTEXTS);
		
		if(meters == null && propContext != null && propContext.getMeterContexts() != null) {
			meters = propContext.getMeterContexts();
		}
		
		if(meters != null) {
			
			for(EnergyStarMeterContext meter : meters) {
				
				if(meter.getMeterId() > 0) {
					meter.setMeterContext(AssetsAPI.getAssetInfo(meter.getMeterId()));
				}
				
				if(meter.getEnergyStarMeterId() == null) {
					String energyStarMeterId = EnergyStarSDK.addPropertyMeter(customer, propContext, meter);
					
					meter.setEnergyStarMeterId(energyStarMeterId);
				}
				if(meter.getPropertyId() < 0 && propContext != null) {
					meter.setPropertyId(propContext.getId());
				}
				EnergyStarUtil.addEnergyStarMetercontext(meter);
			}
		}
		
		return false;
	}

}
