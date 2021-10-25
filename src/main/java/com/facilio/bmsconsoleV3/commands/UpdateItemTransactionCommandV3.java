package com.facilio.bmsconsoleV3.commands;

import java.util.*;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestPurchasedItems;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.util.ItemsApi;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

public class UpdateItemTransactionCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
       String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequests = recordMap.get(moduleName);
        if(!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID)) && transferRequests.get(0).getData().get("isStaged").equals(true)&&transferRequests.get(0).getData().get("isCompleted").equals(false) && transferRequests.get(0).getData().get("isShipped").equals(false)){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            List<FacilioField> itemTransactionsFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
            List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
            FacilioModule transferRequestpurchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS);
            List<FacilioField> transferRequestpurchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS);

            List<ItemTransactionsContext> itemTransactiosnToBeAdded = new ArrayList<>();
            List<V3TransferRequestPurchasedItems> transferRequestPurchasedItems = new ArrayList<>();

            long itemId = (long) context.get(FacilioConstants.ContextNames.ITEM_ID);
            long itemTypeId = (long) context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID);
            ItemTypesContext itemType = ItemsApi.getItemTypes(itemTypeId);
            double quantityTransferred = (double) context.get(FacilioConstants.ContextNames.TOTAL_QUANTITY);
            long storeroomId = (long)context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
            StoreRoomContext storeRoom = StoreroomApi.getStoreRoom(storeroomId);
            String storeRoomName = storeRoom.getName();
            if (CollectionUtils.isNotEmpty(transferRequests)) {
                    ItemContext item = ItemsApi.getItems(itemId);

                        List<PurchasedItemContext> purchasedItem = new ArrayList<>();

                        if (item.getCostTypeEnum() == null || item.getCostType() <= 0
                                || item.getCostTypeEnum() == CostType.FIFO) {
                            purchasedItem = getPurchasedItemList(item.getId(), " asc", purchasedItemModule,
                                    purchasedItemFields);
                        } else if (item.getCostTypeEnum() == CostType.LIFO) {
                            purchasedItem = getPurchasedItemList(item.getId(), " desc", purchasedItemModule,
                                    purchasedItemFields);
                        }

                        if (purchasedItem != null && !purchasedItem.isEmpty()) {
                            PurchasedItemContext pItem = purchasedItem.get(0);
                            double requiredQuantity = -(quantityTransferred);
                            if (pItem.getCurrentQuantity() >= quantityTransferred) {
                                double newQuantity =pItem.getCurrentQuantity()-quantityTransferred;
                                V3TransferRequestPurchasedItems transferRequestPurchasedItem = setTransferRequestPurchasedItem(item,transferRequests.get(0),pItem.getUnitcost(),quantityTransferred);
                                ItemTransactionsContext woItem = setWorkorderItemObj(pItem, quantityTransferred, item, itemType,newQuantity,purchasedItemFields,purchasedItemModule);
                                itemTransactiosnToBeAdded.add(woItem);
                                transferRequestPurchasedItems.add(transferRequestPurchasedItem);
                            } else {
                                for (PurchasedItemContext purchaseitem : purchasedItem) {
                                    ItemTransactionsContext woItem = new ItemTransactionsContext();
                                    double quantityUsedForTheCost = 0;
                                    if (purchaseitem.getCurrentQuantity() + requiredQuantity >= 0) {
                                        quantityUsedForTheCost = requiredQuantity;
                                    } else {
                                        quantityUsedForTheCost = -(purchaseitem.getCurrentQuantity());
                                    }
                                    double newQuantity =purchaseitem.getCurrentQuantity()+quantityUsedForTheCost;
                                    V3TransferRequestPurchasedItems transferRequestPurchasedItem = setTransferRequestPurchasedItem(item,transferRequests.get(0),purchaseitem.getUnitcost(),-(quantityUsedForTheCost));
                                    woItem = setWorkorderItemObj(purchaseitem, -(quantityUsedForTheCost), item, itemType, newQuantity, purchasedItemFields, purchasedItemModule);
                                    itemTransactiosnToBeAdded.add(woItem);
                                    transferRequestPurchasedItems.add(transferRequestPurchasedItem);
                                    requiredQuantity -= quantityUsedForTheCost;
                                    if (requiredQuantity == 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
                            .module(itemTransactionsModule).fields(itemTransactionsFields).addRecords(itemTransactiosnToBeAdded);
                    readingBuilder.save();
                InsertRecordBuilder<V3TransferRequestPurchasedItems> insertRecordBuilder = new InsertRecordBuilder<V3TransferRequestPurchasedItems>()
                        .module(transferRequestpurchasedItemModule).fields(transferRequestpurchasedItemFields).addRecords(transferRequestPurchasedItems);
                insertRecordBuilder.save();

            }
        }
        return false;
    }

   private ItemTransactionsContext setWorkorderItemObj(PurchasedItemContext purchasedItem, double quantity,
                                                        ItemContext item, ItemTypesContext itemTypes,double newQuantity,List<FacilioField> fields,FacilioModule module) throws Exception {
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("currentQuantity"));

        Map<String, Object> map = new HashMap<>();
        map.put("currentQuantity", newQuantity);

        //update total quantity in fromStore in Tool Table
        UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
        updateBuilder.updateViaMap(map);
        ItemTransactionsContext woItem = new ItemTransactionsContext();
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
        woItem.setParentId(purchasedItem.getId());
        woItem.setApprovedState(1);
        woItem.setRemainingQuantity(0);

        return woItem;
    }
    private V3TransferRequestPurchasedItems setTransferRequestPurchasedItem(ItemContext item,V3TransferRequestContext transferRequest,double unitPrice,double quantityTransferred){
        V3TransferRequestPurchasedItems transferRequestPurchasedItem = new V3TransferRequestPurchasedItems();
        transferRequestPurchasedItem.setTransferRequest(transferRequest);
        transferRequestPurchasedItem.setItem(item);
        transferRequestPurchasedItem.setQuantityTransferred(quantityTransferred);
        transferRequestPurchasedItem.setUnitPrice(unitPrice);
        return transferRequestPurchasedItem;
    }
    public static List<PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
                                                                  List<FacilioField> fields) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(PurchasedItemContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
                        NumberOperators.GREATER_THAN))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

        List<PurchasedItemContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }
}
