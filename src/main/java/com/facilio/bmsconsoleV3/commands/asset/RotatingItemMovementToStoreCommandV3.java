package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class RotatingItemMovementToStoreCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,List>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if(CollectionUtils.isNotEmpty(assetList)){
            for(V3AssetContext asset : assetList){
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("moveToStoreRoom") && (boolean) bodyParams.get("moveToStoreRoom")){
                    if(asset.getStoreRoom()==null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR ,"Please enter a storeroom to move the asset to");
                    }
                    if(asset.getRotatingItemType()==null){
                      throw new RESTException(ErrorCode.VALIDATION_ERROR ,"Rotating ItemType cannot be empty");
                    }
                    if(V3AssetAPI.isAssetMaintainable(asset)){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR ,"Please close all open Work Orders for the Asset before moving it to Storeroom");
                    }
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
                    V3ItemContext item = V3ItemsApi.getItem(asset.getRotatingItemType().getId(),asset.getStoreRoom().getId());
                    if(item!=null){
                        //item stocking
                        V3AssetAPI.stockRotatingItem(item,asset);
                    }else{
                        // item creation
                        V3ItemContext rotatingItem = new V3ItemContext();
                        rotatingItem.setItemType(asset.getRotatingItemType());
                        rotatingItem.setStoreRoom(asset.getStoreRoom());
                        rotatingItem.setCostType(ItemContext.CostType.FIFO.getIndex());
                        FacilioContext rotatingItemCreated =  V3Util.createRecord(itemModule, FieldUtil.getAsJSON(rotatingItem));
                        if(rotatingItemCreated.get("records")!=null && ((List<V3ItemContext>) rotatingItemCreated.get("records")).size() > 0){
                            item =((List<V3ItemContext>) rotatingItemCreated.get("records")).get(0);
                        }
                        //item stocking
                        V3AssetAPI.stockRotatingItem(item,asset);
                    }
                    item = V3ItemsApi.getItemWithSupplements(asset.getRotatingItemType().getId(),asset.getStoreRoom().getId());

                    //updating asset's site and location with store's located site
                    if(item.getStoreRoom().getSite()!=null){
                        asset.setSiteId(item.getStoreRoom().getSite().getId());

                        V3BaseSpaceContext assetSpace = new V3BaseSpaceContext();
                        assetSpace.setId(item.getStoreRoom().getSite().getId());
                        asset.setSpace(assetSpace);
                    }
                    //updating rotating item
                    asset.setRotatingItem(item);
                    asset.setIsUsed(false);

                    asset.setCanUpdateRotatingAsset(true);

                    // asset history
                    JSONObject info = new JSONObject();
                    info.put("storeroom", item.getStoreRoom().getName());
                    CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.MOVE_TO_STOREROOM, info,
                            (FacilioContext) context);
                }
            }
        }
        return false;
    }
}
