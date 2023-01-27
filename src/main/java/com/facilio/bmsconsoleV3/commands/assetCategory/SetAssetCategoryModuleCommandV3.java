package com.facilio.bmsconsoleV3.commands.assetCategory;

import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SetAssetCategoryModuleCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3AssetCategoryContext> assetCategoryContexts = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isEmpty(assetCategoryContexts)) {
            return false;
        }

        for (V3AssetCategoryContext assetCategoryContext : assetCategoryContexts) {
            if (assetCategoryContext.getAssetModuleID() != null && assetCategoryContext.getAssetModuleID() > 0L) {
                FacilioModule module = Constants.getModBean().getModule(assetCategoryContext.getAssetModuleID());
                if (module != null) {
                    assetCategoryContext.setModuleName(module.getName());
                }
            }
        }

        return false;
    }
}
