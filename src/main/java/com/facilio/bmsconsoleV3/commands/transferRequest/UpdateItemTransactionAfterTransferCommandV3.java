package com.facilio.bmsconsoleV3.commands.transferRequest;

import java.util.*;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
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
                if (!Objects.isNull(context.get(FacilioConstants.ContextNames.ITEM_TYPES)) && transferRequestContexts.get(0).getIsStaged() && transferRequestContexts.get(0).getIsCompleted()) {
                    Long storeRoomId = transferRequestContexts.get(0).getTransferToStore().getId();
                    List<V3TransferRequestLineItemContext> itemTypesList = (List<V3TransferRequestLineItemContext>) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
                    for(V3TransferRequestLineItemContext itemTypeLineItem : itemTypesList){
                    Long itemTypeId = itemTypeLineItem.getItemType().getId();
                    V3ItemContext item = V3ItemsApi.getItemsForTypeAndStore(storeRoomId, itemTypeId);
                    Long itemId = item.getId();
                    Long lastPurchasedDate = null;
                    double lastPurchasedPrice = 0;
                    List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
                    List<V3ItemTransactionsContext> itemTransactionsToBeAdded = new ArrayList<>();

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String purchasedItemModuleName = FacilioConstants.ContextNames.TRANSFER_REQUEST_PURCHASED_ITEMS;
                    FacilioModule module = modBean.getModule(purchasedItemModuleName);
                    List<FacilioField> fields = modBean.getAllFields(purchasedItemModuleName);
                    SelectRecordsBuilder<V3TransferRequestPurchasedItems> selectRecordsBuilder = new SelectRecordsBuilder<V3TransferRequestPurchasedItems>()
                            .module(module)
                            .beanClass(V3TransferRequestPurchasedItems.class)
                            .select(fields)
                            .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(transferRequestContexts.get(0).getId()), NumberOperators.EQUALS));
                    List<V3TransferRequestPurchasedItems> records = selectRecordsBuilder.get();
                    for (V3TransferRequestPurchasedItems record : records) {
                        V3PurchasedItemContext purchasedItem = setPurchasedItem(record, item);
                        lastPurchasedDate = purchasedItem.getCostDate();
                        lastPurchasedPrice = purchasedItem.getQuantity() * purchasedItem.getUnitcost();
                        purchasedItems.add(purchasedItem);
                        //Item Transactions
                        V3ItemTransactionsContext woItem = setItemTransaction(record, purchasedItem, item,transferRequestContexts.get(0).getId());
                        itemTransactionsToBeAdded.add(woItem);
                    }
                    String itemModuleName = FacilioConstants.ContextNames.PURCHASED_ITEM;
                    module = modBean.getModule(itemModuleName);
                    fields = modBean.getAllFields(itemModuleName);
                    InsertRecordBuilder<V3PurchasedItemContext> readingBuilder = new InsertRecordBuilder<V3PurchasedItemContext>()
                            .module(module).fields(fields).addRecords(purchasedItems);
                    readingBuilder.save();
                    //Item Transaction Insertion
                    FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
                    List<FacilioField> itemTransactionsFields = modBean
                            .getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
                    InsertRecordBuilder<V3ItemTransactionsContext> insertRecordBuilder = new InsertRecordBuilder<V3ItemTransactionsContext>()
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

                    UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                            .module(module).fields(updatedFields)
                            .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                    updateBuilder.updateViaMap(map);
                }
                }
        }
        return false;
    }
      private V3PurchasedItemContext setPurchasedItem(V3TransferRequestPurchasedItems record,V3ItemContext item){
        V3PurchasedItemContext purchasedItem= new V3PurchasedItemContext();
        purchasedItem.setQuantity(record.getQuantity());
        purchasedItem.setCurrentQuantity(record.getQuantity());
        purchasedItem.setItem(item);
        purchasedItem.setItemType(item.getItemType());
        purchasedItem.setUnitcost(record.getUnitPrice());
        purchasedItem.setSysModifiedTime(System.currentTimeMillis());
        purchasedItem.setSysCreatedTime(System.currentTimeMillis());
        purchasedItem.setCostDate(System.currentTimeMillis());

        return purchasedItem;
    }
    private V3ItemTransactionsContext setItemTransaction(V3TransferRequestPurchasedItems record,V3PurchasedItemContext purchasedItem,V3ItemContext item,Long id){
        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
        woItem.setTransactionState(TransactionState.TRANSFERRED_TO);
        woItem.setIsReturnable(false);
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
        }
        woItem.setQuantity(record.getQuantity());
        woItem.setTransactionType(TransactionType.STOCK.getValue());
        woItem.setItem(item);
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setItemType(item.getItemType());
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(id);
        woItem.setApprovedState(1);
        woItem.setRemainingQuantity(0.0);
        return woItem;
    }
}

