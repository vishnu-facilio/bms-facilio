package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		String count = (String) context.get(FacilioConstants.ContextNames.WO_LIST_COUNT);
		List<FacilioField> fields = null;
		if (count != null) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(WorkOrders.ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}
		
		
		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderContext.class)
														.select(fields)
														.maxLevel(0);
//		GenericSelectRecordBuilder selectBuilderCount = new GenericSelectRecordBuilder()
//				.select(fields)
//				.table(dataTableName)
//				.select(fields);
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
//			selectBuilderCount.andCriteria(filterCriteria);
		}
		if (( filters == null || includeParentCriteria) && view != null) {
			Criteria criteria = view.getCriteria();
			selectBuilder.andCriteria(criteria);
//			selectBuilderCount.andCriteria(criteria);
		}
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			selectBuilder.andCriteria(searchCriteria);
//			selectBuilderCount.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				selectBuilder.andCriteria(criteria);
//				selectBuilderCount.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			selectBuilder.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(moduleName,"read");
		if(permissionCriteria != null) {
			selectBuilder.andCriteria(permissionCriteria);
		}
		
		if(context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) != null && (Long) context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) != -1)
		{
			selectBuilder.andCustomWhere("Tickets.DUE_DATE BETWEEN ? AND ?", (Long) context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) * 1000, (Long) context.get(FacilioConstants.ContextNames.WO_DUE_ENDTIME) * 1000);
		}
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			selectBuilder.orderBy(orderBy);
		}
		if (count != null) {
			
		}
		else {
			JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
			if (pagination != null) {
				int page = (int) pagination.get("page");
				int perPage = (int) pagination.get("perPage");
				
				int offset = ((page-1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				
				selectBuilder.offset(offset);
				selectBuilder.limit(perPage);
			}
		}
			List<WorkOrderContext> workOrders = selectBuilder.get();
			TicketAPI.loadTicketLookups(workOrders);
			if (count != null) {
				for (WorkOrderContext wo : workOrders)
				{
					System.out.println("workOrder"+ wo.getData().get("count"));
					context.put(FacilioConstants.ContextNames.WORK_ORDER_COUNT, wo.getData().get("count"));
				}
			}
			else {
				context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
			}		
		return false;
	}
	
}
