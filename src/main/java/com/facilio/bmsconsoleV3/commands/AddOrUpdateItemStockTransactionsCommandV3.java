package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddOrUpdateItemQuantityCommandV3;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.bmsconsole.commands.AddOrUpdateItemQuantityCommandV3.updateBinQuantityForRotatingType;

public class AddOrUpdateItemStockTransactionsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3PurchasedItemContext> purchasedItems = (List<V3PurchasedItemContext>) context
                .get(FacilioConstants.ContextNames.PURCHASED_ITEM);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

        if (purchasedItems != null && !purchasedItems.isEmpty()) {
            Boolean isBulkItemAdd = (Boolean) context.get(FacilioConstants.ContextNames.IS_BULK_ITEM_ADD);

            List<V3ItemTransactionsContext> inventoryTransaction = new ArrayList<>();
            for (V3PurchasedItemContext ic : purchasedItems) {
                V3ItemContext item = V3ItemsApi.getItems(ic.getItem().getId());
                V3ItemTransactionsContext transaction = new V3ItemTransactionsContext();
                transaction.setTransactionType(TransactionType.STOCK.getValue());
                transaction.setTransactionState(TransactionState.ADDITION.getValue());
                transaction.setPurchasedItem(ic);
                transaction.setItem(ic.getItem());
                transaction.setBin(ic.getBin());
                transaction.setStoreRoom(item.getStoreRoom());
                transaction.setItemType(item.getItemType());
                transaction.setQuantity(ic.getQuantity());
                transaction.setParentId(ic.getId());
                transaction.setIsReturnable(false);
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(transaction, baseCurrency, currencyMap, ic.getCurrencyCode(), ic.getExchangeRate());
                // if bulk insertion add stock transaction entry
                if (isBulkItemAdd != null && isBulkItemAdd) {
                    inventoryTransaction.add(transaction);
                } else {
                    SelectRecordsBuilder<V3ItemTransactionsContext> transactionsselectBuilder = new SelectRecordsBuilder<V3ItemTransactionsContext>()
                            .select(fields).table(module.getTableName()).moduleName(module.getName())
                            .beanClass(V3ItemTransactionsContext.class)
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("transactionState"),
                                    String.valueOf(TransactionState.ADDITION.getValue()), EnumOperators.IS))
                            .andCondition(CriteriaAPI.getCondition(fieldMap.get("purchasedItem"),
                                    String.valueOf(ic.getId()), PickListOperators.IS));
                    List<V3ItemTransactionsContext> transactions = transactionsselectBuilder.get();
                    if (transactions != null && !transactions.isEmpty()) {
                        V3ItemTransactionsContext itemTransaction = transactions.get(0);
                        itemTransaction.setQuantity(ic.getQuantity());
                        CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(itemTransaction, baseCurrency, currencyMap, ic.getCurrencyCode(), ic.getExchangeRate());

                        JSONObject itemTransactionJson = FieldUtil.getAsJSON(itemTransaction);
                        Long itemTransactionId = itemTransaction.getId();
                        V3Util.updateBulkRecords(module.getName(), FacilioUtil.getAsMap(itemTransactionJson), Collections.singletonList(itemTransactionId),false);
                    } else {
                        inventoryTransaction.add(transaction);
                    }
                }
            }

            V3Util.createRecordList(module,FieldUtil.getAsMapList(inventoryTransaction,V3ItemTransactionsContext.class),null,null);
        }
        else  {
            V3ItemContext item = (V3ItemContext) context.get(FacilioConstants.ContextNames.ITEM);
            V3AssetContext asset = (V3AssetContext) context.get(FacilioConstants.ContextNames.ROTATING_ASSET);
            if (item != null) {
                V3ItemContext items = V3ItemsApi.getItems(item.getId());
                double q = items.getQuantity()!=null && items.getQuantity() >= 0 ? items.getQuantity() : 0;
                double cq = items.getCurrentQuantity()!=null && items.getCurrentQuantity() >= 0 ? items.getCurrentQuantity() : 0;
                q+=1;
                cq+=1;
                items.setQuantity(q);
                items.setCurrentQuantity(cq);
                asset.setBin(getBin(asset,item));
                updateBinQuantityForRotatingType(Collections.singletonList(asset.getBin()));

                V3ItemTransactionsContext transaction = new V3ItemTransactionsContext();
                transaction.setIsReturnable(false);

                transaction.setTransactionType(TransactionType.STOCK.getValue());
                transaction.setTransactionState(TransactionState.ADDITION.getValue());
                transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
                transaction.setItem(items);
                transaction.setBin(asset.getBin());
                transaction.setStoreRoom(items.getStoreRoom());
                transaction.setItemType(items.getItemType());
                transaction.setQuantity(1);
                transaction.setAsset(asset);
                transaction.setParentId(asset.getId());
                CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(transaction, baseCurrency, currencyMap, items.getCurrencyCode(), items.getExchangeRate());
                updateItemQty(items);

                V3Util.createRecord(module,FacilioUtil.getAsMap(FieldUtil.getAsJSON(transaction)),null,null);
                context.put(FacilioConstants.ContextNames.BIN, Collections.singletonList(asset.getBin()));

                context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(items.getItemType().getId()));
            }
        }
        return false;
    }

    private V3BinContext getBin(V3AssetContext asset, V3ItemContext item) throws Exception {
        V3BinContext bin = asset.getBin();
        if(bin != null && bin.getId() > 0) {
            V3ItemsApi.validateBin(Collections.singleton(bin.getId()),item);
            return bin;
        }
        bin =  prepareNewBinForAsset(bin, item);
        updateAsset(bin,asset);
        return bin;
    }

    private void updateAsset(V3BinContext bin, V3AssetContext asset) throws Exception {
        asset.setBin(bin);
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        FacilioField binField = modBean.getField("bin", FacilioConstants.ContextNames.ASSET);
        V3RecordAPI.updateRecord(asset, module ,Collections.singletonList(binField));
    }

    private static V3BinContext prepareNewBinForAsset(V3BinContext bin, V3ItemContext item) throws Exception {
        Boolean isFirstBin = !V3ItemsApi.itemHasBin(item);
        if(bin != null && bin.getId() <= 0 && bin.getName() != null) {
            Map<String, Long> newlyAddedBins = V3ItemsApi.quickAddBin(Collections.singleton(bin.getName()), item);
            if(MapUtils.isNotEmpty(newlyAddedBins) && newlyAddedBins.containsKey(bin.getName())){
                Long id = newlyAddedBins.get(bin.getName());
                bin.setId(id);
                if(isFirstBin){
                    V3ItemsApi.makeBinDefault(item, bin);
                }
                return bin;
            } else {
                throw new RuntimeException("Unable To Create Bin");
            }
        }

        if (item.getDefaultBin() != null) {
            return item.getDefaultBin();
        } else {
            bin = V3ItemsApi.addVirtualBin(item);
            V3ItemsApi.makeBinDefault(item, bin);
            return bin;
        }
    }

    private void updateItemQty (V3ItemContext item) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);

        UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                .module(module).fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
        updateBuilder.update(item);
    }

}