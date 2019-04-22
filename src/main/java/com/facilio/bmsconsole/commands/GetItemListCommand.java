package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetItemListCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields;
		if (getCount == null) {
			getCount = false;
		}
		if (getCount) {
			fields = FieldFactory.getCountField(module);
		} else {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>().module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName)).select(fields);

		List<Long> accessibleSpaces = AccountUtil.getCurrentUser().getAccessibleSpace();
		builder.innerJoin(ModuleFactory.getStoreRoomModule().getTableName())
				.on(ModuleFactory.getStoreRoomModule().getTableName() + ".ID = "
						+ ModuleFactory.getInventryModule().getTableName() + ".STORE_ROOM_ID");
		if (accessibleSpaces != null && !accessibleSpaces.isEmpty()) {
			builder.andCustomWhere(
					"Store_room.ID IN (Select STORE_ROOM_ID from Storeroom_Sites where SITE_ID IN ( ? ))",
					StringUtils.join(accessibleSpaces, ", "));
		}
		if (getCount) {
			builder.setAggregation();
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			if(orderBy.contains("LAST_PURCHASED_DATE")) {
				orderBy = "Item.LAST_PURCHASED_DATE" + orderBy.substring(0, orderBy.indexOf(" ")); 
			}
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
			if (criteria != null && !criteria.isEmpty()) {
				builder.andCriteria(criteria);
			}
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}

		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
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

		long getStartTime = System.currentTimeMillis();
		List<ItemContext> records = builder.get();
		long getTimeTaken = System.currentTimeMillis() - getStartTime;

		if (records != null && !records.isEmpty()) {
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			} else {
				Set<Long> locatoionIds = new HashSet<Long>();
				for (ItemContext inventry : records) {
					if (inventry.getItemType().getId() != -1) {
						Map<Long, ItemTypesContext> itemMap = ItemsApi
								.getItemTypesMap((inventry.getItemType().getId()));
						inventry.setItemType(itemMap.get(inventry.getItemType().getId()));
					}

					if (inventry.getStoreRoom().getId() != -1) {
						Map<Long, StoreRoomContext> storeroomMap = StoreroomApi
								.getStoreRoomMap((inventry.getStoreRoom().getId()));
						inventry.setStoreRoom(storeroomMap.get(inventry.getStoreRoom().getId()));
					}
					inventry.setCostType(CostType.valueOf(inventry.getCostType()));
				}

				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}

		long timeTaken = System.currentTimeMillis() - startTime;
		return false;
	}
}
