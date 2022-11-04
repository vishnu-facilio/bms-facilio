package com.facilio.bmsconsoleV3.commands.assetdepreciationrel;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetDepreciationRelContext;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ValidateAssetDepreciationRelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(records), "Asset Depreciation record map is empty");

        List<Long> assetIds = new ArrayList<>();

        for (ModuleBaseWithCustomFields record : records) {

            V3AssetDepreciationRelContext assetDepreciationRel = (V3AssetDepreciationRelContext) record;

            AssetDepreciationContext depreciation = assetDepreciationRel.getDepreciation();
            AssetContext asset = assetDepreciationRel.getAsset();

            FacilioUtil.throwIllegalArgumentException(asset == null, "Asset should not be null while adding Asset DepreciationRel");

            FacilioUtil.throwIllegalArgumentException(depreciation == null, "Asset Depreciation should not be null while adding Asset DepreciationRel");

            AssetDepreciationContext assetDepreciationContext = AssetDepreciationAPI.getAssetDepreciation(depreciation.getId());

            FacilioUtil.throwIllegalArgumentException(assetDepreciationContext == null,"Depreciation is not found");

            AssetContext assetInfo = AssetsAPI.getAssetInfo(asset.getId());

            FacilioUtil.throwIllegalArgumentException(assetInfo == null,"Invalid asset");

            FacilioUtil.throwIllegalArgumentException(assetInfo.getUnitPrice() <= 0 , "Unit Price is empty");

            assetIds.add(asset.getId());
        }

        context.put(FacilioConstants.ContextNames.ASSET_IDS,assetIds);

        return false;
    }
}
