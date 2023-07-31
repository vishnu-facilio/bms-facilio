package com.facilio.assetcatergoryfeature.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.assetcatergoryfeature.AssetCategoryFeatureActivationContext;
import com.facilio.assetcatergoryfeature.util.AssetCategoryFeatureStatusUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class FetchAssetCategoryLevelStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long categoryId = (Long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);

        if (categoryId != null) {
            AssetCategoryFeatureActivationContext result = AssetCategoryFeatureStatusUtil.fetchStatusFromCategory(categoryId);
            context.put(FacilioConstants.ContextNames. ASSET_CATEGORY_FEATURE_ACTIVATION ,result);
        }

        return false;
    }

}
