package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

public class ItemTypeQuantityRollupCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> itemTypesIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_TYPES_IDS);
		if (itemTypesIds != null && !itemTypesIds.isEmpty()) {
			FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

			FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
			List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
			Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
			itemFields.addAll(FieldFactory.getCurrencyPropsFields(itemModule));

			FacilioModule transModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			List<FacilioField> transFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
			Map<String, FacilioField> transFieldMap = FieldFactory.getAsMap(transFields);

			CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
			Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

			List<FacilioField> itemTypesFields = modBean.getAllFields(itemTypesModule.getName());
			CurrencyUtil.addMultiCurrencyFieldsToFields(itemTypesFields, itemTypesModule);
			List<FacilioField> itemTypesMultiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(itemTypesFields);

			long lastPurchasedDate = -1, lastIssuedDate = -1;
			double lastPurchasedPrice = -1;
			if (itemTypesIds != null && !itemTypesIds.isEmpty()) {
				Map<Long, V3ItemTypesContext> oldItemTypesRecords = V3RecordAPI.getRecordsMap(itemTypesModule.getName(), itemTypesIds, V3ItemTypesContext.class);
				for (Long id : itemTypesIds) {
					double quantity = getQuantity(id, itemModule, itemFieldMap,false);
					double currentQuantity = getQuantity(id, itemModule, itemFieldMap,true);
					V3ItemTypesContext oldItemTypeRecord = oldItemTypesRecords.get(id);
					SelectRecordsBuilder<V3ItemContext> builder = new SelectRecordsBuilder<V3ItemContext>()
							.select(itemFields).moduleName(itemModule.getName())
							.andCondition(CriteriaAPI.getCondition(itemFieldMap.get("itemType"), String.valueOf(id),
									NumberOperators.EQUALS))
							.beanClass(V3ItemContext.class).orderBy("LAST_PURCHASED_DATE DESC");

					List<V3ItemContext> items = builder.get();
					V3ItemTypesContext itemType = new V3ItemTypesContext();
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
							itemType.setLastPurchasedPrice(lastPurchasedPrice);
						}
						Map<String, Object> itemTypeAsMap = FieldUtil.getAsProperties(itemType);
						itemTypeAsMap.put("currencyCode", item.getCurrencyCode());
						List<String> currRecordPatchFieldNames = new ArrayList<String>() {{
							add("lastPurchasedPrice");
						}};
						CurrencyUtil.checkAndUpdateCurrencyProps(itemTypeAsMap, oldItemTypeRecord, baseCurrency, currencyMap, currRecordPatchFieldNames, itemTypesMultiCurrencyFields);
						itemType = FieldUtil.getAsBeanFromMap(itemTypeAsMap, V3ItemTypesContext.class);
					}

					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(transFieldMap.get("transactionState"),
							String.valueOf(4), NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition(transFieldMap.get("parentTransactionId"),
							"", CommonOperators.IS_EMPTY));
					Criteria criteriaIssue = new Criteria();
					criteriaIssue.addAndCondition(CriteriaAPI.getCondition(transFieldMap.get("transactionState"),
							String.valueOf(2), NumberOperators.EQUALS));
					Criteria finalCriteria = new Criteria();
					finalCriteria.andCriteria(criteria);
					finalCriteria.orCriteria(criteriaIssue);


					SelectRecordsBuilder<V3ItemTransactionsContext> issuetransactionsbuilder = new SelectRecordsBuilder<V3ItemTransactionsContext>()
							.select(transFields).moduleName(transModule.getName())
							.andCondition(CriteriaAPI.getCondition(transFieldMap.get("itemType"), String.valueOf(id),
									NumberOperators.EQUALS))
							.beanClass(V3ItemTransactionsContext.class).orderBy("CREATED_TIME DESC");
					builder.andCriteria(finalCriteria);


					List<V3ItemTransactionsContext> transactions = issuetransactionsbuilder.get();
					if (transactions != null && !transactions.isEmpty()) {
						V3ItemTransactionsContext transaction = transactions.get(0);
						lastIssuedDate = transaction.getSysCreatedTime();
					}

					itemType.setId(id);
					itemType.setQuantity(quantity);
					itemType.setCurrentQuantity(currentQuantity);
					itemType.setLastPurchasedDate(lastPurchasedDate);
					itemType.setLastIssuedDate(lastIssuedDate);

					UpdateRecordBuilder<V3ItemTypesContext> updateBuilder = new UpdateRecordBuilder<V3ItemTypesContext>()
							.module(itemTypesModule).fields(itemTypesFields)
							.andCondition(CriteriaAPI.getIdCondition(itemType.getId(), itemTypesModule));

					updateBuilder.update(itemType);

					updateStoreRoomLastPurchasedDate(storeRoomId, lastPurchasedDate);
				}
			}
		}
		return false;
	}

	public static double getQuantity(long id, FacilioModule module, Map<String, FacilioField> itemFieldMap,Boolean getCurrentQuantity)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}

		List<FacilioField> field = new ArrayList<>();
		if(getCurrentQuantity){
			field.add(FieldFactory.getField("currentQuantity", "sum(CURRENT_QUANTITY)", FieldType.DECIMAL));
		}
		else{
			field.add(FieldFactory.getField("quantity", "sum(QUANTITY)", FieldType.DECIMAL));
		}

		SelectRecordsBuilder<V3ItemContext> builder = new SelectRecordsBuilder<V3ItemContext>()
				.select(field).moduleName(module.getName()).andCondition(CriteriaAPI
						.getCondition(itemFieldMap.get("itemType"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("currentQuantity") != null && getCurrentQuantity) {
				return (double) rs.get(0).get("currentQuantity");
			}
			else if (rs.get(0).get("quantity") != null && !getCurrentQuantity){
				return (double) rs.get(0).get("quantity");
			}
			return 0d;
		}
		return 0d;
	}

	private void updateStoreRoomLastPurchasedDate(long storeRoomId, long lastPurchasedDate) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		V3StoreRoomContext storeRoom = new V3StoreRoomContext();
		storeRoom.setId(storeRoomId);
		storeRoom.setLastPurchasedDate(lastPurchasedDate);
		UpdateRecordBuilder<V3StoreRoomContext> updateBuilder = new UpdateRecordBuilder<V3StoreRoomContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(storeRoomId, module));
		updateBuilder.update(storeRoom);
	}

}
