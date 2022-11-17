package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class V3ItemsApi {
    public static Map<Long, V3ItemTypesContext> getItemTypesMap(long id) throws Exception {
        if (id <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
        SelectRecordsBuilder<V3ItemTypesContext> selectBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
        return selectBuilder.getAsMap();
    }

    public static V3ItemTypesContext getItemTypes(long id) throws Exception {
        if (id <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
        SelectRecordsBuilder<V3ItemTypesContext> selectBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
        List<V3ItemTypesContext> items = selectBuilder.get();
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

    public static V3ItemContext getItems(long id) throws Exception {
        if (id <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField>lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) fieldsAsMap.get("itemType"));
        lookUpfields.add((LookupField) fieldsAsMap.get("storeRoom"));
        SelectRecordsBuilder<V3ItemContext> selectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ItemContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module))
                .fetchSupplements(lookUpfields);
        List<V3ItemContext> items = selectBuilder.get();
        if (items != null && !items.isEmpty()) {
            return items.get(0);
        }
        return null;
    }

    public static List<V3ItemContext> getItemsForStore(long storeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        SelectRecordsBuilder<V3ItemContext> selectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ItemContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                ;
        List<V3ItemContext> items = selectBuilder.get();
        return items;

    }




    public static Map<String, Long> getAllItemTypes() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
        SelectRecordsBuilder<V3ItemTypesContext> selectBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3ItemTypesContext.class);
        List<V3ItemTypesContext> items = selectBuilder.get();
        Map<String, Long> itemNameVsIdMap = new HashMap<>();
        if (items != null && !items.isEmpty()) {
            for (V3ItemTypesContext item : items) {
                itemNameVsIdMap.put(item.getName(), item.getId());
            }
            return itemNameVsIdMap;
        }
        return null;
    }
    public static V3ItemTransactionsContext getItemTransactionsForRequestedLineItem(long requestedLineItem) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTransactionModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
        List<FacilioField> itemTransactionFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

        SelectRecordsBuilder<V3ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<V3ItemTransactionsContext>()
                .select(itemTransactionFields).table(itemTransactionModule.getTableName())
                .moduleName(itemTransactionModule.getName()).beanClass(V3ItemTransactionsContext.class)
                .andCondition(CriteriaAPI.getCondition("REQUESTED_LINEITEM", "requestedLineItem", String.valueOf(requestedLineItem), NumberOperators.EQUALS));

        List<V3ItemTransactionsContext> itemTransactions = selectBuilder.get();
        if(!CollectionUtils.isEmpty(itemTransactions)) {
            return itemTransactions.get(0);
        }
        throw new IllegalArgumentException("Item shoud be issued before being used");
    }

    public static void updateLastPurchasedDetailsForItemType(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

        FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);

        long lastPurchasedDate = -1;
        double lastPurchasedPrice = -1;

        SelectRecordsBuilder<V3ItemContext> builder = new SelectRecordsBuilder<V3ItemContext>()
                .select(itemFields).moduleName(itemModule.getName())
                .andCondition(CriteriaAPI.getCondition(itemFieldMap.get("itemType"), String.valueOf(id),
                        NumberOperators.EQUALS))
                .beanClass(V3ItemContext.class).orderBy("LAST_PURCHASED_DATE DESC");

        List<V3ItemContext> items = builder.get();
        long storeRoomId = -1;
        V3ItemContext item;
        if (items != null && !items.isEmpty()) {
            item = items.get(0);
            storeRoomId = item.getStoreRoom().getId();
            if(item.getLastPurchasedDate()!=null){
                lastPurchasedDate = item.getLastPurchasedDate();
            }
            if( item.getLastPurchasedPrice()!=null){
                lastPurchasedPrice = item.getLastPurchasedPrice();
            }
        }

        V3ItemTypesContext itemType = new V3ItemTypesContext();
        itemType.setId(id);
        itemType.setLastPurchasedDate(lastPurchasedDate);
        itemType.setLastPurchasedPrice(lastPurchasedPrice);

        UpdateRecordBuilder<V3ItemTypesContext> updateBuilder = new UpdateRecordBuilder<V3ItemTypesContext>()
                .module(itemTypesModule).fields(modBean.getAllFields(itemTypesModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(itemType.getId(), itemTypesModule));

        updateBuilder.update(itemType);

        StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
    }

    public static Long getLastPurchasedItemDateForItemId(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
        Long lastPurchasedDate = null;
        SelectRecordsBuilder<V3AssetContext> itemselectBuilder = new SelectRecordsBuilder<V3AssetContext>()
                .select(assetFields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingItem"), String.valueOf(id), NumberOperators.EQUALS))
                .orderBy("PURCHASED_DATE DESC");
        List<V3AssetContext> assetscontext = itemselectBuilder.get();
        if(assetscontext!=null && !assetscontext.isEmpty()) {
            lastPurchasedDate = assetscontext.get(0).getPurchasedDate();
        }

        return lastPurchasedDate;
    }

    public static Double getLastPurchasedItemPriceForItemId(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
        Double lastPurchasedPrice = null;
        SelectRecordsBuilder<V3AssetContext> itemselectBuilder = new SelectRecordsBuilder<V3AssetContext>()
                .select(assetFields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingItem"), String.valueOf(id), NumberOperators.EQUALS))
                .orderBy("PURCHASED_DATE DESC");
        List<V3AssetContext> assetscontext = itemselectBuilder.get();
        if(assetscontext!=null && !assetscontext.isEmpty()) {
            lastPurchasedPrice = assetscontext.get(0).getUnitPrice();
        }

        return lastPurchasedPrice;
    }

    public static void updateLastPurchasedDateForItem(V3ItemContext item)
            throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                .module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
        updateBuilder.update(item);
    }

    public static V3ItemContext getItem(V3ItemTypesContext itemType, V3StoreRoomContext storeroom) throws Exception {
        V3ItemContext itemc = null;
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);

        Collection<SupplementRecord> supplementFields = new ArrayList<>();
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        supplementFields.add((LookupField) fieldsMap.get("itemType"));
        supplementFields.add((LookupField) fieldsMap.get("storeRoom"));

        SelectRecordsBuilder<V3ItemContext> itemselectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(itemFields)
                .table(itemModule.getTableName()).moduleName(itemModule.getName()).beanClass(V3ItemContext.class)
                .andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(storeroom.getId()),
                        NumberOperators.EQUALS))
                .fetchSupplements(supplementFields);

        List<V3ItemContext> items = itemselectBuilder.get();
        if (items != null && !items.isEmpty()) {
            for (V3ItemContext item : items) {
                if (item.getItemType().getId() == itemType.getId()) {
                    return item;
                }
            }
            return addItem(itemModule, itemFields, storeroom, itemType);
        } else {
            return addItem(itemModule, itemFields, storeroom, itemType);
        }
    }

    public static V3ItemContext addItem(FacilioModule module, List<FacilioField> fields, V3StoreRoomContext store, V3ItemTypesContext itemType) throws Exception {
        V3ItemContext item = new V3ItemContext();
        item.setStoreRoom(store);
        item.setItemType(itemType);
        item.setCostType(V3ItemContext.CostType.FIFO.getIndex());
        InsertRecordBuilder<V3ItemContext> readingBuilder = new InsertRecordBuilder<V3ItemContext>().module(module)
                .fields(fields);
        readingBuilder.withLocalId();
        readingBuilder.insert(item);
        return item;
    }


    public static V3ItemContext getItemsForTypeAndStore(long storeId, long itemTypeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        SelectRecordsBuilder<V3ItemContext> selectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ItemContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))

                ;
        List<V3ItemContext> items = selectBuilder.get();
        if(!CollectionUtils.isEmpty(items)) {
            return items.get(0);
        }
        throw new IllegalArgumentException("Item(s) not available in selected store");
    }

    public static List<V3ItemContext> getItemsForType(List<Long> itemTypeIds) throws Exception {

        String ids = StringUtils.join(itemTypeIds, ",");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        SelectRecordsBuilder<V3ItemContext> selectBuilder = new SelectRecordsBuilder<V3ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ItemContext.class)
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(ids), NumberOperators.EQUALS))

                ;
        List<V3ItemContext> items = selectBuilder.get();
        if(!CollectionUtils.isEmpty(items)) {
            return items;
        }
        throw new IllegalArgumentException("No appropriate item found");
    }

    public static V3ItemContext getItem(Long itemTypeId,Long storeRoomId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String itemModuleName = FacilioConstants.ContextNames.ITEM;
        FacilioModule module = modBean.getModule(itemModuleName);
        List<FacilioField> fields = modBean.getAllFields(itemModuleName);
        V3ItemContext item = new V3ItemContext();
        if(itemTypeId != null && itemTypeId >=0 && storeRoomId != null && storeRoomId >=0) {
            SelectRecordsBuilder<V3ItemContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemContext>()
                    .module(module)
                    .beanClass(V3ItemContext.class)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeRoomId), NumberOperators.EQUALS));
            item = selectRecordsBuilder.fetchFirst();
        }
        return item;
    }

}
