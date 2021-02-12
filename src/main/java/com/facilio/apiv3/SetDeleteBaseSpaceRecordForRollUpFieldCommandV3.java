package com.facilio.apiv3;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class SetDeleteBaseSpaceRecordForRollUpFieldCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> recordIds = Constants.getRecordIds(context);
		for(Long spaceId:recordIds) {
			BaseSpaceContext toBeDeletedBaseSpaceContext = SpaceAPI.getBaseSpace(spaceId, true);
			if(toBeDeletedBaseSpaceContext != null) {
				context.put(FacilioConstants.ContextNames.RECORD, toBeDeletedBaseSpaceContext);
				
			}
		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		return false;
	}

}
