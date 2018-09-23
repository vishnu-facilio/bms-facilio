package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {

	@Override
	public boolean execute(Context context) throws Exception {
		super.execute(context);
		
		
		AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		if (assetContext.getId() > 0) {
			if (assetContext.getSpaceId() != -1) {
				Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(Collections.singleton(assetContext.getSpaceId()));
				assetContext.setSpace(spaceMap.get(assetContext.getSpaceId()));
			}
			
		}
		context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		return true;
	}
	
}
