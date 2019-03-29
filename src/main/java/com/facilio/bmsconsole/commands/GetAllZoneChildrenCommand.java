package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllZoneChildrenCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> zoneIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); 
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getZoneChildren(zoneIds));
		
		return false;
	}

}
