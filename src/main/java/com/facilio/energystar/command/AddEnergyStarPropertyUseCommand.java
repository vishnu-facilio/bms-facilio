package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarProperyUseContext;
import com.facilio.energystar.util.EnergyStarUtil;

public class AddEnergyStarPropertyUseCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarPropertyContext propContext = (EnergyStarPropertyContext) context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT);
		
		if(propContext.getPropertyUseContexts() != null) {
			
			//add property use and set id to propContext and update it;
			
			
			for(EnergyStarProperyUseContext propertyUse : propContext.getPropertyUseContexts()) {
				propertyUse.setPropertId(propContext.getId());
				EnergyStarUtil.addEnergyStarPropertyUse(propertyUse);
			}
		}
		
		return false;
	}

}
