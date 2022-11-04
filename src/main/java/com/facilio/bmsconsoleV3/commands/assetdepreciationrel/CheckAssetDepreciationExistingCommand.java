package com.facilio.bmsconsoleV3.commands.assetdepreciationrel;

import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class CheckAssetDepreciationExistingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> assetIds = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_IDS);

        // check whether that asset is found in other depreciation - for now one asset can have one depreciation
        AssetDepreciationContext depreciationOfAsset = AssetDepreciationAPI.getDepreciationOfAsset(assetIds.get(0));

        if (depreciationOfAsset != null) {
            throw new IllegalArgumentException("Asset already found in depreciation: " + depreciationOfAsset.getName());
        }
        return false;
    }
}
