package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.AssetDepreciationAPI;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class AddAssetToDepreciationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Long assetId = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);

        if (id != null && assetId != null) {

            FacilioModule assetDepreciationRelModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);

            Map<String,Object> prop = new HashMap<>();
            prop.put("asset", AssetsAPI.getAssetInfo(assetId));
            prop.put("depreciation", AssetDepreciationAPI.getAssetDepreciation(id));

            V3Util.createRecord(assetDepreciationRelModule, prop);
        }

        return false;
    }
}
