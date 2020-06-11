package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class AddAssetToDepreciationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);

        if (id != null && assetId != null) {
            AssetDepreciationContext assetDepreciationContext = AssetDepreciationAPI.getAssetDepreciation(id);
            if (assetDepreciationContext == null) {
                throw new IllegalArgumentException("Depreciation is not found");
            }
            AssetContext assetInfo = AssetsAPI.getAssetInfo(assetId);
            if (assetInfo == null) {
                throw new IllegalArgumentException("Invalid asset");
            }

            // check whether that asset is found in other depreciation - for now one asset can have one depreciation
            AssetDepreciationContext depreciationOfAsset = AssetDepreciationAPI.getDepreciationOfAsset(assetId);
            if (depreciationOfAsset != null) {
                throw new IllegalArgumentException("Asset already found in depreciation: " + depreciationOfAsset.getName());
            }

            AssetDepreciationAPI.addAsset(assetDepreciationContext.getId(), Collections.singletonList(assetId));

            List<AssetDepreciationCalculationContext> assetDepreciationCalculationContexts = AssetDepreciationAPI.calculateAssetDepreciation(
                    assetDepreciationContext, assetInfo, null);
            AssetDepreciationAPI.saveDepreciationCalculationList(assetDepreciationCalculationContexts, assetDepreciationContext);
        }

        return false;
    }
}
