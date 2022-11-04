package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepreciationChartCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        if (id != null && id > 0 && assetId != null && assetId > 0) {
            AssetDepreciationContext assetDepreciation = AssetDepreciationAPI.getAssetDepreciation(id);
            if (assetDepreciation == null) {
                throw new IllegalArgumentException("Invalid asset depreciation");
            }

            AssetContext assetContext = AssetsAPI.getAssetInfo(assetId);
            if (assetContext == null) {
                throw new IllegalArgumentException("Asset not found");
            }
            List<AssetDepreciationCalculationContext> assetDepreciationCalculationContexts = AssetDepreciationAPI.calculateAssetDepreciation(
                    assetDepreciation, assetContext, null);

            context.put("depreciationList", FieldUtil.getAsMapList(assetDepreciationCalculationContexts,AssetDepreciationCalculationContext.class));
        }
        return false;
    }
}
