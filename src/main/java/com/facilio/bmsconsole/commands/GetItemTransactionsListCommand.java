package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetItemTransactionsListCommand implements Command{
	@Override
	public boolean execute(Context context) throws Exception {
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

		SelectRecordsBuilder<ItemTransactionsContext> builder = new SelectRecordsBuilder<ItemTransactionsContext>().module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName)).select(fields);

		List<Long> accessibleSpaces = AccountUtil.getCurrentUser().getAccessibleSpace();
		builder.innerJoin(ModuleFactory.getInventryModule().getTableName())
				.on(ModuleFactory.getInventryModule().getTableName() + ".ID = "
						+ ModuleFactory.getItemTransactionsModule().getTableName() + ".ITEM_ID");
		if (accessibleSpaces != null && !accessibleSpaces.isEmpty()) {
			builder.andCustomWhere(
					"Store_room.ID IN (Select STORE_ROOM_ID from Storeroom_Sites where SITE_ID IN ( ? ))",
					StringUtils.join(accessibleSpaces, ", "));
		}
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
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

		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}
		
		Boolean getShowItemsForReturn = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_ITEMS_FOR_RETURN);
		if(getShowItemsForReturn!=null && getShowItemsForReturn) {
			List<LookupField>lookUpfields = new ArrayList<>();
			lookUpfields.add((LookupField) itemTransactionsFieldsMap.get("purchasedItem"));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("remainingQuantity"), String.valueOf(0), NumberOperators.GREATER_THAN));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("isReturnable"), String.valueOf(true), BooleanOperators.IS));
			builder.andCondition(CriteriaAPI.getCondition(itemTransactionsFieldsMap.get("transactionState"), String.valueOf(2), NumberOperators.EQUALS));
			builder.fetchLookups(lookUpfields);
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
						ItemContext item= ItemsApi.getItems(itemTransactions.getItem().getId());
						item.setItemType(ItemsApi.getItemTypes(item.getItemType().getId()));
						item.setStoreRoom(StoreroomApi.getStoreRoom(item.getStoreRoom().getId()));
						itemTransactions.setItem(item);
											}
				}

				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}

		return false;
	}
}
