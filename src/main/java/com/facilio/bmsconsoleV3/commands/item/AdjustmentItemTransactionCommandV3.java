package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdjustmentItemTransactionCommandV3  extends FacilioCommand {
    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemTransactionsContext> itemTransactions = recordMap.get(moduleName);

        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);
        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);

        if(CollectionUtils.isNotEmpty(itemTransactions) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("adjustQuantity") && (boolean) bodyParams.get("adjustQuantity")) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            List<FacilioField> itemTransactionsFields = modBean
                    .getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
            FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
            List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);


            List<V3ItemTransactionsContext> itemTransactionsToBeAdded = new ArrayList<>();
            long itemTypeId = -1;
            if (itemTransactions != null && !itemTransactions.isEmpty()) {
                for (V3ItemTransactionsContext itemTransaction : itemTransactions) {
                    V3ItemContext item = V3ItemsApi.getItems(itemTransaction.getItem().getId());
                    itemTypeId = item.getItemType().getId();
                    V3ItemTypesContext itemType = V3ItemsApi.getItemTypes(itemTypeId);
                    if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && itemType.isRotating()) {
                        throw new IllegalArgumentException("Not Applicable for Rotating Items!");
                    } else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
                            && itemType.isRotating()) {
                        throw new IllegalArgumentException("Not Applicable for Rotating Items!");
                    } else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && item.getQuantity() < itemTransaction.getQuantity()) {
                        throw new IllegalArgumentException("Invalid Adjustment Quantity!");
                    } else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
                            && !itemType.isRotating()) {
                        if (itemTransaction.getQuantity() <= item.getQuantity()) {
                            List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);
                            if (purchasedItems != null && !purchasedItems.isEmpty()) {
                                V3PurchasedItemContext pItem = purchasedItems.get(0);
                                double requiredQuantity = -(itemTransaction.getQuantity());
                                if (requiredQuantity + pItem.getCurrentQuantity() >= 0) {
                                    V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                                    woItem = setWorkorderItemObj(pItem, itemTransaction.getQuantity(), item,
                                            itemTransaction, itemType, baseCurrency, currencyMap);
                                    itemTransactionsToBeAdded.add(woItem);
                                } else {
                                    for (V3PurchasedItemContext purchaseitem : purchasedItems) {
                                        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                                        double quantityUsedForTheCost = 0;
                                        if (purchaseitem.getCurrentQuantity() + requiredQuantity >= 0) {
                                            quantityUsedForTheCost = requiredQuantity;
                                        } else {
                                            quantityUsedForTheCost = -(purchaseitem.getCurrentQuantity());
                                        }
                                        woItem = setWorkorderItemObj(purchaseitem, -(quantityUsedForTheCost), item,
                                                itemTransaction, itemType, baseCurrency, currencyMap);
                                        requiredQuantity -= quantityUsedForTheCost;
                                        itemTransactionsToBeAdded.add(woItem);
                                        if (requiredQuantity == 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    } else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
                            && !itemType.isRotating()) {
                        V3PurchasedItemContext pi = itemTransaction.getPurchasedItem();
                        pi.setItem(item);
                        pi.setItemType(itemType);
                        pi.setCostDate(System.currentTimeMillis());
                        itemType.setLastPurchasedDate(pi.getCostDate());
                        item.setLastPurchasedDate(pi.getCostDate());
                        itemType.setLastPurchasedPrice(pi.getUnitcost());
                        item.setLastPurchasedPrice(pi.getUnitcost());
                        addPurchasedItem(purchasedItemModule, purchasedItemFields, pi);
                        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                        woItem = setWorkorderItemObj(itemTransaction.getPurchasedItem(), itemTransaction.getQuantity(), item,
                                itemTransaction, itemType, baseCurrency, currencyMap);
                        itemTransactionsToBeAdded.add(woItem);
                    }

                }

                recordMap.put(moduleName, itemTransactionsToBeAdded);

                context.put(FacilioConstants.ContextNames.PARENT_ID, itemTransactions.get(0).getParentId());
                context.put(FacilioConstants.ContextNames.ITEM_ID, itemTransactions.get(0).getItem().getId());
                context.put(FacilioConstants.ContextNames.ITEM_IDS,
                        Collections.singletonList(itemTransactions.get(0).getItem().getId()));
                context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactionsToBeAdded);
                context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypeId);
                context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypeId));
                context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
                        itemTransactions.get(0).getTransactionStateEnum());
            }
        }
        return false;
    }

    private V3ItemTransactionsContext setWorkorderItemObj(V3PurchasedItemContext purchasedItem, double quantity,
                                                          V3ItemContext item, V3ItemTransactionsContext itemTransactions, V3ItemTypesContext itemTypes, CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap){
        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
        woItem.setTransactionState(itemTransactions.getTransactionStateEnum());
        woItem.setIsReturnable(false);
        if (purchasedItem != null) {
            CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(purchasedItem, baseCurrency, currencyMap,
                    purchasedItem.getCurrencyCode(), purchasedItem.getExchangeRate());
            woItem.setCurrencyCode(purchasedItem.getCurrencyCode());
            woItem.setExchangeRate(purchasedItem.getExchangeRate());
            woItem.setPurchasedItem(purchasedItem);
        }
        woItem.setQuantity(quantity);
        woItem.setTransactionType(TransactionType.STOCK.getValue());
        woItem.setItem(item);
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setItemType(itemTypes);
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(purchasedItem.getId());
        woItem.setParentTransactionId(itemTransactions.getParentTransactionId());
        woItem.setApprovedState(1);
        woItem.setRemainingQuantity(0.0);
        return woItem;
    }

    private void addPurchasedItem(FacilioModule module, List<FacilioField> fields, V3PurchasedItemContext parts)
            throws Exception {
        InsertRecordBuilder<V3PurchasedItemContext> readingBuilder = new InsertRecordBuilder<V3PurchasedItemContext>()
                .module(module).fields(fields).addRecord(parts);
        readingBuilder.save();
    }

    public static List<V3PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
                                                                  List<FacilioField> fields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
