package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllZoneChildrenCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long zoneId = (Long) context.get(FacilioConstants.ContextNames.ZONE_ID); 
		
		if (zoneId != null) {
			context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getZoneChildren(zoneId));
		}
		
		return false;
	}

}
