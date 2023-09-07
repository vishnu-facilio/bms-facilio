package com.facilio.fsm.commands.actuals;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderCostContext;
import com.facilio.fsm.context.ServiceOrderItemsContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
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
import static com.facilio.bmsconsoleV3.util.V3ItemsApi.getItem;
import static com.facilio.bmsconsoleV3.util.V3ItemsApi.getItemWithServingSites;

public class SetServiceOrderItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderItemsContext> serviceOrderItems = recordMap.get(moduleName);
        List<V3ItemTransactionsContext> itemTransactions = new ArrayList<>();
        List<ServiceOrderItemsContext> serviceOrderItemsContexts = new ArrayList<>();
        List<ServiceOrderContext> serviceOrders = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(serviceOrderItems)){
            for(ServiceOrderItemsContext serviceOrderItem : serviceOrderItems){
                V3ItemTransactionsContext itemTransaction = new V3ItemTransactionsContext();
                if(serviceOrderItem.getServiceOrder()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order cannot be empty");
                }

                if(serviceOrderItem.getQuantity()== null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                if(serviceOrderItem.getItem()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item cannot be empty");
                }
                serviceOrders.add(serviceOrderItem.getServiceOrder());

                ServiceOrderContext serviceOrderRecord = getServiceOrder(serviceOrderItem.getServiceOrder().getId());
                if(!isStoreroomServingServiceOderSite(serviceOrderItem.getItem(),serviceOrderRecord)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom does not serve the selected Service Order's site");
                }
                V3ItemContext item = getItem(serviceOrderItem.getItem().getId());
                if(item.getQuantity() < serviceOrderItem.getQuantity()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Insufficient quantity in Storeroom");
                }
                itemTransaction = setDefaultItemTransaction(itemTransaction,serviceOrderItem,item);
                serviceOrderItem = setDefaultServiceOrderItem(serviceOrderItem,item);

                //rotating item
                if (item.getItemType().isRotating()) {
                    if(serviceOrderItem.getRotatingAsset()==null){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating Asset cannot be empty");
                    }
                    if(V3AssetAPI.isAssetMaintainable(serviceOrderItem.getRotatingAsset())){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please close all open Work Orders for the Asset before issuing");
                    }

                    if(isServiceOrderAssetInStoreroom(serviceOrderItem.getServiceOrder())){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating item cannot be issued to a storeroom");
                    }
                    V3AssetContext rotatingAssetRecord = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,serviceOrderItem.getRotatingAsset().getId(),V3AssetContext.class);
                    if(rotatingAssetRecord!=null){
                        if(rotatingAssetRecord.isUsed()){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Insufficient quantity in Storeroom");
                        }
                        rotatingAssetRecord = setRotatingAsset(rotatingAssetRecord,serviceOrderRecord);

                        //history
                        JSONObject newInfo = getAssetHistory(serviceOrderRecord);
                        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, "assetactivity");
                        CommonCommandUtil.addActivityToContext(rotatingAssetRecord.getId(), -1, AssetActivityType.USE_IN_SO, newInfo,(FacilioContext) context);
                        //update asset
                        V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.ASSET, rotatingAssetRecord.getId(), FieldUtil.getAsJSON(rotatingAssetRecord), null, null, null, null, null,null,null, null);
                        itemTransaction.setAsset(serviceOrderItem.getRotatingAsset());
                        itemTransaction = setItemTransaction(itemTransaction,null,1.0);
                        if(item.getIssuanceCost()!=null) {
                            serviceOrderItem = setServiceOrderItem(serviceOrderItem,item.getIssuanceCost(), 1.0);
                        }
                        itemTransactions.add(itemTransaction);
                        serviceOrderItemsContexts.add(serviceOrderItem);
                    }
                }
                //non rotating item
                else{
                    List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);

                    if (CollectionUtils.isNotEmpty(purchasedItems)) {
                        V3PurchasedItemContext pItem = purchasedItems.get(0);
                        //actual created from one purchased Item
                        if (serviceOrderItem.getQuantity() <= pItem.getCurrentQuantity()) {
                            itemTransaction = setItemTransaction(itemTransaction,pItem,serviceOrderItem.getQuantity());
                            if (pItem.getUnitcost() >= 0) {
                                serviceOrderItem = setServiceOrderItem(serviceOrderItem,pItem.getUnitcost(), serviceOrderItem.getQuantity());
                            }
                            itemTransactions.add(itemTransaction);
                            serviceOrderItemsContexts.add(serviceOrderItem);
                        }
                        //actuals created from multiple purchased Items
                        else {
                            List<V3ItemTransactionsContext> itemTransactionsList = new ArrayList<>();
                            List<ServiceOrderItemsContext> serviceOrderItemsList = new ArrayList<>();
                            double requiredQuantity = serviceOrderItem.getQuantity();
                            for (V3PurchasedItemContext purchasedItem : purchasedItems) {
                                ServiceOrderItemsContext serviceOrderItemContext = FieldUtil.cloneBean(serviceOrderItem,ServiceOrderItemsContext.class);
                                V3ItemTransactionsContext itemTransactionsContext = FieldUtil.cloneBean(itemTransaction,V3ItemTransactionsContext.class);
                                Double quantityUsedForTheCost;
                                if (requiredQuantity <= purchasedItem.getCurrentQuantity()) {
                                    quantityUsedForTheCost = requiredQuantity;
                                } else {
                                    quantityUsedForTheCost = purchasedItem.getCurrentQuantity();
                                }

                                itemTransactionsList.add(setItemTransaction(itemTransactionsContext,purchasedItem,quantityUsedForTheCost));
                                serviceOrderItemContext.setQuantity(quantityUsedForTheCost);
                                if (purchasedItem.getUnitcost() >= 0) {
                                    serviceOrderItemsList.add(setServiceOrderItem(serviceOrderItemContext,purchasedItem.getUnitcost(), quantityUsedForTheCost));
                                }
                                requiredQuantity -= quantityUsedForTheCost;
                                if (requiredQuantity <= 0) {
                                    break;
                                }
                            }
                            itemTransactions.addAll(itemTransactionsList);
                            serviceOrderItemsContexts.addAll(serviceOrderItemsList);
                        }
                    }
                }
            }
            //create item transactions
            V3Util.createRecordList(modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS),FieldUtil.getAsMapList(itemTransactions,V3ItemTransactionsContext.class),null,null);
            recordMap.put(moduleName,serviceOrderItemsContexts);

            List<Long> itemIds = serviceOrderItems.stream().map(serviceOrderItem -> serviceOrderItem.getItem().getId()).collect(Collectors.toList());
            List<Long> itemTypeIds = serviceOrderItems.stream().map(serviceOrderItem -> serviceOrderItem.getItemType().getId()).collect(Collectors.toList());

            context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS, serviceOrderItemsContexts);
            context.put(FacilioConstants.ContextNames.ITEM_IDS, itemIds);
            context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypeIds);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE,ServiceOrderCostContext.InventoryCostType.ITEMS);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.ACTUALS);
        }
        return false;
    }
    private Boolean isStoreroomServingServiceOderSite(V3ItemContext item, ServiceOrderContext serviceOrderRecord) throws Exception {
        V3ItemContext itemRecord = getItemWithServingSites(item.getId());
        List<Long> servingSiteIds = new ArrayList<>();
        if(itemRecord.getStoreRoom()!=null && itemRecord.getStoreRoom().getServingsites()!=null){
            servingSiteIds = itemRecord.getStoreRoom().getServingsites().stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());
        }
        return serviceOrderRecord.getSite()!=null && serviceOrderRecord.getSite().getId()>0 && servingSiteIds.contains(serviceOrderRecord.getSite().getId());
    }
    private Boolean isServiceOrderAssetInStoreroom(ServiceOrderContext serviceOrder) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> lookUpFields = new ArrayList<>();
        lookUpFields.add((LookupField) fieldMap.get("asset"));
        List<ServiceOrderContext> serviceOrderRecords =  V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,Collections.singletonList(serviceOrder.getId()),ServiceOrderContext.class,lookUpFields);
        ServiceOrderContext serviceOrderRecord =serviceOrderRecords.get(0);

         return isAssetInStoreRoom(serviceOrderRecord.getAsset());

    }
    private ServiceOrderContext getServiceOrder(Long serviceOrderId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Collection<SupplementRecord> lookUpFields = new ArrayList<>();
        lookUpFields.add((LookupField) fieldMap.get("space"));
        lookUpFields.add((LookupField) fieldMap.get("site"));
        return V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,Collections.singletonList(serviceOrderId),ServiceOrderContext.class,lookUpFields).get(0);
    }
    private V3ItemTransactionsContext setDefaultItemTransaction(V3ItemTransactionsContext itemTransaction,ServiceOrderItemsContext serviceOrderItem,V3ItemContext item){
        itemTransaction.setServiceOrder(serviceOrderItem.getServiceOrder());
        itemTransaction.setServiceOrderItem(serviceOrderItem);
        itemTransaction.setTransactionType(TransactionType.WORKORDER);
        itemTransaction.setTransactionState(TransactionState.USE);
        itemTransaction.setIsReturnable(false);
        itemTransaction.setParentId(serviceOrderItem.getServiceOrder().getId());
        itemTransaction.setItem(item);
        itemTransaction.setItemType(item.getItemType());
        itemTransaction.setStoreRoom(item.getStoreRoom());
        return itemTransaction;
    }
    private ServiceOrderItemsContext setDefaultServiceOrderItem(ServiceOrderItemsContext serviceOrderItem,V3ItemContext item){
        serviceOrderItem.setStoreRoom(item.getStoreRoom());
        serviceOrderItem.setItemType(item.getItemType());
        return serviceOrderItem;
    }
    private V3AssetContext setRotatingAsset(V3AssetContext rotatingAssetRecord,ServiceOrderContext serviceOrderRecord) throws RESTException {
        rotatingAssetRecord.setIsUsed(true);
        rotatingAssetRecord.setStoreRoom(null);
        rotatingAssetRecord.setRotatingItem(null);
        //set asset site
        if(serviceOrderRecord.getSite()==null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order's site cannot be empty");
        }else{
            rotatingAssetRecord.setSiteId(serviceOrderRecord.getSite().getId());
        }
        //set asset space
        V3BaseSpaceContext assetSpace = new V3BaseSpaceContext();
        if(serviceOrderRecord.getSpace()!=null && serviceOrderRecord.getSpace().getId()>0){
            assetSpace.setId(serviceOrderRecord.getSpace().getId());
        }else{
            assetSpace.setId(serviceOrderRecord.getSite().getId());
        }
        rotatingAssetRecord.setSpace(assetSpace);
        rotatingAssetRecord.setCanUpdateRotatingAsset(true);
        return  rotatingAssetRecord;
    }
    private JSONObject getAssetHistory(ServiceOrderContext serviceOrderRecord){
        JSONObject newInfo = new JSONObject();
        newInfo.put("soId", serviceOrderRecord.getId());
        newInfo.put("site", serviceOrderRecord.getSite().getName());
        if(serviceOrderRecord.getSpace()!=null){
            newInfo.put("space",serviceOrderRecord.getSpace().getName());
        }
        return newInfo;
    }
    private V3ItemTransactionsContext setItemTransaction(V3ItemTransactionsContext itemTransaction,V3PurchasedItemContext purchasedItem,Double quantity){
        if(purchasedItem!=null){
            itemTransaction.setPurchasedItem(purchasedItem);
        }
        itemTransaction.setQuantity(quantity);
        itemTransaction.setRemainingQuantity(quantity);
        return itemTransaction;
    }
    private ServiceOrderItemsContext setServiceOrderItem(ServiceOrderItemsContext serviceOrderItem,Double unitPrice,Double quantity){
        serviceOrderItem.setQuantity(quantity);
        serviceOrderItem.setTotalCost(unitPrice * quantity);
        serviceOrderItem.setUnitPrice(unitPrice);
        return serviceOrderItem;
    }
}
