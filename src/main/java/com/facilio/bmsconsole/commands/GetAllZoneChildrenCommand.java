package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllZoneChildrenCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> zoneIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); 
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getZoneChildren(zoneIds));
		
		return false;
	}

}
