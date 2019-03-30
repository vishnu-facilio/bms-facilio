package com.facilio.leed.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.util.LeedAPI;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

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
