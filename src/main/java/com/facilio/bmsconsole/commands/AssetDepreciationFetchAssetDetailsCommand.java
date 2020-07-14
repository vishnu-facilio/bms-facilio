package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

public class AssetDepreciationFetchAssetDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        AssetDepreciationContext assetDepreciation = (AssetDepreciationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.ASSET_DEPRECIATION, id);
        if (assetDepreciation != null) {
            assetDepreciation.setAssetDepreciationRelList(AssetDepreciationAPI.getRelList(assetDepreciation.getId()));
        }
        return false;
    }
}
