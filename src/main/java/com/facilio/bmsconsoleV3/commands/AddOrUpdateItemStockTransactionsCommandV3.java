package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateItemStockTransactionsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<PurchasedItemContext> purchasedItems = (List<PurchasedItemContext>) context
                .get(FacilioConstants.ContextNames.PURCHASED_ITEM);
        ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        if (purchasedItems != null && !purchasedItems.isEmpty()) {
            Boolean isBulkItemAdd = (Boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ITEM_ADD);

            List<ItemTransactionsContext> inventoryTransaction = new ArrayList<>();
            for (PurchasedItemContext ic : purchasedItems) {
                ItemContext item = ItemsApi.getItems(ic.getItem().getId());
                ItemTransactionsContext transaction = new ItemTransactionsContext();
                if(shipment == null) {
                    transaction.setTransactionType(TransactionType.STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                }
                else {
                    transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                    transaction.setShipment(shipment.getId());
                }
                transaction.setPurchasedItem(ic);
                transaction.setItem(ic.getItem());
                transaction.setStoreRoom(item.getStoreRoom());
                transaction.setItemType(item.getItemType());
                transaction.setQuantity(ic.getQuantity());
                transaction.setParentId(ic.getId());
                transaction.setIsReturnable(false);
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);

                // if bulk insertion add stock transaction entry
                if (isBulkItemAdd != null && isBulkItemAdd) {
                    inventoryTransaction.add(transaction);
                } else {
                    SelectRecordsBuilder<ItemTransactionsContext> transactionsselectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
                            .select(fields).table(module.getTableName()).moduleName(module.getName())
                            .beanClass(ItemTransactionsContext.class)
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
                                    String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.IS))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("purchasedItem"),
                                    String.valueOf(ic.getId()), PickListOperators.IS));
                    List<ItemTransactionsContext> transactions = transactionsselectBuilder.get();
                    if (transactions != null && !transactions.isEmpty()) {
                        ItemTransactionsContext it = transactions.get(0);
                        it.setQuantity(ic.getQuantity());
                        UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
                                .module(module).fields(modBean.getAllFields(module.getName()))
                                .andCondition(CriteriaAPI.getIdCondition(it.getId(), module));
                        updateBuilder.update(it);
                    } else {
                        inventoryTransaction.add(transaction);
                    }
                }
            }

            InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
                    .module(module).fields(fields).addRecords(inventoryTransaction);
            readingBuilder.save();
        }
        else  {
            V3ItemContext item = (V3ItemContext) context.get(FacilioConstants.ContextNames.ITEM);
            V3AssetContext asset = (V3AssetContext) context.get(FacilioConstants.ContextNames.ROTATING_ASSET);
            if (item != null) {
                ItemContext items = ItemsApi.getItems(item.getId());
                double q = items.getQuantity() >= 0 ? items.getQuantity() : 0;
                q+=1;
                items.setQuantity(q);
                V3ItemTransactionsContext transaction = new V3ItemTransactionsContext();
                transaction.setIsReturnable(false);

                if(shipment == null) {
                    transaction.setTransactionType(TransactionType.STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                }
                else {
                    transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
                    transaction.setTransactionState(TransactionState.ADDITION.getValue());
                    transaction.setShipment(shipment.getId());
                }
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                transaction.setItem(items);
                transaction.setStoreRoom(items.getStoreRoom());
                transaction.setItemType(items.getItemType());
                transaction.setQuantity(1);
                transaction.setAsset(asset);
                transaction.setParentId(asset.getId());
                updateItemQty(items);
                InsertRecordBuilder<V3ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<V3ItemTransactionsContext>()
                        .module(module).fields(fields).addRecord(transaction);
                readingBuilder.save();

                context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(items.getItemType().getId()));
            }
        }
        return false;
    }

    private void updateItemQty (ItemContext item) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);

        UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
                .module(module).fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
        updateBuilder.update(item);
    }

}