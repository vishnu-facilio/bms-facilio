package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import javax.xml.bind.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

public class SetWorkOrderItemsCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkorderItemContext> workOrderItems = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderItemsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);

        FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
//        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);


        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

        List<V3WorkorderItemContext> workorderItemslist = new ArrayList<>();
        List<V3WorkorderItemContext> itemToBeAdded = new ArrayList<>();

        long itemTypesId = -1;
        ApprovalState approvalState = null;
        List<Long> parentIds = new ArrayList<>();
        Map<Long,V3WorkOrderContext> workOrderMap = new HashMap<>();
        V3WorkOrderContext wo = new V3WorkOrderContext();
        if (CollectionUtils.isNotEmpty(workOrderItems)) {
            for (V3WorkorderItemContext workorderItem : workOrderItems) {
                long parentId = workorderItem.getParentId()>0 ? workorderItem.getParentId() : -1;

                if(workorderItem.getWorkorder()!= null && parentId < 0){
                    parentId = workorderItem.getWorkorder().getId();
                }
                if(parentId<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Work order cannot be null");
                }

                parentIds.add(parentId);
                long parentTransactionId = workorderItem.getParentTransactionId();

                if(workorderItem.getQuantity() <= 0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                V3ItemContext item = getItem(workorderItem.getItem().getId());
                itemTypesId = item.getItemType().getId();
                V3ItemTypesContext itemType = getItemType(itemTypesId);

                if(workOrderMap.get(parentId)==null){
                     wo = V3InventoryUtil.getWorkOrder(parentId);
                    workOrderMap.put(parentId,wo);
                }else{
                    wo = workOrderMap.get(parentId);
                }
               List<Long> servingSiteIds = item.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
               if(wo.getSiteId()>0 && !servingSiteIds.contains(wo.getSiteId())){
                   throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom does not serve the selected work order's site");
               }
                if (workorderItem.getRequestedLineItem() != null && workorderItem.getRequestedLineItem().getId() > 0) {
                    if(!V3InventoryRequestAPI.checkQuantityForWoItemNeedingApprovalV3(itemType, workorderItem.getRequestedLineItem(), workorderItem.getQuantity())) {
                        throw new IllegalArgumentException("Please check the quantity approved/issued in the request");
                    }
                }
                else if(workorderItem.getParentTransactionId() > 0) {
                    if(!InventoryRequestAPI.checkQuantityForWoItem(workorderItem.getParentTransactionId(), workorderItem.getQuantity(), workorderItem.getRemainingQuantity())){
                        throw new IllegalArgumentException("Please check the quantity issued");
                    }
                }
                if (workorderItem.getId() > 0) {
//                    if (!eventTypes.contains(EventType.EDIT)) {
//                        eventTypes.add(EventType.EDIT);
//                    }
                    V3WorkorderItemContext wItem  = V3RecordAPI.getRecord(workorderItemsModule.getName(),workorderItem.getId(),V3WorkorderItemContext.class);
                    if (wItem != null) {
                        V3PurchasedItemContext purchasedItem  = V3RecordAPI.getRecord(purchasedItemModule.getName(),wItem.getPurchasedItem().getId(),V3PurchasedItemContext.class);

                        if (purchasedItem != null) {
                            double q = wItem.getQuantity();
                            if ((q + purchasedItem.getCurrentQuantity() < workorderItem.getQuantity())) {
                                throw new IllegalArgumentException("Insufficient quantity in inventory!");
                            } else {
                                approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                                if (workorderItem.getRequestedLineItem() != null && workorderItem.getRequestedLineItem().getId() > 0) {
                                    approvalState = ApprovalState.APPROVED;
                                }
                                if (itemType.isRotating()) {
                                    wItem = setWorkorderItemObj(purchasedItem, 1, item, parentId, approvalState, wo, workorderItem.getAsset(), workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem);

                                } else {
                                    wItem = setWorkorderItemObj(purchasedItem, workorderItem.getQuantity(), item,
                                            parentId, approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem);
                                }
                                // updatePurchasedItem(purchaseditem);
                                wItem.setId(workorderItem.getId());
                                workorderItemslist.add(wItem);
                                itemToBeAdded.add(wItem);
                            }
                        }
                    }
                }
                else{
//                    if (!eventTypes.contains(EventType.CREATE)) {
//                        eventTypes.add(EventType.CREATE);
//                    }
                    if (workorderItem.getRequestedLineItem() == null && workorderItem.getParentTransactionId() <= 0 && item.getQuantity() < workorderItem.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                    } else {
                        approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                        if (workorderItem.getRequestedLineItem() != null && workorderItem.getRequestedLineItem().getId() > 0) {
                            approvalState = ApprovalState.APPROVED;
                        }
                        if (itemType.isRotating()) {
                            List<Long> assetIds = workorderItem.getAssetIds();
                            List<V3AssetContext> purchasedItem = getAssetsFromId(assetIds, assetModule, assetFields);
                            if (purchasedItem != null) {
                                for (V3AssetContext asset : purchasedItem) {
                                    if (workorderItem.getRequestedLineItem() == null && workorderItem.getParentTransactionId() <= 0 && asset.isUsed()) {
                                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                                    }
                                    V3WorkorderItemContext woItem = new V3WorkorderItemContext();
                                    asset.setIsUsed(true);
                                    woItem = setWorkorderItemObj(null, 1, item, parentId, approvalState, wo, asset, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem);
                                    updatePurchasedItem(asset);
                                    workorderItemslist.add(woItem);
                                    itemToBeAdded.add(woItem);
                                }
                            }
                        } else {
                            List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);

                            if (purchasedItems != null && !purchasedItems.isEmpty()) {
                                V3PurchasedItemContext pItem = purchasedItems.get(0);
                                if (workorderItem.getQuantity() <= pItem.getCurrentQuantity()) {
                                    V3WorkorderItemContext woItem = new V3WorkorderItemContext();
                                    woItem = setWorkorderItemObj(pItem, workorderItem.getQuantity(), item, parentId,
                                            approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem);
                                    workorderItemslist.add(woItem);
                                    itemToBeAdded.add(woItem);
                                } else {
                                    double requiredQuantity = workorderItem.getQuantity();
                                    for (V3PurchasedItemContext purchaseitem : purchasedItems) {
                                        V3WorkorderItemContext woItem = new V3WorkorderItemContext();
                                        double quantityUsedForTheCost = 0;
                                        if (requiredQuantity <= purchaseitem.getCurrentQuantity()) {
                                            quantityUsedForTheCost = requiredQuantity;
                                        } else {
                                            quantityUsedForTheCost = purchaseitem.getCurrentQuantity();
                                        }
                                        woItem = setWorkorderItemObj(purchaseitem, quantityUsedForTheCost, item,
                                                parentId, approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem);
                                        requiredQuantity -= quantityUsedForTheCost;
                                        workorderItemslist.add(woItem);
                                        itemToBeAdded.add(woItem);
                                        if (requiredQuantity <= 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(itemToBeAdded)){
                Map<String,Object> data = workOrderItems.get(0).getData();
                itemToBeAdded.get(0).setData(data);
                recordMap.put(moduleName, itemToBeAdded);
            }

            if(CollectionUtils.isNotEmpty(workorderItemslist)) {
                List<Long> recordIds = new ArrayList<>();
                for(V3WorkorderItemContext ws : workorderItemslist){
                    recordIds.add(ws.getId());
                }
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WORKORDER_ITEMS);
            }
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
            context.put(FacilioConstants.ContextNames.ITEM_ID, workOrderItems.get(0).getItem().getId());
            context.put(FacilioConstants.ContextNames.ITEM_IDS,
                    Collections.singletonList(workOrderItems.get(0).getItem().getId()));
            context.put(FacilioConstants.ContextNames.WO_ITEMS_LIST, workorderItemslist);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItemslist);
            context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);
            context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
            context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypesId));
        }

        return false;
    }
    private V3WorkorderItemContext setWorkorderItemObj(V3PurchasedItemContext purchasedItem, double quantity,
                                                     V3ItemContext item, long parentId, ApprovalState approvalState, V3WorkOrderContext wo, V3AssetContext asset, V3InventoryRequestLineItemContext lineItem, long parentTransactionId, Context context, V3WorkorderItemContext workOrderItem) throws Exception{
        V3WorkorderItemContext woItem = new V3WorkorderItemContext();
        woItem.setTransactionType(TransactionType.WORKORDER);
        woItem.setIsReturnable(false);
        double costOccured = 0;
        Double unitPrice = null;
        if(workOrderItem.getInventoryReservation()!=null){
            woItem.setInventoryReservation(workOrderItem.getInventoryReservation());
        }
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
            if (purchasedItem.getUnitcost() >= 0) {
                costOccured = purchasedItem.getUnitcost() * quantity;
                unitPrice = purchasedItem.getUnitcost();
            }
        }
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setQuantity(quantity);
        woItem.setTransactionState(TransactionState.USE);
        if(parentTransactionId != -1) {
            woItem.setParentTransactionId(parentTransactionId);
        }

        if(lineItem != null) {
            woItem.setRequestedLineItem(lineItem);
            woItem.setParentTransactionId(ItemsApi.getItemTransactionsForRequestedLineItem(lineItem.getId()).getId());
        }



        woItem.setItem(item);
        woItem.setItemType(item.getItemType());
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(parentId);
        woItem.setApprovedState(approvalState);
        if (asset != null) {
            woItem.setAsset(asset);
            if(asset.getUnitPrice() != null) {
                costOccured = asset.getUnitPrice() * quantity;
                unitPrice = asset.getUnitPrice();
            }
        }
        woItem.setRemainingQuantity(quantity);

        woItem.setCost(costOccured);
        woItem.setUnitPrice(unitPrice);
        if (wo != null) {
            woItem.setWorkorder(wo);
            if (wo.getAssignedTo() != null) {
                woItem.setIssuedTo(wo.getAssignedTo());
            }
        }
        JSONObject newinfo = new JSONObject();

        if (item.getItemType() != null) {
            V3ItemTypesContext itemType = V3ItemsApi.getItemTypes(item.getItemType().getId());
            if(itemType != null && itemType.isRotating() && asset != null && woItem.getTransactionStateEnum() == TransactionState.USE) {
                if(woItem.getIssuedTo() != null) {
                    asset.setLastIssuedToUser(woItem.getIssuedTo());
                }
                if(woItem.getWorkorder() != null) {
                    asset.setLastIssuedToWo(woItem.getWorkorder().getId());
                }
                asset.setLastIssuedTime(System.currentTimeMillis());
                AssetsAPI.updateAssetV3(asset, asset.getId());

                if(woItem.getTransactionTypeEnum() == TransactionType.WORKORDER) {
                    newinfo.put("woId", woItem.getParentId());
                }
                CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.USE, newinfo,
                        (FacilioContext) context);
            }
        }
        return woItem;
    }

    public static V3ItemContext getItem(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> lookUpfields = new ArrayList<>();
        LookupFieldMeta storeRoomField = new LookupFieldMeta((LookupField) fieldMap.get("storeRoom"));
        MultiLookupField servingSitesField = (MultiLookupField) modBean.getField("servingsites", FacilioConstants.ContextNames.STORE_ROOM);
        storeRoomField.addChildSupplement(servingSitesField);
        lookUpfields.add(storeRoomField);
        lookUpfields.add((LookupField) fieldMap.get("itemType"));

        List<V3ItemContext> inventories = V3RecordAPI.getRecordsListWithSupplements(module.getName(),Collections.singletonList(id),V3ItemContext.class,lookUpfields);

        if (inventories != null && !inventories.isEmpty()) {
            return inventories.get(0);
        }
        return null;
    }

    public static V3ItemTypesContext getItemType(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

        V3ItemTypesContext itemType  = V3RecordAPI.getRecord(itemTypesModule.getName(),id,V3ItemTypesContext.class);
        if (itemType != null) {
            return itemType;
        }
        return null;
    }

    private void updatePurchasedItem(V3AssetContext asset) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        V3RecordAPI.updateRecord(asset, module, fields);

        System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

    }

    public static List<V3AssetContext> getAssetsFromId(List<Long> id, FacilioModule module, List<FacilioField> fields)
            throws Exception {
        List<V3AssetContext> assets  = V3RecordAPI.getRecordsList(module.getName(),id,V3AssetContext.class);
        if (assets != null && !assets.isEmpty()) {
            return assets;
        }
        return null;
    }

}
