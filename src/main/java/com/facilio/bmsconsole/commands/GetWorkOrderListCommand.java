package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		String count = (String) context.get(FacilioConstants.ContextNames.WO_LIST_COUNT);
		List<FacilioField> fields = null;
		
		 List<Map<String, Object>> subViewsCount = null;
		
		if (count != null) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(WorkOrders.ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			Boolean getSubViewCount = (Boolean) context.get(FacilioConstants.ContextNames.WO_VIEW_COUNT);
			if (getSubViewCount != null && getSubViewCount) {
				subViewsCount = setSubViewCount(view);
				context.put(FacilioConstants.ContextNames.SUB_VIEW_COUNT, subViewsCount);
			}
			fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		}


		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(WorkOrderContext.class)
				.select(fields)
				.maxLevel(0);

		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		if (( filters == null || includeParentCriteria) && view != null) {
			Criteria criteria = view.getCriteria();
			selectBuilder.andCriteria(criteria);
		}
		
		String subView = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		if (subView != null && !subView.isEmpty()) {
			selectBuilder.andCriteria(ViewFactory.getCriteriaForView(subView, ModuleFactory.getWorkOrdersModule()));
		}
		else if (subViewsCount != null && !subViewsCount.isEmpty()) {
			String defaultSubView = (String) subViewsCount.get(0).get("name");
			selectBuilder.andCriteria(ViewFactory.getCriteriaForView(defaultSubView, ModuleFactory.getWorkOrdersModule()));
			context.put(FacilioConstants.ContextNames.SUB_VIEW, defaultSubView);
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

		if (count != null) {
			if (workOrders != null && !workOrders.isEmpty()) {
				context.put(FacilioConstants.ContextNames.WORK_ORDER_COUNT, workOrders.get(0).getData().get("count"));
			}
		}
		else {
			TicketAPI.loadTicketLookups(workOrders);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}		
		return false;
	}
	
	/*private void setSubViewCount(Context context, FacilioView view) throws Exception {
		FacilioModule module = ModuleFactory.getWorkOrdersModule();
		Map<String, Integer> viewsCount = new LinkedHashMap<>();
		switch(view.getName()) {
		
			// Sub Views - My open, Overdue, Unassigned
			case "myteamopen": 
				Criteria criteria = ViewFactory.getCriteriaForView("myopen", module);
				setWorkOrderCount("myopen", criteria, view, viewsCount, module);
				
				criteria = ViewFactory.getCriteriaForView("myoverdue", module);
				setWorkOrderCount("myoverdue", criteria, view, viewsCount, module);
				
				criteria = ViewFactory.getCriteriaForView("unassigned", module);
				setWorkOrderCount("unassigned", criteria, view, viewsCount, module);
				
				break;
				
			// Sub Views - My Overdue, Due Today
			case "myopen":
				Condition myOpen = ViewFactory.getMyUserCondition(module);
				break;
				
			case "resolved":
				break;
				
			case "closed":
				break;
		}
		
		setWorkOrderCount("all", null, view, viewsCount, module);
		
		context.put(FacilioConstants.ContextNames.RECORD_COUNT, viewsCount);
		
	}*/
	
	private List<Map<String, Object>> setSubViewCount(FacilioView view) throws Exception {
		List<Map<String, Object>> subViews = ViewFactory.getSubViewsCriteria(FacilioConstants.ContextNames.WORK_ORDER, view.getName());
		for(Map<String, Object> subView: subViews) {
			setWorkOrderCount(subView, view.getCriteria());
			subView.remove("criteria");
		}
		return subViews;
	}
	
	private void setWorkOrderCount (Map<String, Object>subView, Criteria viewCriteria) throws Exception {
		FacilioModule workorderModule = ModuleFactory.getWorkOrdersModule();
		String woTable = workorderModule.getTableName();
		String ticketTable = ModuleFactory.getTicketsModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(woTable)
				.innerJoin(ticketTable)
				.on(woTable+".ID=" + ticketTable+ ".ID")
				.select(Collections.singletonList( FieldFactory.getField("count", "COUNT(WorkOrders.ID)", FieldType.NUMBER)))
				.andCriteria(viewCriteria);
		
		Criteria subViewcriteria = (Criteria) subView.get("criteria");
		if (subViewcriteria != null && !subViewcriteria.isEmpty()) {
			selectBuilder.andCriteria(subViewcriteria);
		}
		
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(workorderModule.getName());
		if(scopeCriteria != null)
		{
			selectBuilder.andCriteria(scopeCriteria);
		}

		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria(workorderModule.getName(),"read");
		if(permissionCriteria != null) {
			selectBuilder.andCriteria(permissionCriteria);
		}
		
		long count = 0;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			count = (long) props.get(0).get("count");
		}
		subView.put("count", count);
	}
	
}
