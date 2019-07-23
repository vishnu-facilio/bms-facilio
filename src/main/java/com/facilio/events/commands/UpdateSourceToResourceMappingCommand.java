package com.facilio.events.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventAPI;

public class UpdateSourceToResourceMappingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		long assetId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);
		if(assetId == -1) {
			throw new IllegalArgumentException("Invalid Asset ID specified during updation of Node-Asset mapping");
		}
		
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if(id == -1) {
			throw new IllegalArgumentException("Invalid ID specified during updation of Node-Asset mapping");
		}
		
		EventAPI.updateResourceForSource(assetId, id, AccountUtil.getCurrentOrg().getOrgId());
		
		return false;
	}

}
