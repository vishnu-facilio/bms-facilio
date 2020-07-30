package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;

public class ViewAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String viewList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		FacilioChain getViewListChain = FacilioChainFactory.getViewListChain();
		getViewListChain.execute(context);
		
		setViews((List<FacilioView>) context.get(FacilioConstants.ContextNames.VIEW_LIST));
		
		return SUCCESS;
	}
	

	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
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
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	private ScheduleInfo scheduleInfo;
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	private List<ReportInfo> scheduledReports;
	public List<ReportInfo> getScheduledReports() {
		return scheduledReports;
	}
	
	public void setScheduledReports(List<ReportInfo> scheduledReports) {
		this.scheduledReports = scheduledReports;
	}
	public String addWoScheduleView() throws Exception {
		
		emailTemplate.setName("Report");
		emailTemplate.setFrom("report@${org.domain}.facilio.com");
		
		FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.CV_NAME, viewName);


		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.MAX_COUNT, maxCount);
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduleInfo);
 		
		FacilioChain mailReportChain = ReportsChainFactory.getWoViewScheduleChain();
		mailReportChain.execute(context);
 		
 		return SUCCESS;
	}
	public String woScheduledList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		FacilioChain mailReportChain = ReportsChainFactory.getWoScheduledViewListChain();
		mailReportChain.execute(context);
		setScheduledReports((List<ReportInfo>) context.get(FacilioConstants.ContextNames.REPORT_LIST));
		
		return SUCCESS;
	}
	
	public String editWoScheduledView () throws Exception {
		emailTemplate.setName("Report");
		emailTemplate.setFrom("report@${org.domain}.facilio.com");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, type);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.MAX_COUNT, maxCount);
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduleInfo);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		FacilioChain mailReportChain = ReportsChainFactory.updateWoScheduledViewChain();
		mailReportChain.execute(context);
		
//		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String deleteScheduledView () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		FacilioChain mailReportChain = ReportsChainFactory.deleteWoScheduledViewChain();
		mailReportChain.execute(context);
		
//		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	
	public String v2viewlist() throws Exception{
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS) && moduleName.equals("alarm")) {
			setModuleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		}
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.GROUP_STATUS, getGroupStatus());

		FacilioChain getViewListsChain = FacilioChainFactory.getViewListChain();
		getViewListsChain.execute(context);

		setResult("views", context.get(FacilioConstants.ContextNames.VIEW_LIST));
		setResult("groupViews", context.get(FacilioConstants.ContextNames.GROUP_VIEWS));	// TODO remove

		return SUCCESS;
	}
	
	public String v2viewGroups() throws Exception{
		
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS) && moduleName.equals("alarm")) {
			setModuleName(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		}
		
		FacilioChain chain = ReadOnlyChainFactory.getViewGroupsList();
		
		Context context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.GROUP_STATUS, getGroupStatus());
		
		chain.execute();
		
		setResult("views", context.get(FacilioConstants.ContextNames.VIEW_LIST));
		setResult("groupViews", (List<ViewGroups>) context.get(FacilioConstants.ContextNames.GROUP_VIEWS));
		
		return SUCCESS;
	}
	
	public String v2getViewDetail() throws Exception
	{
	getViewDetail();
	setResult("viewDetail", view);
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
		
		FacilioChain getViewChain = FacilioChainFactory.getViewDetailsChain();
		getViewChain.execute(context);
		
		setView((FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW));
		
		return SUCCESS;
	}
	
	public String addView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FILTERS, view.getFilters());
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, view.getFields());
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		if (view.getIncludeParentCriteria()) {
			context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		}
		FacilioChain addView = FacilioChainFactory.getAddViewChain();
		addView.execute(context);
		
		this.viewId = view.getId();
		
		return SUCCESS;
	}
	
	public String v2addGroup() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addViewGroupChain();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.VIEW_GROUP, viewGroup);
		chain.execute();
		
		
		setResult("viewGroup", context.get(FacilioConstants.ContextNames.VIEW_GROUP));
		return SUCCESS;
	}
	
	public String v2editView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FILTERS, view.getFilters());
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, view.getFields());
		context.put(FacilioConstants.ContextNames.NEW_CV, view);
		if (view.getIncludeParentCriteria()) {
			context.put(FacilioConstants.ContextNames.CV_NAME, parentView);
		}
		
		FacilioChain editView = TransactionChainFactory.editViewChain();
		editView.execute(context);
		
		setViewName(view.getName());
		getViewDetail();
		
		setResult("view", view);
		return SUCCESS;
	}
		
	public String deleteView() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VIEWID, id);
		FacilioChain deleteView = TransactionChainFactory.deleteViewChain();
		deleteView.execute(context);
		return SUCCESS;
		
	}
	
	public String v2DeleteView() throws Exception {		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VIEWID, id);
		FacilioChain deleteView = TransactionChainFactory.deleteViewChain();
		deleteView.execute(context);
		return SUCCESS;	
		
	}
		
	public String customizeView() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.VIEW_LIST, views);
		
		FacilioChain addView = FacilioChainFactory.getViewsCustomizeChain();
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
		FacilioChain customizeColumnChain = FacilioChainFactory.getViewCustomizeColumnChain();
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
		
		FacilioChain customizeSortColumnsChain = FacilioChainFactory.getViewCustomizeSortColumnsChain();
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
	
	private Boolean groupStatus;
	public Boolean getGroupStatus() {
		if (groupStatus == null) {
			return true;
		}
		return groupStatus;
	}
	public void setGroupStatus(Boolean groupStatus) {
		this.groupStatus = groupStatus;
	}
	
	private ViewGroups viewGroup;

	
	public ViewGroups getViewGroup() {
		return viewGroup;
	}
	public void setViewGroup(ViewGroups viewGroup) {
		this.viewGroup = viewGroup;
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
