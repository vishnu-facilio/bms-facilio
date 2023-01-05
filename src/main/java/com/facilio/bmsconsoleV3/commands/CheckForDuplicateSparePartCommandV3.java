package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CheckForDuplicateSparePartCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AssetSpareParts> assetSpareParts = recordMap.get(ModName);
        for (AssetSpareParts assetSparePart: assetSpareParts
        ) {
            AssetSpareParts existingSparePart = V3AssetAPI.getSparePart(assetSparePart.getItemType(),assetSparePart.getAsset());
            if (existingSparePart != null) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selected Item is already a spare part for this asset");
            }
        }
        return false;
    }
}
