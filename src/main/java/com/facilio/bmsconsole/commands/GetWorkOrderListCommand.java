package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		List<Long> woIds = new ArrayList<>();
		woIds = (List<Long>) context.get(FacilioConstants.ContextNames.WO_IDS);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		String count = (String) context.get(FacilioConstants.ContextNames.WO_LIST_COUNT);
		boolean isApproval = (Boolean) context.get(FacilioConstants.ContextNames.IS_APPROVAL);
		
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
			List<String> selectFields = (List<String>) context.get(FacilioConstants.ContextNames.FETCH_SELECTED_FIELDS);
			if (CollectionUtils.isNotEmpty(selectFields)) {
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				fields = selectFields.stream().map(fieldName -> fieldMap.get(fieldName)).collect(Collectors.toList());
			}
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("workorder");
		List<WorkOrderContext> workOrders = new ArrayList<WorkOrderContext>();
		if (woIds != null && !woIds.isEmpty()) {
			SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
					.table(dataTableName)
					.moduleName(moduleName)
					.beanClass(WorkOrderContext.class)
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),StringUtils.join(woIds, ","),NumberOperators.EQUALS));
			
			workOrders = selectBuilder.get();
		}
		else {
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
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			selectBuilder.andCriteria(view.getCriteria());
		}
		
		String subView = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		if (subView != null && !subView.isEmpty()) {
			Criteria subViewCriteria = ViewFactory.getCriteriaForView(subView, ModuleFactory.getWorkOrdersModule());
			if (subViewCriteria != null && !subViewCriteria.isEmpty()) {
				selectBuilder.andCriteria(subViewCriteria);
			}
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

//		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
//		if(scopeCriteria != null)
//		{
//			selectBuilder.andCriteria(scopeCriteria);
//		}
//
//		if (AccountUtil.getCurrentAccount().getUser().getUserType() != 2) {
//			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(moduleName,"read");
//			if(permissionCriteria != null) {
//				selectBuilder.andCriteria(permissionCriteria);
//			}
//		}

		if(context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) != null && (Long) context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) != -1)
		{
			selectBuilder.andCustomWhere("Tickets.DUE_DATE BETWEEN ? AND ?", (Long) context.get(FacilioConstants.ContextNames.WO_DUE_STARTTIME) * 1000, (Long) context.get(FacilioConstants.ContextNames.WO_DUE_ENDTIME) * 1000);
		}
		Boolean fetchAllTypes = (Boolean) context.get(ContextNames.WO_FETCH_ALL);
		if (!isApproval && !isUpcomingGroup(view) && (fetchAllTypes == null || !fetchAllTypes)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("STATUS_ID", "status", TicketAPI.getStatus("preopen").getId()+"", NumberOperators.NOT_EQUALS));

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

				if (perPage != -1) {
					int offset = ((page-1) * perPage);
					if (offset < 0) {
						offset = 0;
					}

					selectBuilder.offset(offset);
					selectBuilder.limit(perPage);
				}

				if (perPage == -1 && (filters == null || !filters.containsKey("createdTime"))) {
					throw new IllegalArgumentException("createdTime filter is mandatory");
				}
			}
		}
		 workOrders = selectBuilder.get();
	}

		if (count != null) {
			if (workOrders != null && !workOrders.isEmpty()) {
				context.put(FacilioConstants.ContextNames.WORK_ORDER_COUNT, workOrders.get(0).getData().get("count"));
			}
		}
		else {
			TicketAPI.loadWorkOrderLookups(workOrders);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		}		
		return false;
	}

	private static boolean isUpcomingGroup(FacilioView view) {
		Map<String, List<String>> woGroup = ViewFactory.getGroupViews(FacilioConstants.ContextNames.WORK_ORDER);
		List<String> viewList = woGroup.get("upcomingWorkorder");
		for (String v: viewList) {
			if (view.getName().equals(v)) {
				return true;
			}
		}
		return false;
	}
	
	private List<Map<String, Object>> setSubViewCount(FacilioView view) throws Exception {
		List<Map<String, Object>> subViews = ViewFactory.getSubViewsCriteria(FacilioConstants.ContextNames.WORK_ORDER, view.getName());
		if (subViews != null) {
			for(Map<String, Object> subView: subViews) {
				setWorkOrderCount(subView, view.getCriteria());
			}
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workorderModule))
				.andCriteria(viewCriteria);
		
		Criteria subViewcriteria = (Criteria) subView.get("criteria");
		if (subViewcriteria != null && !subViewcriteria.isEmpty()) {
			selectBuilder.andCriteria(subViewcriteria);
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(workorderModule.getName());
		if(scopeCriteria != null)
		{
			selectBuilder.andCriteria(scopeCriteria);
		}

		Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(workorderModule.getName(),"read");
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
