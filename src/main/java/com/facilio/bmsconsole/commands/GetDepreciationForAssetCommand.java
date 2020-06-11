package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetDepreciationForAssetCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        if (assetId != null) {
            AssetContext assetContext = AssetsAPI.getAssetInfo(assetId);
            if (assetContext == null) {
                throw new IllegalArgumentException("Invalid asset Id");
            }

            AssetDepreciationContext depreciationOfAsset = AssetDepreciationAPI.getDepreciationOfAsset(assetId);
            context.put(FacilioConstants.ContextNames.ASSET_DEPRECIATION, depreciationOfAsset);
        }
        return false;
    }
}
