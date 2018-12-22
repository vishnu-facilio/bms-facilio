package com.facilio.bmsconsole.actions;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;

public class ViewAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String viewList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		Chain getViewListChain = FacilioChainFactory.getViewListChain();
		getViewListChain.execute(context);
		
		setViews((List<FacilioView>) context.get(FacilioConstants.ContextNames.VIEW_LIST));
		
		return SUCCESS;
	}
	
	public String v2viewlist() throws Exception{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.GROUP_STATUS, true);

		Chain getViewListsChain = FacilioChainFactory.getViewListsChain();
		getViewListsChain.execute(context);
		setViews((List<FacilioView>) context.get(FacilioConstants.ContextNames.VIEW_LIST));
		setGroupViews((LinkedHashMap) context.get(FacilioConstants.ContextNames.GROUP_VIEWS));

		setResult("views", views);

		setResult("groupViews", groupViews);

		return SUCCESS;
	}
	

	public String getViewDetail() throws Exception
	{
		String moduleName = getModuleName();
		if (moduleName == null || moduleName.equals("approval")) {
			if (moduleName.equals("approval")) {
				viewName = "approval_" + viewName;
			}
			moduleName = "workorder";
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.PARENT_VIEW, parentView);
		context.put(FacilioConstants.ContextNames.FETCH_FIELD_DISPLAY_NAMES, true);
		
		Chain getViewChain = FacilioChainFactory.getViewDetailsChain();
		getViewChain.execute(context);
		
		setView((FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW));
		
		return SUCCESS;
	}
	
	private List<ViewSharingContext> viewSharing;
	public List<ViewSharingContext> getViewSharing() {
		return viewSharing;
	}
	public void setViewSharing(List<ViewSharingContext> viewSharing) {
		this.viewSharing = viewSharing;
	}
	
	public String addView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FILTERS, view.getFilters());
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, view.getFields());
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		context.put(FacilioConstants.ContextNames.VIEW_SHARING_LIST, viewSharing);
		if (view.getIncludeParentCriteria()) {
			context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		}
		Chain addView = FacilioChainFactory.getAddViewChain();
		addView.execute(context);
		
		this.viewId = view.getId();
		
		return SUCCESS;
	}
	
	public String v2editView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FILTERS, view.getFilters());
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, view.getFields());
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		context.put(FacilioConstants.ContextNames.VIEW_SHARING_LIST, viewSharing);
		if (view.getIncludeParentCriteria()) {
			context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		}
		
		Chain editView = TransactionChainFactory.editViewChain();
		editView.execute(context);
		
		setViewName(view.getName());
		getViewDetail();
		
		setResult("view", view);
		return SUCCESS;
	}
		
	public String deleteView() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VIEWID, id);
		Chain deleteView = TransactionChainFactory.deleteViewChain();
		deleteView.execute(context);
		return SUCCESS;
		
	}
		
	public String customizeView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.VIEW_LIST, views);
		
		Chain addView = FacilioChainFactory.getViewsCustomizeChain();
		addView.execute(context);
		
		setViews((List<FacilioView>) context.get(FacilioConstants.ContextNames.VIEW_LIST));
		
		return SUCCESS;
	}
	
	public String customizeColumns() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if(viewId == -1) {
			context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		}
		else {
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		}
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, fields);
		Chain customizeColumnChain = FacilioChainFactory.getViewCustomizeColumnChain();
		customizeColumnChain.execute(context);

		setFields((List<ViewField>) context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, fields));
		
		return SUCCESS;
	}
	
	public String customizeSortColumns() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		JSONObject sortObject = new JSONObject();
		sortObject.put("orderBy", getOrderBy());
		sortObject.put("orderType", getOrderType());
		
		context.put(FacilioConstants.ContextNames.VIEWID, viewId);
		context.put(FacilioConstants.ContextNames.SORTING, sortObject);
		
		Chain customizeSortColumnsChain = FacilioChainFactory.getViewCustomizeSortColumnsChain();
		customizeSortColumnsChain.execute(context);
		
		this.sortFields = (List<SortField>) context.get(FacilioConstants.ContextNames.SORT_FIELDS_OBJECT);
		
		return SUCCESS;
	}
	
	private long viewId = -1;
	public long getViewId() {
		return viewId;
	}
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}

	private List<FacilioView> views;
	public List<FacilioView> getViews() {
		return views;
	}
	public void setViews(List<FacilioView> views) {
		this.views = views;
	}
	
	private LinkedHashMap groupViews;
	public LinkedHashMap getGroupViews() {
		return groupViews;
	}
	public void setGroupViews(LinkedHashMap groupViews) {
		this.groupViews = groupViews;
	}

	private FacilioView view;
	public FacilioView getView() {
		return view;
	}
	public void setView(FacilioView view) {
		this.view = view;
	}

	private String parentView;
	public String getParentView() {
		return parentView;
	}
	public void setParentView(String parentView) {
		this.parentView = parentView;
	}


	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String filters;
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private List<ViewField> fields;
	public List<ViewField> getFields() {
		return fields;
	}
	public void setFields(List<ViewField> fields) {
		this.fields = fields;
	}
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public List<SortField> getSortFields() {
		return sortFields;
	}

	public void setSortFields(List<SortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String orderBy;
	
	private String orderType;
	
	private List<SortField> sortFields;
	
}
