package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class AssetMovementPropsSetCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetMovementContext assetMovementContext = (AssetMovementContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		
		if(assetMovementContext!=null) {
			assetMovementContext.setIsMovementNeeded(true);
			if(assetMovementContext.getAssetId() <= 0) {
				throw new IllegalArgumentException("Asset Id cannot be null");
			}
			AssetContext asset = AssetsAPI.getAssetInfo(assetMovementContext.getAssetId());
			if(StringUtils.isNotEmpty(assetMovementContext.getToGeoLocation()) && StringUtils.isNotEmpty(asset.getCurrentLocation())) {
				String[] latLng = assetMovementContext.getToGeoLocation().trim().split("\\s*,\\s*");
				double newLat = Double.parseDouble(latLng[0]);
				double newLng = Double.parseDouble(latLng[1]);
				
				if(AssetsAPI.isWithInLocation(asset.getCurrentLocation(), newLat, newLng, asset.getBoundaryRadius())) {
					assetMovementContext.setIsMovementNeeded(false);
				}
			}
			
			if(assetMovementContext.getRequestedBy() == null) {
				assetMovementContext.setRequestedBy(AccountUtil.getCurrentUser());
		    }
			assetMovementContext.setApprovalNeeded(asset.getMoveApprovalNeeded());
			context.put(FacilioConstants.ContextNames.RECORD, assetMovementContext);
		}
		return false;
	}

}
