package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventAPI;

public class UpdateSourceToResourceMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		if(source == null || source.isEmpty()) {
			throw new IllegalArgumentException("Invalid node specified during updation of Node-Asset mapping");
		}
		
		long assetId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);
		if(assetId == -1) {
			throw new IllegalArgumentException("Invalid Asset ID specified during updation of Node-Asset mapping");
		}
		
		EventAPI.updateResourceForSource(assetId, source, AccountUtil.getCurrentOrg().getOrgId());
		
		return false;
	}

}
