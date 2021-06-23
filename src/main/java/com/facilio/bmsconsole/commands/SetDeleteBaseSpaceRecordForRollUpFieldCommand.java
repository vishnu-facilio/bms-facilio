package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class SetDeleteBaseSpaceRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		BaseSpaceContext toBeDeletedBaseSpaceContext = SpaceAPI.getBaseSpace(spaceId, true);
		if(toBeDeletedBaseSpaceContext != null) {
			context.put(FacilioConstants.ContextNames.RECORD, toBeDeletedBaseSpaceContext);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		}
		
		return false;
	}

}
