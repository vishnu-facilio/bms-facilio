package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.constants.EventConstants;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.OrgInfo;

public class UpdateNodeToAssetMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String node = (String) context.get(EventConstants.EventContextNames.NODE);
		if(node == null || node.isEmpty()) {
			throw new IllegalArgumentException("Invalid node specified during updation of Node-Asset mapping");
		}
		
		long assetId = (long) context.get(EventConstants.EventContextNames.ASSET_ID);
		if(assetId == -1) {
			throw new IllegalArgumentException("Invalid Asset ID specified during updation of Node-Asset mapping");
		}
		
		EventAPI.updateAssetForNode(assetId, node, OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return false;
	}

}
