package com.facilio.energystar.command;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;

public class EnergyStarSyncPropertyMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<EnergyStarPropertyContext> propertyContexts = (List<EnergyStarPropertyContext>)context.get(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT);
		
		if(propertyContexts == null) {
			return false;
		}
		
		for(EnergyStarPropertyContext property : propertyContexts) {
			
			if(property.getEnergyStarPropertyId() != null) {
				
				EnergyStarSDK.fillPropertyMetaAndUseData(property);
				
				FacilioChain chain = TransactionChainFactory.updateEnergyStarPropertyChain();
				
				FacilioContext context1 = chain.getContext();
				
				context1.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, property);
				
				chain.execute();
			}
		}
		
		return false;
	}

}
