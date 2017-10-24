package com.facilio.leed.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;

public class LeedBuildingDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		LeedConfigurationContext leedData = LeedAPI.fetchLeedConfigurationContext(buildingId);	
		context.put(FacilioConstants.ContextNames.LEED, leedData);	
		return false;
	}
	
}
