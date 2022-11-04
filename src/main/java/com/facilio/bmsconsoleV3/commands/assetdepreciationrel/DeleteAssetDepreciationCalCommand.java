package com.facilio.bmsconsoleV3.commands.assetdepreciationrel;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationCalculationContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetDepreciationRelContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DeleteAssetDepreciationCalCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record : records) {

            V3AssetDepreciationRelContext assetDepreciationRel = (V3AssetDepreciationRelContext) record;

            long depreciationId = assetDepreciationRel.getDepreciation().getId();
            long assetId = assetDepreciationRel.getAsset().getId();

            AssetDepreciationContext assetDepreciationContext = AssetDepreciationAPI.getAssetDepreciation(depreciationId);
            AssetContext assetInfo = AssetsAPI.getAssetInfo(assetId);

            AssetDepreciationAPI.deleteAssetDepreciationCalculation(assetDepreciationContext, assetInfo);

        }

        return false;
    }
}
