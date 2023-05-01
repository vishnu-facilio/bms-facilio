package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetToolTransactionsListCommand extends FacilioCommand {
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
		Map<String, FacilioField> toolTransactionsFieldsMap = FieldFactory.getAsMap(fields);

		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

		SelectRecordsBuilder<ToolTransactionContext> builder = new SelectRecordsBuilder<ToolTransactionContext>()
				.module(module).beanClass(FacilioConstants.ContextNames.getClassFromModuleName(moduleName))
				.select(fields);

		builder.innerJoin(ModuleFactory.getToolModule().getTableName())
				.on(ModuleFactory.getToolModule().getTableName() + ".ID = "
						+ ModuleFactory.getToolTransactionsModule().getTableName() + ".TOOL");
//		Long siteId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_SITE_ID);
//		Set<Long> storeIds = StoreroomApi.getStoreRoomList(siteId, false);
//		if(CollectionUtils.isNotEmpty(storeIds)) {
//			builder.andCondition(CriteriaAPI.getConditionFromList("STORE_ROOM_ID", "storeRoomId", storeIds, NumberOperators.EQUALS));
//		}
	
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
		
		
		builder.fetchSupplement((LookupField) toolTransactionsFieldsMap.get("purchasedTool"));
		builder.fetchSupplement((LookupField) toolTransactionsFieldsMap.get("asset"));
		builder.fetchSupplement((LookupField) toolTransactionsFieldsMap.get("toolType"));
		builder.fetchSupplement((LookupField) toolTransactionsFieldsMap.get("issuedTo"));


		Boolean getShowToolsForReturn = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_TOOLS_FOR_RETURN);
        Boolean getShowToolsForIssue = (Boolean) context.get(FacilioConstants.ContextNames.SHOW_TOOLS_FOR_ISSUE);
		if (getShowToolsForReturn != null && getShowToolsForReturn) {
			
//			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("remainingQuantity"),
//					String.valueOf(0), NumberOperators.GREATER_THAN));
			// builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("isReturnable"),
			// String.valueOf(true), BooleanOperators.IS));
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("transactionState"),
					String.valueOf(4), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("parentTransactionId"),
					String.valueOf(0), NumberOperators.LESS_THAN));
			Criteria criteriaIssue = new Criteria();
			criteriaIssue.addAndCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("transactionState"),
					String.valueOf(2), NumberOperators.EQUALS));
			Criteria finalCriteria = new Criteria();
			finalCriteria.andCriteria(criteria);
			finalCriteria.orCriteria(criteriaIssue);
			builder.andCriteria(finalCriteria);
			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("remainingQuantity"),
					String.valueOf(0), NumberOperators.GREATER_THAN));

		}
		else if(getShowToolsForIssue != null && getShowToolsForIssue) {
			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("transactionState"),
					String.valueOf(2), NumberOperators.EQUALS));
			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("transactionType"),
					String.valueOf(4), NumberOperators.NOT_EQUALS));

			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("issuedTo"),
					String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
			builder.andCondition(CriteriaAPI.getCondition(toolTransactionsFieldsMap.get("remainingQuantity"),
					String.valueOf(0), NumberOperators.GREATER_THAN));

		}

		if(AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO) {
			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("inventory","read");
			if(permissionCriteria != null) {
				builder.innerJoin("Store_room").on("Tool.STORE_ROOM_ID = Store_room.ID");
				builder.andCriteria(permissionCriteria);
			}
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

		List<ToolTransactionContext> records = builder.get();
		if (records != null && !records.isEmpty()) {
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			} else {
				for (ToolTransactionContext toolTransactions : records) {
					if (toolTransactions.getTool() != null && toolTransactions.getTool().getId() != -1) {
						ToolContext tool = ToolsApi.getTool(toolTransactions.getTool().getId());
						tool.setToolType(ToolsApi.getToolTypes(tool.getToolType().getId()));
						tool.setStoreRoom(StoreroomApi.getStoreRoom(tool.getStoreRoom().getId()));
						toolTransactions.setTool(tool);
						if(toolTransactions.getTransactionType() == TransactionType.MANUAL.getValue()) {
							toolTransactions.setResource(ResourceAPI.getExtendedResource(toolTransactions.getParentId()));
						}
					}
				}

				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}

		return false;
	}
}
