package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAssetToDepreciationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);

        if (id != null && assetId != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);

            AssetDepreciationContext assetDepreciationContext = AssetDepreciationAPI.getAssetDepreciation(id);
            if (assetDepreciationContext == null) {
                throw new IllegalArgumentException("Depreciation is not found");
            }
            AssetContext assetInfo = AssetsAPI.getAssetInfo(assetId);
            if (assetInfo == null) {
                throw new IllegalArgumentException("Invalid asset");
            }

            List<AssetDepreciationRelContext> assetDepreciationRelList = assetDepreciationContext.getAssetDepreciationRelList();
            if (assetDepreciationRelList == null) {
                assetDepreciationRelList = new ArrayList<>();
            }
            for (AssetDepreciationRelContext relContext : assetDepreciationRelList) {
                if (relContext.getAssetId() == assetId) {
                    throw new IllegalArgumentException("Asset already added to the depreciation");
                }
            }
            AssetDepreciationAPI.addAsset(assetDepreciationContext.getId(), Collections.singletonList(assetId));
        }

        return false;
    }
}
