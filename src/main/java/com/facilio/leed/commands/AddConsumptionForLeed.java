package com.facilio.leed.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.UtilityProviderContext;
import com.facilio.leed.util.LeedAPI;

public class AddConsumptionForLeed implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long buildingId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(buildingId > 0)
		{
			List<UtilityProviderContext> utilityproviders = LeedAPI.getAllUtilityProviders(buildingId);
			
			BuildingContext buildingcontext = (BuildingContext)context.get(FacilioConstants.ContextNames.BUILDING);
			if(utilityproviders != null && !utilityproviders.isEmpty() && buildingcontext != null) {
				buildingcontext.setUtilityProviders(utilityproviders);
			}
			
			
		}
		return false;
	}

}
