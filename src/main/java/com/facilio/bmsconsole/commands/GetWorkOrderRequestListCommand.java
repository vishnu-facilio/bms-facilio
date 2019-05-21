package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class GetWorkOrderRequestListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		String count = (String) context.get(FacilioConstants.ContextNames.WO_LIST_COUNT);
		List<FacilioField> fields = new ArrayList<FacilioField>();
		if (count != null) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(WorkOrderRequests.ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields.add(countFld);
		}
		else {
			Boolean getViewsCount = (Boolean) context.get(FacilioConstants.ContextNames.WO_VIEW_COUNT);
			if (getViewsCount != null && getViewsCount) {
				Map<String, Object> viewsCount = setViewsCount();
				context.put(FacilioConstants.ContextNames.VIEW_COUNT, viewsCount);
			}
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		
		SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderRequestContext.class)
														.select(fields)
														.maxLevel(0);

		context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		if (view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		List<Condition> conditionList = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
		if(conditionList != null && !conditionList.isEmpty()) {
			for(Condition condition : conditionList) {
				builder.andCondition(condition);
			}
		}
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}
		
		if (AccountUtil.getCurrentAccount().getUser().getUserType() != 2) {
			Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(moduleName,"read");
			if(permissionCriteria != null) {
				builder.andCriteria(permissionCriteria);
			}
		}
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		List<WorkOrderRequestContext> workOrderRequests = builder.get();
		loadRequesters(workOrderRequests);
		TicketAPI.loadTicketLookups(workOrderRequests);
		if (count != null) {
			for (WorkOrderRequestContext wo : workOrderRequests)
			{
				context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST_COUNT, wo.getData().get("count"));
			}
		}
		else {
			context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST_LIST, workOrderRequests);
		}		
		return false;
	}
	
	private void loadRequesters(List<WorkOrderRequestContext> workOrderRequests) throws Exception {
		if(workOrderRequests != null && !workOrderRequests.isEmpty()) {
			List<Long> ids = workOrderRequests.stream().filter(req -> req.getRequester() != null).map(req -> req.getRequester().getId()).collect(Collectors.toList());
			
			if(ids.size() > 0)
			{
				/*Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(AccountConstants.getOrgUserFields());
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), ids, NumberOperators.EQUALS));
				Map<Long, User> users = AccountUtil.getOrgBean().getOrgUsersAsMap(AccountUtil.getCurrentOrg().getOrgId(), criteria);
				List<Long> reqIds = ids.stream().filter(id -> !users.containsKey(id)).collect(Collectors.toList());
				if (!reqIds.isEmpty()) {
					users.
				}*/
				Map<Long, User> requesters = AccountUtil.getUserBean().getUsersAsMap(null, ids);
				for(WorkOrderRequestContext workOrderRequest : workOrderRequests) {
					if(workOrderRequest.getRequester() != null)
					{
						workOrderRequest.setRequester(requesters.get(workOrderRequest.getRequester().getId()));
					}
				}
			}
		}
	}
	
	private Map<String, Object> setViewsCount() throws Exception {
		
		FacilioModule workorderRequestModule = ModuleFactory.getWorkOrderRequestsModule();
		
		Map<String, Object> viewDetail = new HashMap<>();
		viewDetail.put("open", getWrCount(ViewFactory.getCriteriaForView("open", workorderRequestModule)));
		viewDetail.put("rejected", getWrCount(ViewFactory.getCriteriaForView("rejected", workorderRequestModule)));
		viewDetail.put("all", getWrCount(null));
		
		/*
		List<Map<String, Object>> views = new ArrayList<>();
		Map<String, FacilioView> fviews = ViewFactory.getModuleViews(workorderRequestModule.getName());
		
		FacilioView open = fviews.get("open");
		viewDetail.put("name", open.getName());
		viewDetail.put("displayName", open.getDisplayName());
		setWrCount(open.getCriteria(), viewDetail);
		views.add(viewDetail);
		
		FacilioView rejected = fviews.get("rejected");
		viewDetail.put("name", rejected.getName());
		viewDetail.put("displayName", rejected.getDisplayName());
		setWrCount(rejected.getCriteria(), viewDetail);
		views.add(viewDetail);
		
		FacilioView all = fviews.get("all");
		viewDetail.put("name", all.getName());
		viewDetail.put("displayName", all.getDisplayName());
		setWrCount(all.getCriteria(), viewDetail);
		views.add(viewDetail);*/
		
		return viewDetail;
	}
	
	private long getWrCount (Criteria viewCriteria) throws Exception {
		FacilioModule workorderRequestModule = ModuleFactory.getWorkOrderRequestsModule();
		String wrTable = workorderRequestModule.getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(wrTable)
				.select(Collections.singletonList( FieldFactory.getField("count", "COUNT(" + wrTable + ".ID)", FieldType.NUMBER)))
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workorderRequestModule))
				.andCriteria(viewCriteria);
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(workorderRequestModule.getName());
		if(scopeCriteria != null)
		{
			selectBuilder.andCriteria(scopeCriteria);
		}

		if (AccountUtil.getCurrentAccount().getUser().getUserType() != 2) {
			Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(workorderRequestModule.getName(),"read");
			if(permissionCriteria != null) {
				selectBuilder.andCriteria(permissionCriteria);
			}
		}
		
		long count = 0;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			count = (long) props.get(0).get("count");
		}
		return count;
	}
	
}
