package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

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
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		
		SelectRecordsBuilder<WorkOrderRequestContext> builder = new SelectRecordsBuilder<WorkOrderRequestContext>()
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderRequestContext.class)
														.select(fields)
														.maxLevel(0);

		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
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
			System.out.println(AccountUtil.getCurrentAccount());
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
						workOrderRequest.setRequester((User) requesters.get(workOrderRequest.getRequester().getId()));
					}
				}
			}
		}
	}
}
