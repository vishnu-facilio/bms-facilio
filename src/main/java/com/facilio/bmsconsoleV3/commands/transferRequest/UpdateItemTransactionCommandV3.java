package com.facilio.bmsconsoleV3.commands.transferRequest;

import java.util.*;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3StoreroomApi;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateItemTransactionCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
       String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequests = recordMap.get(moduleName);
        if(!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES)) && transferRequests.get(0).getIsStaged() && !transferRequests.get(0).getIsCompleted() && !transferRequests.get(0).getIsShipped()){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            List<FacilioField> itemTransactionsFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
            List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
            FacilioModule transferRequestpurchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS);
            List<FacilioField> transferRequestpurchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS);
            List<V3TransferRequestLineItemContext> itemTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
            for(V3TransferRequestLineItemContext itemTypeLineItem : itemTypesList){
                List<V3ItemTransactionsContext> itemTransactiosnToBeAdded = new ArrayList<>();
                List<V3TransferRequestPurchasedItems> transferRequestPurchasedItems = new ArrayList<>();


                V3ItemTypesContext itemType = itemTypeLineItem.getItemType();
                double quantityTransferred = itemTypeLineItem.getQuantity();
                long storeroomId = (long)context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
                V3StoreRoomContext storeRoom = V3StoreroomApi.getStoreRoom(storeroomId);
                V3ItemContext item = V3ItemsApi.getItem(itemType,storeRoom);

                List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item,null);

                if (purchasedItems != null && !purchasedItems.isEmpty()) {
                        V3PurchasedItemContext pItem = purchasedItems.get(0);
                        double requiredQuantity = -(quantityTransferred);
                        if (pItem.getCurrentQuantity() >= quantityTransferred) {
                            double newQuantity =pItem.getCurrentQuantity()-quantityTransferred;
                            V3TransferRequestPurchasedItems transferRequestPurchasedItem = setTransferRequestPurchasedItem(item,transferRequests.get(0),pItem.getUnitcost(),quantityTransferred);
                            V3ItemTransactionsContext woItem = setWorkorderItemObj(pItem, quantityTransferred, item, itemType,newQuantity,purchasedItemFields,purchasedItemModule,transferRequests.get(0).getId());
                            itemTransactiosnToBeAdded.add(woItem);
                            transferRequestPurchasedItems.add(transferRequestPurchasedItem);
                        } else {
                            for (V3PurchasedItemContext purchaseitem : purchasedItems) {
                                V3ItemTransactionsContext woItem;
                                double quantityUsedForTheCost = 0;
                                if (purchaseitem.getCurrentQuantity() + requiredQuantity >= 0) {
                                    quantityUsedForTheCost = requiredQuantity;
                                } else {
                                    quantityUsedForTheCost = -(purchaseitem.getCurrentQuantity());
                                }
                                double newQuantity =purchaseitem.getCurrentQuantity()+quantityUsedForTheCost;
                                V3TransferRequestPurchasedItems transferRequestPurchasedItem = setTransferRequestPurchasedItem(item,transferRequests.get(0),purchaseitem.getUnitcost(),-(quantityUsedForTheCost));
                                woItem = setWorkorderItemObj(purchaseitem, -(quantityUsedForTheCost), item, itemType, newQuantity, purchasedItemFields, purchasedItemModule,transferRequests.get(0).getId());
                                itemTransactiosnToBeAdded.add(woItem);
                                transferRequestPurchasedItems.add(transferRequestPurchasedItem);
                                requiredQuantity -= quantityUsedForTheCost;
                                if (requiredQuantity == 0) {
                                    break;
                                }
                            }
                        }
                    }

                    V3Util.createRecordList(itemTransactionsModule, FieldUtil.getAsMapList(itemTransactiosnToBeAdded,V3ItemTransactionsContext.class),null,null);

                    InsertRecordBuilder<V3TransferRequestPurchasedItems> insertRecordBuilder = new InsertRecordBuilder<V3TransferRequestPurchasedItems>()
                            .module(transferRequestpurchasedItemModule).fields(transferRequestpurchasedItemFields).addRecords(transferRequestPurchasedItems);
                    insertRecordBuilder.save();

            }

        }
        return false;
    }

   private V3ItemTransactionsContext setWorkorderItemObj(V3PurchasedItemContext purchasedItem, double quantity,
                                                        V3ItemContext item, V3ItemTypesContext itemTypes,double newQuantity,List<FacilioField> fields,FacilioModule module,Long id) throws Exception {
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("currentQuantity"));

        Map<String, Object> map = new HashMap<>();
        map.put("currentQuantity", newQuantity);

        UpdateRecordBuilder<V3PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<V3PurchasedItemContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
        updateBuilder.updateViaMap(map);
        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
        woItem.setTransactionState(TransactionState.TRANSFERRED_FROM);
        woItem.setIsReturnable(false);
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
        }
        woItem.setQuantity(quantity);
        woItem.setTransactionType(TransactionType.STOCK.getValue());
        woItem.setItem(item);
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setItemType(itemTypes);
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(id);
        woItem.setApprovedState(1);
        woItem.setRemainingQuantity(0.0);

        return woItem;
    }
    private V3TransferRequestPurchasedItems setTransferRequestPurchasedItem(V3ItemContext item,V3TransferRequestContext transferRequest,double unitPrice,double quantityTransferred){
        V3TransferRequestPurchasedItems transferRequestPurchasedItem = new V3TransferRequestPurchasedItems();
        transferRequestPurchasedItem.setTransferRequest(transferRequest);
        transferRequestPurchasedItem.setItem(item);
        transferRequestPurchasedItem.setQuantity(quantityTransferred);
        transferRequestPurchasedItem.setUnitPrice(unitPrice);
        return transferRequestPurchasedItem;
    }
    public static List<V3PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
                                                                  List<FacilioField> fields) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedItemContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
                        NumberOperators.GREATER_THAN))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

        List<V3PurchasedItemContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }
}
