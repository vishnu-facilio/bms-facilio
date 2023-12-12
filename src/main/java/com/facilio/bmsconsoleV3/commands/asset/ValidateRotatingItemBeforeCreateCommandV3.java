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

public class ValidateRotatingItemBeforeCreateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,List>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        if(CollectionUtils.isNotEmpty(assetList)){
            for(V3AssetContext asset : assetList){
                if ((asset.getRotatingItemType() != null && asset.getRotatingItemType().getId() > 0) && (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR ,"An asset can be either be a Rotating Item or a Rotating Tool. Can't be both");
                }
                if((asset.getRotatingItemType()!=null && asset.getStoreRoom()==null) || (asset.getRotatingItemType()==null && asset.getStoreRoom()!=null)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating Item Type needs to be mapped to a storeroom");
                }
                if(asset.getStoreRoom()!=null){
                    V3StoreRoomContext storeroom = V3StoreroomApi.getStoreRoom(asset.getStoreRoom().getId());
                    if(storeroom.getSite().getId()!= asset.getSiteId()) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Asset's site doesn't match storeroom's located site");
                    }
                    if(asset.getSpace()!=null && asset.getSpace().getId() != asset.getSiteId()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Location cannot be entered for a Rotating Item");
                    }
                } else {
                    if(asset.getBin() != null && asset.getBin().getId() > 0){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please Select StoreRoom to choose bin");
                    }
                }
            }
        }

        return false;
    }
}
