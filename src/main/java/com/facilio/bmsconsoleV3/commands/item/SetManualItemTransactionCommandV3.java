package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class SetManualItemTransactionCommandV3  extends FacilioCommand {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub

        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemTransactionsContext> itemTransactions = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(itemTransactions) && MapUtils.isNotEmpty(bodyParams) && ((bodyParams.containsKey("issue") && (boolean) bodyParams.get("issue")) || (bodyParams.containsKey("return") && (boolean) bodyParams.get("return")))) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
            List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);

            List<V3ItemTransactionsContext> itemTransactionsList = new ArrayList<>();
            List<V3ItemTransactionsContext> itemTransactionsToBeAdded = new ArrayList<>();
            long itemTypeId = -1;
            ApprovalState approvalState = null;
            if (itemTransactions != null && !itemTransactions.isEmpty()) {
                for (V3ItemTransactionsContext itemTransaction : itemTransactions) {
                    V3ItemContext item = getItem(itemTransaction.getItem().getId());
                    itemTypeId = item.getItemType().getId();
                    V3ItemTypesContext itemType = getItemType(itemTypeId);
                    long parentId = itemTransaction.getParentId();

                    if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
                            && item.getQuantity() < itemTransaction.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                    } else {
                        approvalState = ApprovalState.YET_TO_BE_REQUESTED;
                        if (itemTransaction.getRequestedLineItem() != null && itemTransaction.getRequestedLineItem().getId() > 0) {
                            approvalState = ApprovalState.APPROVED;
                        }

                        if (itemType.isRotating()) {
                            List<Long> assetIds = itemTransaction.getAssetIds();
                            List<V3AssetContext> assets = getPurchasedItemsListFromId(assetIds);
                            if (assets != null) {
                                for (V3AssetContext asset : assets) {
                                    if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
                                            && asset.isUsed()) {
                                        throw new IllegalArgumentException("Insufficient quantity in inventory!");
                                    }
                                    V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                                    if (itemTransaction.getTransactionStateEnum() == TransactionState.RETURN) {
                                        asset.setIsUsed(false);
                                    } else if (itemTransaction
                                            .getTransactionStateEnum() == TransactionState.ISSUE) {
                                        asset.setIsUsed(true);
                                    }
                                    //	}
                                    woItem = setWorkorderItemObj(null, 1, item, parentId, itemTransaction, itemType,
                                            approvalState, asset, context);
                                    updatePurchasedItem(asset);
                                    itemTransactionsList.add(woItem);
                                    itemTransactionsToBeAdded.add(woItem);
                                }
                            }
                        } else {

                            List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);

                            if (purchasedItems != null && !purchasedItems.isEmpty()) {
                                V3PurchasedItemContext pItem = purchasedItems.get(0);
                                if (itemTransaction.getQuantity() <= pItem.getCurrentQuantity()) {
                                    V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                                    woItem = setWorkorderItemObj(pItem, itemTransaction.getQuantity(), item, parentId,
                                            itemTransaction, itemType, approvalState, null, context);
                                    itemTransactionsList.add(woItem);
                                    itemTransactionsToBeAdded.add(woItem);
                                } else {
                                    double requiredQuantity = itemTransaction.getQuantity();
                                    for (V3PurchasedItemContext purchaseitem : purchasedItems) {
                                        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
                                        double quantityUsedForTheCost = 0;
                                        if (requiredQuantity <= purchaseitem.getCurrentQuantity()) {
                                            quantityUsedForTheCost = requiredQuantity;
                                        }
                                        else if(purchaseitem.getCurrentQuantity()==0 && itemTransaction.getTransactionState()==TransactionState.RETURN.getValue()){
                                            quantityUsedForTheCost = requiredQuantity;
                                        }
                                        else {
                                            quantityUsedForTheCost = purchaseitem.getCurrentQuantity();
                                        }
                                        woItem = setWorkorderItemObj(purchaseitem, quantityUsedForTheCost, item,
                                                parentId, itemTransaction, itemType, approvalState, null, context);
                                        requiredQuantity -= quantityUsedForTheCost;
                                        itemTransactionsList.add(woItem);
                                        itemTransactionsToBeAdded.add(woItem);
                                        if (requiredQuantity <= 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                if(CollectionUtils.isNotEmpty(itemTransactionsToBeAdded)){
                    recordMap.put(moduleName, itemTransactionsToBeAdded);
                }
                context.put(FacilioConstants.ContextNames.PARENT_ID, itemTransactions.get(0).getParentId());
                context.put(FacilioConstants.ContextNames.ITEM_ID, itemTransactions.get(0).getItem().getId());
                context.put(FacilioConstants.ContextNames.ITEM_IDS,
                        Collections.singletonList(itemTransactions.get(0).getItem().getId()));
                context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactionsList);
                context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypeId);
                context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypeId));
                context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
                        itemTransactions.get(0).getTransactionStateEnum());
            }
        }
        return false;
    }

    private V3ItemTransactionsContext setWorkorderItemObj(V3PurchasedItemContext purchasedItem, double quantity,
                                                          V3ItemContext item, long parentId, V3ItemTransactionsContext itemTransactions, V3ItemTypesContext itemTypes,
                                                          ApprovalState approvalState, V3AssetContext asset,Context context) throws Exception {
        V3ItemTransactionsContext woItem = new V3ItemTransactionsContext();
        if(itemTransactions.getRequestedLineItem() != null) {
            woItem.setRequestedLineItem(itemTransactions.getRequestedLineItem());
        }

        woItem.setTransactionType(TransactionType.MANUAL.getValue());


        woItem.setTransactionState(itemTransactions.getTransactionStateEnum());
        woItem.setIsReturnable(true);
        if (purchasedItem != null) {
            woItem.setPurchasedItem(purchasedItem);
        }
        if (asset != null) {
            woItem.setAsset(asset);
        }
        woItem.setQuantity(quantity);
        if(itemTransactions.getResource() != null && itemTransactions.getResource().getResourceType() != ResourceContext.ResourceType.USER.getValue()) {
            woItem.setTransactionCost(quantity* purchasedItem.getUnitcost());
        }
        woItem.setItem(item);
        woItem.setStoreRoom(item.getStoreRoom());
        woItem.setItemType(itemTypes);
        woItem.setSysModifiedTime(System.currentTimeMillis());
        woItem.setParentId(parentId);
        woItem.setParentTransactionId(itemTransactions.getParentTransactionId());
        woItem.setApprovedState(approvalState);
        if (itemTransactions.getTransactionStateEnum() == TransactionState.ISSUE) {
            if(itemTransactions.getTransactionType() == TransactionType.MANUAL.getValue() && (itemTransactions.getResource() == null || (itemTransactions.getResource() != null && itemTransactions.getResource().getResourceType() == ResourceContext.ResourceType.USER.getValue()) )) {
                woItem.setRemainingQuantity(quantity);
            }
            else
            {
                woItem.setRemainingQuantity(0.0);
            }
        }

        if (itemTransactions.getTransactionStateEnum() == TransactionState.RETURN) {
            woItem.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
        }
        woItem.setIssuedTo(itemTransactions.getIssuedTo());
        JSONObject newinfo = new JSONObject();

        if (itemTypes.isRotating() && woItem.getTransactionStateEnum() == TransactionState.ISSUE) {

            asset.setLastIssuedToUser(woItem.getIssuedTo());
            if(woItem.getWorkorder() != null) {
                asset.setLastIssuedToWo(woItem.getWorkorder().getId());
            }
            asset.setLastIssuedTime(System.currentTimeMillis());
            V3AssetAPI.updateAsset(asset, asset.getId());

            if(woItem.getTransactionTypeEnum() == TransactionType.MANUAL) {
                User user = AccountUtil.getUserBean().getUser(woItem.getParentId(), true);
                newinfo.put("issuedTo",user.getName());

            }
            CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.ISSUE, newinfo,
                    (FacilioContext) context);

        }
        else if(itemTypes.isRotating() && woItem.getTransactionStateEnum() == TransactionState.RETURN) {
            User user = new User();
            user.setId(-99);
            asset.setLastIssuedToUser(user);
            asset.setLastIssuedToWo(-99l);
            asset.setLastIssuedTime(-99l);
            V3AssetAPI.updateAsset(asset, asset.getId());

            if(woItem.getTransactionTypeEnum() == TransactionType.MANUAL) {
                user = AccountUtil.getUserBean().getUser(woItem.getParentId(), true);
                newinfo.put("returnedBy", user.getName());
            }
            else if(woItem.getTransactionTypeEnum() == TransactionType.WORKORDER) {
                newinfo.put("returnedBy", "WO - #"+ woItem.getParentId());
            }
            CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.RETURN, newinfo,
                    (FacilioContext) context);
        }

        return woItem;
    }

    public static V3ItemContext getItem(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<LookupField> lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldMap.get("storeRoom"));
        SelectRecordsBuilder<V3ItemContext> selectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ItemContext.class)
                .andCustomWhere(module.getTableName() + ".ID = ?", id).fetchSupplements(lookUpfields);

        List<V3ItemContext> inventories = selectBuilder.get();

        if (inventories != null && !inventories.isEmpty()) {
            return inventories.get(0);
        }
        return null;
    }

    public static V3ItemTypesContext getItemType(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);

        SelectRecordsBuilder<V3ItemTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
                .beanClass(V3ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, itemTypesModule));

        List<V3ItemTypesContext> itemTypes = itemTypesselectBuilder.get();
        if (itemTypes != null && !itemTypes.isEmpty()) {
            return itemTypes.get(0);
        }
        return null;
    }

    private void updatePurchasedItem(V3AssetContext purchasedItem) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        UpdateRecordBuilder<V3AssetContext> updateBuilder = new UpdateRecordBuilder<V3AssetContext>().module(module)
                .fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
        updateBuilder.update(purchasedItem);

        System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

    }

    public static V3PurchasedItemContext getInventoryCost(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);

        SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedItemContext.class).andCustomWhere(module.getTableName() + ".ITEM_ID = ?", id);

        List<V3PurchasedItemContext> inventoryCosts = selectBuilder.get();

        if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
            return inventoryCosts.get(0);
        }
        return null;
    }

    public static List<V3PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
                                                                    List<FacilioField> fields,Map<String, Object> bodyParams) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedItemContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);
        if(bodyParams.containsKey("issue") && (boolean) bodyParams.get("issue")){
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
                    NumberOperators.GREATER_THAN));
        }
        List<V3PurchasedItemContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }

    public static List<V3AssetContext> getPurchasedItemsListFromId(List<Long> id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        SelectRecordsBuilder<V3AssetContext> selectBuilder = new SelectRecordsBuilder<V3AssetContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3AssetContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        List<V3AssetContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }
}
