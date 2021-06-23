package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class AssetMovementPropsSetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetMovementContext assetMovementContext = (AssetMovementContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(assetMovementContext != null) {
			if(assetMovementContext.getAssetId() <= 0) {
				throw new IllegalArgumentException("Asset Id cannot be null");
			}
			if(!AssetsAPI.getPendingAssetMovementRecordForAsset(assetMovementContext.getAssetId())) {
				throw new IllegalArgumentException("Only one movement request can be placed at a time");
			}
			AssetContext asset = AssetsAPI.getAssetInfo(assetMovementContext.getAssetId());
			if(assetMovementContext.getRequestedBy() == null) {
				assetMovementContext.setRequestedBy(AccountUtil.getCurrentUser());
		    }
			assetMovementContext.setApprovalNeeded(asset.getMoveApprovalNeeded());
			context.put(FacilioConstants.ContextNames.RECORD, assetMovementContext);
		}
		return false;
	}

}
