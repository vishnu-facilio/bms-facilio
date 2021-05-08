package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetBasespaceWithHierarchyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long spaceid = (Long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		boolean fetchDeleted = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, false);
		if(spaceid > 0){
			BaseSpaceContext basespace = SpaceAPI.getBasespaceDetailsWithHierarchy(spaceid, fetchDeleted);
			context.put(FacilioConstants.ContextNames.BASE_SPACE, basespace);
		}
		return false;
	}

}
