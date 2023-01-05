package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.asset.AssetSpareParts;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CheckForRotatableItemCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String ModName = FacilioConstants.ContextNames.ASSET_SPARE_PARTS;
        Map<String, List> recordMap = (Map<String, List>) context.get("recordMap");
        List<AssetSpareParts> assetSpareParts = recordMap.get(ModName);
        for (AssetSpareParts assetSparePart: assetSpareParts
             ) {
            V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,assetSparePart.getItemType().getId(),V3ItemTypesContext.class);
            if(itemType.isRotating == null || !itemType.isRotating) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selected Item should be of a rotating type");
            }
        }
        return false;
    }
}
