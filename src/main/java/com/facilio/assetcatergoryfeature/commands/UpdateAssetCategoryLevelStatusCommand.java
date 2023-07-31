package com.facilio.assetcatergoryfeature.commands;


import com.facilio.beans.NamespaceBean;
import com.facilio.command.FacilioCommand;
import com.facilio.assetcatergoryfeature.AssetCategoryFeatureActivationContext;
import com.facilio.assetcatergoryfeature.util.AssetCategoryFeatureStatusUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.NSType;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;


public class UpdateAssetCategoryLevelStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long categoryId = (Long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);
        AssetCategoryFeatureActivationContext connectedCategoryContext = (AssetCategoryFeatureActivationContext) context.get(FacilioConstants.ContextNames. ASSET_CATEGORY_FEATURE_ACTIVATION );
        int nsType = (int) context.get(FacilioConstants.ContextNames.TYPE);

        if (categoryId != null) {
            AssetCategoryFeatureStatusUtil.updateCategoryLevelExecStatus(connectedCategoryContext, categoryId);
            NamespaceBean nsBean= Constants.getNsBean();
            nsBean.updateNsCacheWithCategory(categoryId, Collections.singletonList(NSType.valueOf(nsType)));
        }
        return false;
    }

}
