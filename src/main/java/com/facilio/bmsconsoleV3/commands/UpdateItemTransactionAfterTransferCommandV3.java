package com.facilio.bmsconsoleV3.commands;

import java.util.*;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestPurchasedItems;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
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

public class UpdateItemTransactionAfterTransferCommandV3 extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(transferRequestContexts)) {
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {
                if (!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES)) && transferRequestContext != null && transferRequestContext.getData().get("isCompleted").equals(true)) {
                    Long storeRoomId = transferRequestContext.getTransferToStore().getId();
                    List<V3TransferRequestLineItemContext> itemTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
                    for(V3TransferRequestLineItemContext itemTypeLineItem : itemTypesList){
                    Long itemTypeId = itemTypeLineItem.getItemType().getId();
                    ItemContext item = ItemsApi.getItemsForTypeAndStore(storeRoomId, itemTypeId);
                    Long itemId = item.getId();
                    Long lastPurchasedDate = null;
                    double lastPurchasedPrice = 0;
                    List<PurchasedItemContext> purchasedItems = new ArrayList<>();
                    List<ItemTransactionsContext> itemTransactionsToBeAdded = new ArrayList<>();

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String purchasedItemModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS;
                    FacilioModule module = modBean.getModule(purchasedItemModuleName);
                    List<FacilioField> fields = modBean.getAllFields(purchasedItemModuleName);
                    SelectRecordsBuilder<V3TransferRequestPurchasedItems> selectRecordsBuilder = new SelectRecordsBuilder<V3TransferRequestPurchasedItems>()
                            .module(module)
                            .beanClass(V3TransferRequestPurchasedItems.class)
                            .select(fields)
                            .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(transferRequestContext.getId()), NumberOperators.EQUALS));
                    List<V3TransferRequestPurchasedItems> records = selectRecordsBuilder.get();
                    for (V3TransferRequestPurchasedItems record : records) {
                        PurchasedItemContext purchasedItem = setPurchasedItem(record, item);
                        lastPurchasedDate = purchasedItem.getModifiedTime();
                        lastPurchasedPrice = purchasedItem.getQuantity() * purchasedItem.getUnitcost();
                        purchasedItems.add(purchasedItem);
                        //Item Transactions
                        ItemTransactionsContext woItem = setItemTransaction(record, purchasedItem, item);
                        itemTransactionsToBeAdded.add(woItem);
                    }
                    String itemModuleName = FacilioConstants.ContextNames.PURCHASED_ITEM;
                    module = modBean.getModule(itemModuleName);
                    fields = modBean.getAllFields(itemModuleName);
                    InsertRecordBuilder<PurchasedItemContext> readingBuilder = new InsertRecordBuilder<PurchasedItemContext>()
                            .module(module).fields(fields).addRecords(purchasedItems);
                    readingBuilder.save();
                    //Item Transaction Insertion
                    FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
                    List<FacilioField> itemTransactionsFields = modBean
                            .getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
                    InsertRecordBuilder<ItemTransactionsContext> insertRecordBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
                            .module(itemTransactionsModule).fields(itemTransactionsFields).addRecords(itemTransactionsToBeAdded);
                    insertRecordBuilder.save();
                    //Updating last purchased price and date of item
                    itemModuleName = FacilioConstants.ContextNames.ITEM;
                    module = modBean.getModule(itemModuleName);
                    fields = modBean.getAllFields(itemModuleName);
                    List<FacilioField> updatedFields = new ArrayList<>();
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    updatedFields.add(fieldsMap.get("lastPurchasedDate"));
                    updatedFields.add(fieldsMap.get("lastPurchasedPrice"));

                    Map<String, Object> map = new HashMap<>();
                    map.put("lastPurchasedDate", lastPurchasedDate);
                    map.put("lastPurchasedPrice", lastPurchasedPrice);

                    UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
                            .module(module).fields(updatedFields)
                            .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                    updateBuilder.updateViaMap(map);
                }
                }
            }
        }
        return false;
    }
      private PurchasedItemContext setPurchasedItem(V3TransferRequestPurchasedItems record,ItemContext item){
        PurchasedItemContext purchasedItem= new PurchasedItemContext();
        purchasedItem.setQuantity(record.getQuantityTransferred());
        purchasedItem.setCurrentQuantity(record.getQuantityTransferred());
        purchasedItem.setItem(item);
        purchasedItem.setItemType(item.getItemType());
        purchasedItem.setUnitcost(record.getUnitPrice());
        purchasedItem.setModifiedTime(System.currentTimeMillis());
        purchasedItem.setSysModifiedTime(System.currentTimeMillis());
        purchasedItem.setSysCreatedTime(System.currentTimeMillis());
        purchasedItem.setCostDate(System.currentTimeMillis());
        purchasedItem.setTtime(System.currentTimeMillis());
        return purchasedItem;
    }
    private ItemTransactionsContext setItemTransaction(V3TransferRequestPurchasedItems record,PurchasedItemContext purchasedItem,ItemContext item){
        ItemTransactionsContext woItem = new ItemTransactionsContext();
        woItem.setTransactionState(TransactionState.TRANSFERRED_TO);
        woItem.setIsReturnable(false);
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
        }
        woItem.setQuantity(record.getQuantityTransferred());
        woItem.setTransactionType(TransactionType.STOCK.getValue());
        woItem.setItem(item);
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setItemType(item.getItemType());
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(item.getId());
        woItem.setApprovedState(1);
        woItem.setRemainingQuantity(0);
        return woItem;
    }
}

