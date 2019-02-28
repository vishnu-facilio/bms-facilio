package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {

	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			super.execute(context);
			AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (assetContext != null && assetContext.getId() > 0) {
				if (assetContext.getSpaceId() != -1) {
					BaseSpaceContext space = SpaceAPI.getBaseSpace(assetContext.getSpaceId());
					assetContext.setSpace(space);
				}
				
			}
			context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		}
		return false;
	}
	
}
