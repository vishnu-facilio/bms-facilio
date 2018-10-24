package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class ApprovalAction extends FacilioAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public String approvalList() throws Exception {
		// TODO Auto-generated method stub
		
		String[] views = viewName.split("_", 2); 
		FacilioContext context = new FacilioContext();
		setViewName(views[1]);
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.WO_DUE_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WO_DUE_ENDTIME, getEndTime());
		if (getCount() != null) {	// only count
			context.put(FacilioConstants.ContextNames.WO_LIST_COUNT, getCount());
		}
		if(getShowViewsCount()) {
			context.put(FacilioConstants.ContextNames.WO_VIEW_COUNT, true);
		}
		if( getSubView() != null) {
			context.put(FacilioConstants.ContextNames.SUB_VIEW, getSubView());
		}
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		context.put(FacilioConstants.ContextNames.CRITERIA_IDS, getCriteriaIds());

		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "workorder.subject,workorder.description");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}

		JSONObject sorting = new JSONObject();
		if (getOrderBy() != null) {
			sorting.put("orderBy", getOrderBy());
			sorting.put("orderType", getOrderType());
		} else {
			sorting.put("orderBy", "createdTime");
			sorting.put("orderType", "desc");
		}
		context.put(FacilioConstants.ContextNames.SORTING, sorting);

		JSONObject pagination = new JSONObject();
		pagination.put("page", getPage());
		pagination.put("perPage", getPerPage());
		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		System.out.println("PAGINATION ####### " + pagination);

		System.out.println("View Name : " + getViewName());
		Chain workOrderListChain = ReadOnlyChainFactory.getWorkOrderListChain();
		workOrderListChain.execute(context);

		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		if (getCount() != null) {
//			setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
			setWoCount((long) context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT));
			System.out.println("data" + getWoCount() + getViewName());
		}
		else {
			if(getShowViewsCount()) {
				setSubViewsCount((List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SUB_VIEW_COUNT));
				setSubView((String) context.get(FacilioConstants.ContextNames.SUB_VIEW));
			}
			setWorkOrders((List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
		}
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if (cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}

		return SUCCESS;
	}
	public String v2approvalList() throws Exception {
		approvalList();
		setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		if (getSubView() != null) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW, subView);
		}
		if (getShowViewsCount()) {
			setResult(FacilioConstants.ContextNames.SUB_VIEW_COUNT, subViewsCount);
		}
		setResult(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		return SUCCESS;
	}
	private String criteriaIds;

	public void setCriteriaIds(String criteriaIds) {
		this.criteriaIds = criteriaIds;
	}

	public String getCriteriaIds() {
		return this.criteriaIds;
	}
	
	private Boolean showViewsCount;
	public Boolean getShowViewsCount() {
		if (showViewsCount == null) {
			return false;
		}
		return showViewsCount;
	}
	public void setShowViewsCount(Boolean showViewsCount) {
		this.showViewsCount = showViewsCount;
	}
	
	List<Map<String, Object>> subViewsCount;
	public List<Map<String, Object>> getSubViewsCount() {
		return subViewsCount;
	}
	public void setSubViewsCount(List<Map<String, Object>> subViewsCount) {
		this.subViewsCount = subViewsCount;
	}
	private String displayName = "All Approval";

	public String getViewDisplayName() {
		return displayName;
	}

	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	private String viewName = null;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private long assetId = -1;

	public long getAssetId() {
		return assetId;
	}

	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}

	private long spaceId = -1;

	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private long startTime = -1;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	private long endTime = -1;

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private String filters;

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return this.filters;
	}

	String orderBy;

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	String orderType;

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderType() {
		return this.orderType;
	}

	String search;

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return this.search;
	}

	private int page;

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return this.page;
	}

	public int perPage = 40;

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getPerPage() {
		return this.perPage;
	}
	
	private List<WorkOrderContext> workOrders;

	public List<WorkOrderContext> getWorkOrders() {
		return workOrders;
	}

	public void setWorkOrders(List<WorkOrderContext> workOrders) {
		this.workOrders = workOrders;
	}

	public String getModuleLinkName() {
		return FacilioConstants.ContextNames.WORK_ORDER;
	}

	public ViewLayout getViewlayout() {
		return ViewLayout.getViewWorkOrderLayout();
	}

	public List<WorkOrderContext> getRecords() {
		return workOrders;
	}
	
	private String subView;
	public String getSubView() {
		return subView;
	}
	public void setSubView(String subView) {
		this.subView = subView;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private long woCount;

	public long getWoCount() {
		return woCount;
	}

	public void setWoCount(long woCount) {
		this.woCount = woCount;
	}
	
	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
