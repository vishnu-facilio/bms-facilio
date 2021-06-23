package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetToolsListCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(GetToolsListCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
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

		SelectRecordsBuilder<ToolContext> builder = new SelectRecordsBuilder<ToolContext>().module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName)).select(fields);

		Long siteId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID);
		boolean includeServingSite = (boolean) context.get(FacilioConstants.ContextNames.INCLUDE_SERVING_SITE);
		
		Set<Long> storeIds = StoreroomApi.getStoreRoomList(siteId, includeServingSite);
		if(CollectionUtils.isNotEmpty(storeIds)) {
			builder.andCondition(CriteriaAPI.getConditionFromList("STORE_ROOM_ID", "storeRoomId", storeIds, NumberOperators.EQUALS));
		}
		if (getCount) {
			builder.setAggregation();
		}
		if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO){
			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("inventory", "read");
			if (permissionCriteria != null) {
				builder.innerJoin("Store_room").on("Tool.STORE_ROOM_ID = Store_room.ID");
				builder.andCriteria(permissionCriteria);
			}
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			// if(orderBy.contains("LAST_PURCHASED_DATE")) {
			// orderBy = "Tool.LAST_PURCHASED_DATE" + orderBy.substring(0,
			// orderBy.indexOf(" "));
			// }
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

		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
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
		List<ToolContext> records = builder.get();
		long getTimeTaken = System.currentTimeMillis() - getStartTime;

		if (records != null && !records.isEmpty()) {
			if (AccountUtil.getCurrentOrg().getOrgId() == 75) {
				log.info("records" + records);
				log.info("filterCriteria" + filterCriteria);
				log.info("filters" + filters);
			}
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			} else {
				Set<Long> locatoionIds = new HashSet<Long>();
				for (ToolContext stockedTools : records) {
					if (stockedTools.getToolType().getId() != -1) {
						ToolTypesContext tool = ToolsApi.getToolTypes(stockedTools.getToolType().getId());
						stockedTools.setToolType(tool);
					}

					if (stockedTools.getStoreRoom().getId() != -1) {
						Map<Long, StoreRoomContext> storeroomMap = StoreroomApi
								.getStoreRoomMap((stockedTools.getStoreRoom().getId()));
						stockedTools.setStoreRoom(storeroomMap.get(stockedTools.getStoreRoom().getId()));
					}
				}

				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}

		long timeTaken = System.currentTimeMillis() - startTime;
		return false;
	}
}
