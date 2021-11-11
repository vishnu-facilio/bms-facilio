package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateInventoryRequestCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequestContext = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(inventoryRequestContext)) {
            for (V3InventoryRequestContext inventoryRequestContexts : inventoryRequestContext) {
                if (inventoryRequestContexts != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    Map<Long, List<UpdateChangeSet>> map = null;
                    FacilioModule module = modBean.getModule(moduleName);
                    List<FacilioField> fields = modBean.getAllFields(moduleName);
                    FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
                    if (inventoryRequestContexts.getId() > 0) {
                        map = RecordAPI.updateRecord(inventoryRequestContexts, module, fields, true);
                        if (inventoryRequestContexts.getLineItems() != null) {
                            DeleteRecordBuilder<InventoryRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<InventoryRequestLineItemContext>()
                                    .module(lineModule)
                                    .andCondition(CriteriaAPI.getCondition("INVENTORY_REQUEST_ID", "inventoryRequestId", String.valueOf(inventoryRequestContexts.getId()), NumberOperators.EQUALS));
                            deleteBuilder.delete();
                            updateLineItems(inventoryRequestContexts);
                            V3RecordAPI.addRecord(false, inventoryRequestContexts.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
                        }
                    }
                }
            }
        }
        return false;
    }

    private void updateLineItems(V3InventoryRequestContext inventoryRequestContext) throws Exception {
        List<V3InventoryRequestLineItemContext> rotatingItems = new ArrayList<V3InventoryRequestLineItemContext>();
        V3InventoryRequestContext invReq = new V3InventoryRequestContext();
        invReq.setId(inventoryRequestContext.getId());

        for (V3InventoryRequestLineItemContext lineItemContext : inventoryRequestContext.getLineItems()) {
            lineItemContext.setInventoryRequestId(invReq);
            if (inventoryRequestContext.isIssued()) {
                lineItemContext.setIssuedQuantity(lineItemContext.getQuantity());
            }
            if (inventoryRequestContext.getParentId() != null) {
                lineItemContext.setParentId(inventoryRequestContext.getParentId());
            }
            if (inventoryRequestContext.getStoreRoom() != null && inventoryRequestContext.getStoreRoom().getId() > 0) {
                lineItemContext.setStoreRoom(inventoryRequestContext.getStoreRoom());
            }
            if (lineItemContext.getAssetIds() != null && lineItemContext.getAssetIds().size() > 0) {
                V3AssetContext lineItemAsset = new V3AssetContext();
                lineItemAsset.setId(lineItemContext.getAssetIds().get(0));
                lineItemContext.setAsset(lineItemAsset);
                lineItemContext.setQuantity(1.0);
                if (inventoryRequestContext.isIssued()) {
                    lineItemContext.setIssuedQuantity(1.0);
                }
                for (int i = 1; i < lineItemContext.getAssetIds().size(); i++) {
                    V3InventoryRequestLineItemContext lineItem = new V3InventoryRequestLineItemContext();
                    V3AssetContext asset = new V3AssetContext();
                    asset.setId(lineItemContext.getAssetIds().get(i));
                    lineItem.setAsset(asset);
                    lineItem.setInventoryType(lineItemContext.getInventoryType());
                    if (lineItemContext.getInventoryType() == InventoryType.ITEM.getValue()) {
                        lineItem.setItemType(lineItemContext.getItemType());
                    } else if (lineItemContext.getInventoryType() == InventoryType.TOOL.getValue()) {
                        lineItem.setToolType(lineItemContext.getToolType());
                    }

                    lineItem.setQuantity(1.0);
                    if (inventoryRequestContext.isIssued()) {
                        lineItem.setIssuedQuantity(1.0);
                    }

                    lineItem.setInventoryRequestId(invReq);
                    if (inventoryRequestContext.getParentId() > 0) {
                        lineItem.setParentId(inventoryRequestContext.getParentId());
                    }
                    if (inventoryRequestContext.getStoreRoom() != null && inventoryRequestContext.getStoreRoom().getId() > 0) {
                        lineItem.setStoreRoom(inventoryRequestContext.getStoreRoom());
                    }

                    rotatingItems.add(lineItem);
                }
            }

        }
        inventoryRequestContext.getLineItems().addAll(rotatingItems);
    }


}
