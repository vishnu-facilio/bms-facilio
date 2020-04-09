package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ValidateAssetDepreciationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AssetDepreciationContext> assetDepreciationList = (List<AssetDepreciationContext>) CommandUtil.getModuleDataList(context, FacilioConstants.ContextNames.ASSET_DEPRECIATION);
        if (CollectionUtils.isNotEmpty(assetDepreciationList)) {
            AssetDepreciationContext assetDepreciationContext = assetDepreciationList.get(0);
            if (assetDepreciationContext.getFrequency() < 0 || assetDepreciationContext.getFrequencyTypeEnum() == null) {
                throw new IllegalArgumentException("Frequency cannot be empty");
            }
//            if (assetDepreciationContext.getAssetId() < 0) {
//                throw new IllegalArgumentException("Asset cannot be empty");
//            }
            if (assetDepreciationContext.getDepreciationTypeEnum() == null) {
                throw new IllegalArgumentException("Depreciation type cannot be empty");
            }
            if (StringUtils.isEmpty(assetDepreciationContext.getName())) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
        }
        return false;
    }
}
