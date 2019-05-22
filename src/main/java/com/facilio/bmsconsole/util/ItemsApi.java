package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;

public class ItemsApi {
	public static Map<Long, ItemTypesContext> getItemTypesMap(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.getAsMap();
	}

	public static ItemTypesContext getItemTypes(long id) throws Exception {
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		List<ItemTypesContext> items = selectBuilder.get();
		if (items != null && !items.isEmpty()) {
			return items.get(0);
		}
		return null;
	}

	public static ItemContext getItems(long id) throws Exception {
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
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.fetchLookups(lookUpfields);
		List<ItemContext> items = selectBuilder.get();
		if (items != null && !items.isEmpty()) {
			return items.get(0);
		}
		return null;
	}

	public static List<ItemContext> getItemsForStore(long storeId) throws Exception {
		if (storeId <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
				;
		List<ItemContext> items = selectBuilder.get();
		return items;

	}



	public static ItemContext getItemsForTypeAndStore(long storeId, long itemTypeId) throws Exception {
		if (storeId <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))

				;
		List<ItemContext> items = selectBuilder.get();
		if(!CollectionUtils.isEmpty(items)) {
			return items.get(0);
		}
	 throw new IllegalArgumentException("No appropriate item found");
	}

	public static Map<String, Long> getAllItemTypes() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		SelectRecordsBuilder<ItemTypesContext> selectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(ItemTypesContext.class);
		List<ItemTypesContext> items = selectBuilder.get();
		Map<String, Long> itemNameVsIdMap = new HashMap<>();
		if (items != null && !items.isEmpty()) {
			for (ItemTypesContext item : items) {
				itemNameVsIdMap.put(item.getName(), item.getId());
			}
			return itemNameVsIdMap;
		}
		return null;
	}
	public static ItemTransactionsContext getItemTransactionsForRequestedLineItem(long requestedLineItem) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTransactionModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);

		SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.select(itemTransactionFields).table(itemTransactionModule.getTableName())
				.moduleName(itemTransactionModule.getName()).beanClass(ItemTransactionsContext.class)
				.andCondition(CriteriaAPI.getCondition("REQUESTED_LINEITEM", "requestedLineItem", String.valueOf(requestedLineItem), NumberOperators.EQUALS));

		List<ItemTransactionsContext> itemTransactions = selectBuilder.get();
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

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>()
				.select(itemFields).moduleName(itemModule.getName())
				.andCondition(CriteriaAPI.getCondition(itemFieldMap.get("itemType"), String.valueOf(id),
						NumberOperators.EQUALS))
				.beanClass(ItemContext.class).orderBy("LAST_PURCHASED_DATE DESC");

		List<ItemContext> items = builder.get();
		long storeRoomId = -1;
		ItemContext item;
		if (items != null && !items.isEmpty()) {
			item = items.get(0);
			storeRoomId = item.getStoreRoom().getId();
			lastPurchasedDate = item.getLastPurchasedDate();
			lastPurchasedPrice = item.getLastPurchasedPrice();
		}

		ItemTypesContext itemType = new ItemTypesContext();
		itemType.setId(id);
		itemType.setLastPurchasedDate(lastPurchasedDate);
		itemType.setLastPurchasedPrice(lastPurchasedPrice);

		UpdateRecordBuilder<ItemTypesContext> updateBuilder = new UpdateRecordBuilder<ItemTypesContext>()
				.module(itemTypesModule).fields(modBean.getAllFields(itemTypesModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(itemType.getId(), itemTypesModule));

		updateBuilder.update(itemType);

		StoreroomApi.updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
	}

	public static long getLastPurchasedItemDateForItemId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
		long lastPurchasedDate = -1;
		SelectRecordsBuilder<AssetContext> itemselectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(assetFields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingItem"), String.valueOf(id), NumberOperators.EQUALS))
				.orderBy("PURCHASED_DATE DESC");
		List<AssetContext> assetscontext = itemselectBuilder.get();
		if(assetscontext!=null && !assetscontext.isEmpty()) {
			lastPurchasedDate = assetscontext.get(0).getPurchasedDate();
		}

		return lastPurchasedDate;
	}

	public static double getLastPurchasedItemPriceForItemId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(assetFields);
		long lastPurchasedDate = -1;
		SelectRecordsBuilder<AssetContext> itemselectBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(assetFields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(AssetContext.class).andCondition(CriteriaAPI.getCondition(fieldMap.get("rotatingItem"), String.valueOf(id), NumberOperators.EQUALS))
				.orderBy("PURCHASED_DATE DESC");
		List<AssetContext> assetscontext = itemselectBuilder.get();
		if(assetscontext!=null && !assetscontext.isEmpty()) {
			lastPurchasedDate = assetscontext.get(0).getUnitPrice();
		}

		return lastPurchasedDate;
	}

	public static void updateLastPurchasedDateForItem(ItemContext item)
			throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
		updateBuilder.update(item);
	}

	public static ItemContext getItem(ItemTypesContext itemType, StoreRoomContext storeroom) throws Exception {
		ItemContext itemc = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
		SelectRecordsBuilder<ItemContext> itemselectBuilder = new SelectRecordsBuilder<ItemContext>().select(itemFields)
				.table(itemModule.getTableName()).moduleName(itemModule.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(storeroom.getId()),
						NumberOperators.EQUALS));

		List<ItemContext> items = itemselectBuilder.get();
		if (items != null && !items.isEmpty()) {
			for (ItemContext item : items) {
				if (item.getItemType().getId() == itemType.getId()) {
					return item;
				}
			}
			return addItem(itemModule, itemFields, storeroom, itemType);
		} else {
			return addItem(itemModule, itemFields, storeroom, itemType);
		}
	}

	public static ItemContext addItem(FacilioModule module, List<FacilioField> fields, StoreRoomContext store, ItemTypesContext itemType) throws Exception {
		ItemContext item = new ItemContext();
		item.setStoreRoom(store);
		item.setItemType(itemType);
		item.setCostType(CostType.FIFO);
		InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>().module(module)
				.fields(fields);
		readingBuilder.withLocalId();
		readingBuilder.insert(item);
		return item;
	}


	public static ItemContext getItemsForTypeAndStore(long storeId, long itemTypeId) throws Exception {
        if (storeId <= 0) {
            return null;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
        SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
                .table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
                .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))

                ;
        List<ItemContext> items = selectBuilder.get();
        if(!CollectionUtils.isEmpty(items)) {
            return items.get(0);
        }
     throw new IllegalArgumentException("No appropriate item found");
    }


}
