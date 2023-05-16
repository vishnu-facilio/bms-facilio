package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateRotatingItemBeforeUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,List>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        Map<Long,V3AssetContext> oldAssetRecordMap = (Map<Long,V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP)).get("asset"));
        if(CollectionUtils.isNotEmpty(assetList)) {
            for (V3AssetContext asset : assetList) {
                V3AssetContext oldAsset =oldAssetRecordMap.get(asset.getId());
                if ((asset.getRotatingItemType() != null && asset.getRotatingItemType().getId() > 0) && (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR ,"An asset can be either be a Rotating Item or a Rotating Tool. Can't be both");
                }
                if(!asset.getCanUpdateRotatingAsset()) {
                    if((oldAsset.getRotatingItemType()!=null && asset.getRotatingItemType()!=null  && oldAsset.getRotatingItemType().getId()!= asset.getRotatingItemType().getId())
                            || (oldAsset.getRotatingItemType()!=null &&  asset.getRotatingItemType()==null) || (oldAsset.getRotatingItemType()==null &&  asset.getRotatingItemType()!=null)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating Item Type cannot be updated");
                    }
                    if ((asset.getRotatingItemType() == null && asset.getStoreRoom() != null) || (oldAsset.getStoreRoom() != null && asset.getStoreRoom() != null && oldAsset.getStoreRoom().getId() != asset.getStoreRoom().getId()) || (oldAsset.getStoreRoom() != null && asset.getStoreRoom() == null) || (oldAsset.getStoreRoom() == null && asset.getStoreRoom() != null)) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be updated");
                    }
                    if (asset.getRotatingItemType() != null && ((oldAsset.getSpace() != null && asset.getSpace() != null && oldAsset.getSpace().getId() != asset.getSpace().getId()) || (oldAsset.getSpace() != null && asset.getSpace() == null) || (oldAsset.getSpace() == null && asset.getSpace() != null))) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Asset Location cannot be updated");
                    }
                }
            }
        }

        return false;
    }
}
