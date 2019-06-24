package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetForTypeCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Integer inventoryType = (Integer)context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		
		if(inventoryType == 1) {
			List<Long> itemTypeIds = (List<Long>)context.get(FacilioConstants.ContextNames.ITEM_TYPES_IDS);
			context.put(FacilioConstants.ContextNames.ASSETS, AssetsAPI.getAssetForItemType(itemTypeIds));	
		}
		else if(inventoryType == 2) {
			List<Long> toolTypeIds = (List<Long>)context.get(FacilioConstants.ContextNames.TOOL_TYPES_IDS);
			context.put(FacilioConstants.ContextNames.ASSETS, AssetsAPI.getAssetForToolType(toolTypeIds));	
		}
		return false;
	}

}
