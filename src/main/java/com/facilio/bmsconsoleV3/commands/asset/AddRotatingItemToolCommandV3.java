package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddRotatingItemToolCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = new ArrayList<>();
        if(context.get(FacilioConstants.ContextNames.RECORD)!=null){
            assetList = (List<V3AssetContext>) context.get(FacilioConstants.ContextNames.RECORD);
        }
        else{
            assetList = (List<V3AssetContext>) (((Map<String,List>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        }
        if(CollectionUtils.isNotEmpty(assetList)){
            for(V3AssetContext asset:assetList){
                if (asset.getRotatingItemType() != null && asset.getRotatingItemType().getId() > 0 && asset.getStoreRoom()!=null && asset.getStoreRoom().getId()>0) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
                    V3ItemContext item = V3ItemsApi.getItem(asset.getRotatingItemType().getId(),asset.getStoreRoom().getId());

                    if(item!=null){
                        //item stocking
                        V3AssetAPI.stockRotatingItem(item,asset);
                        //update rotating item value in asset
                        V3AssetAPI.updateRotatingItem(item,asset);
                    }else{
                        // item creation
                        V3ItemContext rotatingItem = new V3ItemContext();
                        rotatingItem.setItemType(asset.getRotatingItemType());
                        rotatingItem.setStoreRoom(asset.getStoreRoom());
                        rotatingItem.setCostType(CostType.FIFO.getIndex());
                        FacilioContext rotatingItemCreated =  V3Util.createRecord(itemModule, FieldUtil.getAsJSON(rotatingItem));
                        if(rotatingItemCreated.get("records")!=null && ((List<V3ItemContext>) rotatingItemCreated.get("records")).size() > 0){
                            item  =((List<V3ItemContext>) rotatingItemCreated.get("records")).get(0);
                        }
                        //item stocking
                        V3AssetAPI.stockRotatingItem(item,asset);

                        //update rotating item value in asset
                        V3AssetAPI.updateRotatingItem(item,asset);
                    }
                    item = V3ItemsApi.getItemWithSupplements(asset.getRotatingItemType().getId(),asset.getStoreRoom().getId());
                    // asset history
                    JSONObject info = new JSONObject();
                    context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, "assetactivity");
                    info.put("storeroom", item.getStoreRoom().getName());
                    CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.ADDED_TO_INVENTORY, info,
                            (FacilioContext) context);

                }
                //removing tools handling for now
//                    if (asset.getRotatingTool() != null && asset.getRotatingTool().getId() > 0) {
//                        V3ToolContext tool = asset.getRotatingTool();
//                        FacilioChain c = TransactionChainFactoryV3.getAddOrUpdateToolStockTransactionChain();
//                        c.getContext().put(FacilioConstants.ContextNames.TOOL, tool);
//                        c.getContext().put(FacilioConstants.ContextNames.ROTATING_ASSET, asset);
//                        c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
//                        c.execute();
//                    }
            }
        }
        return false;
    }
}

