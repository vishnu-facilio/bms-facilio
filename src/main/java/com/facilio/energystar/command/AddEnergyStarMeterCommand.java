package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarMeterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		if(propContext.getMeterContexts() != null) {
			
			
			for(EnergyStarMeterContext meter : propContext.getMeterContexts()) {
				
				//add meter here;
				
				meter.setProperyId(propContext.getId());
				EnergyStarUtil.addEnergyStarMetercontext(meter);
			}
		}
		
		return false;
	}

}
