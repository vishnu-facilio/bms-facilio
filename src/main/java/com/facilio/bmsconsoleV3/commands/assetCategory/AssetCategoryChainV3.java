package com.facilio.bmsconsoleV3.commands.assetCategory;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;

public class AssetCategoryChainV3 {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getAssetCategoryChain(){
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new AddAssetCategoryModuleCommandV3());
        chain.addCommand(TransactionChainFactory.commonAddModuleChain());
        chain.addCommand(new UpdateCategoryAssetModuleIdCommandV3());
        return chain;
    }
}
