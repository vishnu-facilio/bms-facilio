package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.*;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsoleV3.util.V3AssetAPI.isAssetInStoreRoom;
import static com.facilio.bmsconsoleV3.util.V3ItemsApi.getItemWithServingSites;

public class SetWorkOrderItemsCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkorderItemContext> workOrderItems = recordMap.get(moduleName);

        List<V3WorkorderItemContext> workorderItemslist = new ArrayList<>();
        List<V3WorkorderItemContext> itemToBeAdded = new ArrayList<>();

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

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
                V3ItemContext item = getItemWithServingSites(workorderItem.getItem().getId());
                itemTypesId = item.getItemType().getId();
                V3ItemTypesContext itemType = getItemType(itemTypesId);

                if(workOrderMap.get(parentId)==null){
                     wo = V3WorkOderAPI.getWorkOrder(parentId,Collections.singletonList("resource"));
                    workOrderMap.put(parentId,wo);
                }else{
                    wo = workOrderMap.get(parentId);
                }
               List<Long> servingSiteIds = new ArrayList<>();
                if(item.getStoreRoom()!=null && item.getStoreRoom().getServingsites()!=null){
                    servingSiteIds = item.getStoreRoom().getServingsites().stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
                }
               if(wo.getSiteId()>0 && !servingSiteIds.contains(wo.getSiteId())){
                   throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom does not serve the selected work order's site");
               }
                if (workorderItem.getRequestedLineItem() != null && workorderItem.getRequestedLineItem().getId() > 0) {
                    if(!V3InventoryRequestAPI.checkQuantityForWoItemNeedingApprovalV3(itemType, workorderItem.getRequestedLineItem(), workorderItem.getQuantity())) {
                        throw new IllegalArgumentException("Please check the quantity approved/issued in the request");
                    }
                }
                else if(workorderItem.getParentTransactionId() > 0) {
                    if(workorderItem.getQuantity() > workorderItem.getRemainingQuantity()){
                        throw new IllegalArgumentException("Quantity is greater than the remaining quantity");
                    }
                }
                if (workorderItem.getId() > 0) {
                    V3WorkorderItemContext wItem  = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItem.getId(),V3WorkorderItemContext.class);
                    if (wItem != null) {
                        V3PurchasedItemContext purchasedItem  = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PURCHASED_ITEM,wItem.getPurchasedItem().getId(),V3PurchasedItemContext.class);

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
                                    if(workorderItem.getAsset()==null){
                                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating Asset cannot be empty");
                                    }
                                    wItem = setWorkorderItemObj(purchasedItem, 1, item, parentId, approvalState, wo, workorderItem.getAsset(), workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem, baseCurrency, currencyMap);
                                } else {
                                    wItem = setWorkorderItemObj(purchasedItem, workorderItem.getQuantity(), item,
                                            parentId, approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem, baseCurrency, currencyMap);
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
                    if (workorderItem.getRequestedLineItem() == null && workorderItem.getParentTransactionId() <= 0 && item.getQuantity() < workorderItem.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                    } else {
                        approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                        if (workorderItem.getRequestedLineItem() != null && workorderItem.getRequestedLineItem().getId() > 0) {
                            approvalState = ApprovalState.APPROVED;
                        }
                        if (itemType.isRotating()) {
                            if(workorderItem.getAsset()==null){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating Asset cannot be empty");
                            }
                            if(V3AssetAPI.isAssetMaintainable(workorderItem.getAsset())){
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please close all open Work Orders for the Asset before issuing");
                            }
                            if(wo.getResource()!=null && wo.getResource().getResourceTypeEnum().equals(ResourceContext.ResourceType.ASSET)){
                                  V3AssetContext asset = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,wo.getResource().getId(),V3AssetContext.class);
                                  if (isAssetInStoreRoom(asset)){
                                      throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating item cannot be issued to a storeroom");
                                  }
                            }
                            V3AssetContext asset = workorderItem.getAsset();
                            V3AssetContext assetRecord = getAssetFromId(asset.getId());
                            if (assetRecord != null) {
                                    if (workorderItem.getRequestedLineItem() == null && workorderItem.getParentTransactionId() <= 0 && assetRecord.isUsed()) {
                                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                                    }
                                    V3WorkorderItemContext woItem = new V3WorkorderItemContext();
                                    assetRecord.setIsUsed(true);
                                    assetRecord.setStoreRoom(null);
                                    assetRecord.setRotatingItem(null);
                                    assetRecord.setSiteId(wo.getSiteId());
                                    if(wo.getResource()!=null && wo.getResource().getSpace()!=null){
                                        V3BaseSpaceContext assetLocation = new V3BaseSpaceContext();
                                        assetLocation.setId(wo.getResource().getSpace().getId());
                                        assetRecord.setSpace(assetLocation);
                                    }else{
                                        V3BaseSpaceContext assetSpace = new V3BaseSpaceContext();
                                        assetSpace.setId(wo.getSiteId());
                                        assetRecord.setSpace(assetSpace);
                                    }
                                    assetRecord.setCanUpdateRotatingAsset(true);
                                    woItem = setWorkorderItemObj(null, 1, item, parentId, approvalState, wo, assetRecord, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem, baseCurrency, currencyMap);
                                    updateAsset(assetRecord);
                                    workorderItemslist.add(woItem);
                                    itemToBeAdded.add(woItem);
                            }
                        } else {
                            List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);

                            if (purchasedItems != null && !purchasedItems.isEmpty()) {
                                V3PurchasedItemContext pItem = purchasedItems.get(0);
                                if (workorderItem.getQuantity() <= pItem.getCurrentQuantity()) {
                                    V3WorkorderItemContext woItem = new V3WorkorderItemContext();
                                    woItem = setWorkorderItemObj(pItem, workorderItem.getQuantity(), item, parentId,
                                            approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem, baseCurrency, currencyMap);
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
                                                parentId, approvalState, wo, null, workorderItem.getRequestedLineItem(), parentTransactionId, context, workorderItem, baseCurrency, currencyMap);
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
//            todo - revamp the logic
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
                                                       V3ItemContext item, long parentId, ApprovalState approvalState, V3WorkOrderContext wo, V3AssetContext asset, V3InventoryRequestLineItemContext lineItem, long parentTransactionId, Context context, V3WorkorderItemContext workOrderItem,
                                                       CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) throws Exception{
        V3WorkorderItemContext woItem = new V3WorkorderItemContext();
        woItem.setTransactionType(TransactionType.WORKORDER);
        woItem.setIsReturnable(false);
        double costOccured = 0;
        Double unitPrice = null;
        if(workOrderItem.getInventoryReservation()!=null){
            woItem.setInventoryReservation(workOrderItem.getInventoryReservation());
            User requestedFor = V3InventoryRequestAPI.getUserToIssueFromReservation(workOrderItem.getInventoryReservation());
            woItem.setIssuedTo(requestedFor);
        }
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
            CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(woItem, baseCurrency, currencyMap, purchasedItem.getCurrencyCode(), purchasedItem.getExchangeRate());
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
            if(item.getIssuanceCost()!=null) {
                unitPrice = item.getIssuanceCost();
                costOccured = item.getIssuanceCost() * quantity;
            }
//            if(asset.getUnitPrice() != null) {
//                costOccured = asset.getUnitPrice() * quantity;
//                unitPrice = asset.getUnitPrice();
//            }
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
                    newinfo.put("site", getSiteName(wo.getSiteId()));
                    if(wo.getResource()!=null && wo.getResource().getSpace()!=null){
                        newinfo.put("space", getSpaceName(wo.getResource().getSpace().getId()));
                    }
                }
                context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, "assetactivity");
                CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.USE, newinfo,
                        (FacilioContext) context);
            }
        }
        return woItem;
    }
    private String getSiteName(Long siteId) throws Exception {
        V3SiteContext site = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SITE,siteId, V3SiteContext.class);
        if(site!=null) {
            return site.getName();
        }
        return null;
    }
    private String getSpaceName(Long spaceId) throws Exception {
        V3SpaceContext space = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SPACE,spaceId, V3SpaceContext.class);
        if(space!=null) {
            return space.getName();
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

    private void updateAsset(V3AssetContext asset) throws Exception {
        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.ASSET, asset.getId(), FieldUtil.getAsJSON(asset), null, null, null, null, null,null,null, null,null);
    }

    public static V3AssetContext getAssetFromId(Long id)
            throws Exception {
        V3AssetContext asset  = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,id,V3AssetContext.class);
        if (asset != null) {
            return asset;
        }
        return null;
    }

}
