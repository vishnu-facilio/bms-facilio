package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {

	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			super.execute(context);
			AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (assetContext != null && assetContext.getId() > 0) {
				AssetsAPI.loadAssetsLookups(Collections.singletonList(assetContext));
			}
			context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		}
		return false;
	}
	
}
