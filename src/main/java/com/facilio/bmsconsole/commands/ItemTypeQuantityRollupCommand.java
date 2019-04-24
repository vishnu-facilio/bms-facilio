package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemTypeQuantityRollupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> itemTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_TYPES_IDS);
		if (itemTypesIds != null && !itemTypesIds.isEmpty()) {
			FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

			FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
			List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
			Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);

			FacilioModule transModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			List<FacilioField> transFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			Map<String, FacilioField> transFieldMap = FieldFactory.getAsMap(transFields);

			long lastPurchasedDate = -1, lastIssuedDate = -1;
			double lastPurchasedPrice = -1;
			if (itemTypesIds != null && !itemTypesIds.isEmpty()) {
				for (Long id : itemTypesIds) {
					double quantity = getTotalQuantity(id, itemModule, itemFieldMap);

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

					SelectRecordsBuilder<ItemTransactionsContext> issuetransactionsbuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
							.select(transFields).moduleName(transModule.getName())
							.andCondition(CriteriaAPI.getCondition(transFieldMap.get("itemType"), String.valueOf(id),
									NumberOperators.EQUALS))
							.andCondition(CriteriaAPI.getCondition(transFieldMap.get("transactionState"),
									String.valueOf(2), NumberOperators.EQUALS))
							.beanClass(ItemTransactionsContext.class).orderBy("CREATED_TIME DESC");

					List<ItemTransactionsContext> transactions = issuetransactionsbuilder.get();
					if (transactions != null && !transactions.isEmpty()) {
						ItemTransactionsContext transaction = transactions.get(0);
						lastIssuedDate = transaction.getSysCreatedTime();
					}

					ItemTypesContext itemType = new ItemTypesContext();
					itemType.setId(id);
					itemType.setCurrentQuantity(quantity);
					itemType.setLastPurchasedDate(lastPurchasedDate);
					itemType.setLastPurchasedPrice(lastPurchasedPrice);
					itemType.setLastIssuedDate(lastIssuedDate);

					UpdateRecordBuilder<ItemTypesContext> updateBuilder = new UpdateRecordBuilder<ItemTypesContext>()
							.module(itemTypesModule).fields(modBean.getAllFields(itemTypesModule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(itemType.getId(), itemTypesModule));

					updateBuilder.update(itemType);

					updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
				}
			}
		}
		return false;
	}

	public static double getTotalQuantity(long id, FacilioModule module, Map<String, FacilioField> itemFieldMap)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalQuantity", "sum(QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>()
				.select(field).moduleName(module.getName()).andCondition(CriteriaAPI
						.getCondition(itemFieldMap.get("itemType"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalQuantity") != null) {
				return (double) rs.get(0).get("totalQuantity");
			}
			return 0d;
		}
		return 0d;
	}

	private void updateStoreRoomLastPurchasedDate(long storeRoomId, long lastPurchasedDate) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		StoreRoomContext storeRoom = new StoreRoomContext();
		storeRoom.setId(storeRoomId);
		storeRoom.setLastPurchasedDate(lastPurchasedDate);
		UpdateRecordBuilder<StoreRoomContext> updateBuilder = new UpdateRecordBuilder<StoreRoomContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(storeRoomId, module));
		updateBuilder.update(storeRoom);
	}

}
