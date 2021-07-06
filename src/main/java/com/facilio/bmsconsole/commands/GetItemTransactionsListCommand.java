package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetItemTransactionsListCommand extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields;
		if (getCount != null && getCount) {
			fields = FieldFactory.getCountField(module);
		} else {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		Map<String, FacilioField> itemTransactionsFieldsMap = FieldFactory.getAsMap(fields);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

		SelectRecordsBuilder<ItemTransactionsContext> builder = new SelectRecordsBuilder<ItemTransactionsContext>()
				.module(module).beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields);

		builder.innerJoin(ModuleFactory.getInventryModule().getTableName())
				.on(ModuleFactory.getInventryModule().getTableName() + ".ID = "
						+ ModuleFactory.getItemTransactionsModule().getTableName() + ".ITEM_ID");
//		Long siteId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID);
//		Set<Long> storeIds = StoreroomApi.getStoreRoomList(siteId, false);
//		if(CollectionUtils.isNotEmpty(storeIds)) {
//			builder.andCondition(CriteriaAPI.getConditionFromList("STORE_ROOM_ID", "storeRoomId", storeIds, NumberOperators.EQUALS));
//		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			// temp fix
			// if(orderBy.contains("CREATED_TIME")) {
			// orderBy = "Item_Transactions.CREATED_TIME" +
			// orderBy.substring(12);
			// }
			builder.orderBy(orderBy);
		}
		if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("inventory", "read");
			if (permissionCriteria != null) {
				builder.innerJoin("Store_room").on("Item.STORE_ROOM_ID = Store_room.ID");
				builder.andCriteria(permissionCriteria);
			}
		}
		Integer maxLevel = (Integer) context.get(FacilioConstants.ContextNames.MAX_LEVEL);
		if (maxLevel != null && maxLevel != -1) {
			builder.maxLevel(maxLevel);
		}

		List<Long> idsToSelect = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (idsToSelect != null && !idsToSelect.isEmpty()) {
			builder.andCondition(CriteriaAPI.getIdCondition(idsToSelect, module));
		}

		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		if ((filters == null || includeParentCriteria) && view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}

		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}

		builder.fetchSupplement((LookupField) itemTransactionsFieldsMap.get("purchasedItem"));
		builder.fetchSupplement((LookupField) itemTransactionsFieldsMap.get("asset"));
		builder.fetchSupplement((LookupField) itemTransactionsFieldsMap.get("itemType"));
		builder.fetchSupplement((LookupField) itemTransactionsFieldsMap.get("issuedTo"));

		Boolean getShowItemsForReturn = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_ITEMS_FOR_RETURN);
		Boolean getShowItemsForIssue = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_ITEMS_FOR_ISSUE);
		
		if (getShowItemsForReturn != null && getShowItemsForReturn) {
			// List<LookupField>lookUpfields = new ArrayList<>();
			// lookUpfields.add((LookupField)
			// itemTransactionsFieldsMap.get("purchasedItem"));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("remainingQuantity"),
					String.valueOf(0), NumberOperators.GREATER_THAN));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("isReturnable"),
					String.valueOf(true), BooleanOperators.IS));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("transactionState"),
					String.valueOf(2), NumberOperators.EQUALS));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("transactionType"),
					String.valueOf(4), NumberOperators.NOT_EQUALS));
			// builder.fetchLookups(lookUpfields);
		}
		else if(getShowItemsForIssue != null && getShowItemsForIssue) {
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("remainingQuantity"),
					String.valueOf(0), NumberOperators.GREATER_THAN));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("transactionState"),
					String.valueOf(2), NumberOperators.EQUALS));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("issuedTo"),
					String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
		
		}

		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			builder.offset(offset);
			builder.limit(perPage);
		}

		List<ItemTransactionsContext> records = builder.get();
		if (records != null && !records.isEmpty()) {
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			} else {
				for (ItemTransactionsContext itemTransactions : records) {
					if (itemTransactions.getItem() != null && itemTransactions.getItem().getId() != -1) {
						ItemContext item = ItemsApi.getItems(itemTransactions.getItem().getId());
						item.setItemType(ItemsApi.getItemTypes(item.getItemType().getId()));
						item.setStoreRoom(StoreroomApi.getStoreRoom(item.getStoreRoom().getId()));
						itemTransactions.setItem(item);
						if(itemTransactions.getTransactionType() == TransactionType.MANUAL.getValue()) {
							itemTransactions.setResource(ResourceAPI.getExtendedResource(itemTransactions.getParentId()));
						}
					}
				}

				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}

		return false;
	}
}
