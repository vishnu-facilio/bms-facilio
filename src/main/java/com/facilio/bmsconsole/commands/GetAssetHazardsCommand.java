package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetHazardContext;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetHazardsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetContext asset = (AssetContext)context.get(FacilioConstants.ContextNames.ASSET);
		if(asset != null && asset.getId() > 0 && AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			List<AssetHazardContext> assetHazards = HazardsAPI.fetchAssetAssociatedHazards(asset.getId());
			if(CollectionUtils.isNotEmpty(assetHazards)) {
				List<Long> hazardIds = new ArrayList<Long>();
				for(AssetHazardContext ah : assetHazards) {
					hazardIds.add(ah.getHazard().getId());
				}
				asset.setHazardIds(hazardIds);
			}
		}
		return false;
	}

}
