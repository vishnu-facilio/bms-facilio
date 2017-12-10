package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.bmsconsole.workflow.WorkorderTemplate;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class WorkOrderAction extends ActionSupport {
	
	public String newWorkOrder() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain newTicket = FacilioChainFactory.getNewWorkOrderChain();
		newTicket.execute(context);
		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		return SUCCESS;
	}
	
	private List<FacilioField> fields;
	
	public List getFormlayout()
	{
		return FormLayout.getNewTicketLayout(fields);
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() {
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private List<Long> attachmentId;
	public List<Long> getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(List<Long> attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	public String addWorkOrder() throws Exception {
		return addWorkOrder(workorder);
	}
	
	public String addWorkOrder(WorkOrderContext workorder) throws Exception {
		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.TICKET, workorder.getTicket());
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		setWorkOrderId(workorder.getId());
		return SUCCESS;
	}
	
	private Long templateId;
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	public String addWorkOrderFromTemplate() throws Exception {
		
		WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), getTemplateId());
		JSONParser parser = new JSONParser();
		JSONObject content = (JSONObject) parser.parse((String) template.getTemplate(new HashMap<String, Object>()).get("content"));
		
		WorkOrderContext workorder = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);
		
		return addWorkOrder(workorder);
	}
	
	public String addWorkOrderTemplate() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		
		Chain addTemplate = FacilioChainFactory.getAddWorkorderTemplateChain();
		addTemplate.execute(context);
		
		return SUCCESS;
	}
	
	public String addPreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		if(workorder.getAsset() != null) {
			preventivemaintenance.setAssetId(workorder.getAsset().getId());
		}
		if(workorder.getSpace() != null) {
			preventivemaintenance.setSpaceId(workorder.getSpace().getId());
		}
		preventivemaintenance.setStatus(true);
		
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		
		Chain addTemplate = FacilioChainFactory.getAddPreventiveMaintenanceChain();
		addTemplate.execute(context);
		
		return SUCCESS;
	}
	
	public String updatePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		if(workorder.getAsset() != null) {
			preventivemaintenance.setAssetId(workorder.getAsset().getId());
		}
		if(workorder.getSpace() != null) {
			preventivemaintenance.setSpaceId(workorder.getSpace().getId());
		}
		preventivemaintenance.setStatus(true);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		
		Chain updatePM = FacilioChainFactory.getUpdatePreventiveMaintenanceChain();
		updatePM.execute(context);
		
		return SUCCESS;
	}
	
	public String preventiveMaintenanceSummary() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));
		
		Chain editPM = FacilioChainFactory.getEditPreventiveMaintenanceChain();
		editPM.execute(context);
		
		setPreventivemaintenance((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		
		return SUCCESS;
	}
	
	public String deletePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Chain deletePM = FacilioChainFactory.getDeletePreventiveMaintenanceChain();
		deletePM.execute(context);
		
		return SUCCESS;
	}
	
	public String changePreventiveMaintenanceStatus() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		
		Chain addTemplate = FacilioChainFactory.getChangePreventiveMaintenanceStatusChain();
		addTemplate.execute(context);
		
		return SUCCESS;
	}
	
	public String getActivePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS, true);
		
		Chain getPmchain = FacilioChainFactory.getGetPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		
		setPms((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		
		return SUCCESS;
	}
	
	public String getUpcomingPreventiveMaintenance() throws Exception {
		
		long startTime = System.currentTimeMillis() / 1000;
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, startTime);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, startTime + (7*24*60*60));
		
		Chain getPmchain = FacilioChainFactory.getGetUpcomingPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		
		setPms((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		
		return SUCCESS;
	}
	
	public String getInactivePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS, false);
		
		Chain getPmchain = FacilioChainFactory.getGetPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		
		setPms((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		
		return SUCCESS;
	}
	
	private PreventiveMaintenance preventivemaintenance;
	public PreventiveMaintenance getPreventivemaintenance() {
		return preventivemaintenance;
	}
	public void setPreventivemaintenance(PreventiveMaintenance preventivemaintenance) {
		this.preventivemaintenance = preventivemaintenance;
	}
	
	private List<PreventiveMaintenance> pms;
	public List<PreventiveMaintenance> getPms() {
		return pms;
	}
	public void setPms(List<PreventiveMaintenance> pms) {
		this.pms = pms;
	}
	
	public String assignWorkOrder() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ASSIGN_TICKET);
		return updateWorkOrder(context);
	}
	
	public String closeWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CLOSE_WORK_ORDER);
		
		workorder = new WorkOrderContext();
		workorder.setStatus(TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), "Closed")); //We shouldn't allow close to be edited
		
		return updateWorkOrder(context);
	}
	
	public String deleteWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Chain deleteWorkOrder = FacilioChainFactory.getDeleteWorkOrderChain();
		deleteWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String updateWorkOrder() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		return updateWorkOrder(context);
	}
	
	private String updateWorkOrder(FacilioContext context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		// updating start time, end time when workorder status changes
		if (workorder.getStatus() != null) {
			
			TicketStatusContext statusObj = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), workorder.getStatus().getId());
			if ("Work in Progress".equalsIgnoreCase(statusObj.getStatus())) {
				workorder.setActualWorkStart(System.currentTimeMillis());
			}
			else if ("Resolved".equalsIgnoreCase(statusObj.getStatus())) {
				workorder.setActualWorkEnd(System.currentTimeMillis());
			} 
		}
		Chain updateWorkOrder = FacilioChainFactory.getUpdateWorkOrderChain();
		updateWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String viewWorkOrder() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderId());
		
		Chain getWorkOrderChain = FacilioChainFactory.getWorkOrderDetailsChain();
		getWorkOrderChain.execute(context);
		
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		return SUCCESS;
	}
	
	private WorkOrderContext workorder;
	public WorkOrderContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(WorkOrderContext workorder) {
		this.workorder = workorder;
	}
	
	private long workOrderId = -1;
	public long getWorkOrderId() {
		return workOrderId;
	}
	public void setWorkOrderId(long workOrderId) {
		this.workOrderId = workOrderId;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private int rowsUpdated = -1;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	public String workOrderList() throws Exception {
		// TODO Auto-generated method stub
 		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
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
 		}
 		else {
 			sorting.put("orderBy", "createdTime");
 			sorting.put("orderType", "desc");
 		}
 		context.put(FacilioConstants.ContextNames.SORTING, sorting);
 		
 		System.out.println("View Name : "+getViewName());
 		Chain workOrderListChain = FacilioChainFactory.getWorkOrderListChain();
 		workOrderListChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setWorkOrders((List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST));
		
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		
		return SUCCESS;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}

	private List<WorkOrderContext> workOrders;
	public List<WorkOrderContext> getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(List<WorkOrderContext> workOrders) {
		this.workOrders = workOrders;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.WORK_ORDER;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewWorkOrderLayout();
	}
	
	public List<WorkOrderContext> getRecords() 
	{
		return workOrders;
	}
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	private String displayName = "All Work Orders";
	public String getViewDisplayName() {
		return displayName;
	}
	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public RecordSummaryLayout getRecordSummaryLayout()
	{
		return RecordSummaryLayout.getRecordSummaryTicketLayout();
	}
	
	public WorkOrderContext getRecord() 
	{
		return workorder;
	}
	
	private String filters;
	public void setFilters(String filters)
	{
		this.filters = filters;
	}
	
	public String getFilters()
	{
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
	
	public String getActivitiesList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET_ID, workOrderId);
		
		Chain activitiesChain = FacilioChainFactory.getTicketActivitiesChain();
		activitiesChain.execute(context);
		
		setActivities((List<TicketActivity>) context.get(FacilioConstants.TicketActivity.TICKET_ACTIVITIES));
		
		return SUCCESS;
	}
	
	private List<TicketActivity> activities;
	public List<TicketActivity> getActivities() {
		return activities;
	}
	public void setActivities(List<TicketActivity> activities) {
		this.activities = activities;
	}

}
