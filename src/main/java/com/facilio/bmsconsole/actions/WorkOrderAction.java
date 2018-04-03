package com.facilio.bmsconsole.actions;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
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
	
	public String addWorkOrder(WorkOrderContext workorder)  {
		try {
		FacilioContext context = new FacilioContext();
//		context.put(FacilioConstants.ContextNames.TICKET, workorder.getTicket());
		context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST, getAttachmentId());
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		setWorkOrderId(workorder.getId());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
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
		
		JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(getTemplateId());
		JSONParser parser = new JSONParser();
		JSONObject content = (JSONObject) parser.parse((String) template.getTemplate(new HashMap<String, Object>()).get("content"));
		
		WorkOrderContext workorder = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);
		
		return addWorkOrder(workorder);
	}
	
	public String addPreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		workorder.setRequester(null);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.PM_REMINDERS, reminders);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
		
		Chain addTemplate = FacilioChainFactory.getAddPreventiveMaintenanceChain();
		addTemplate.execute(context);
		
		return SUCCESS;
	}
	
	private List<PMReminder> reminders;
	public List<PMReminder> getReminders() {
		return reminders;
	}
	public void setReminders(List<PMReminder> reminders) {
		this.reminders = reminders;
	}

	private long pmId = -1;
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}
	
	public String executePM() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
		context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);
		
		Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
		executePm.execute(context);
		
		workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		return SUCCESS;
	}
	
	public String executePMs() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, Instant.now().getEpochSecond());
		context.put(FacilioConstants.ContextNames.ONLY_POST_REMINDER_TYPE, true);
		
		Chain executePm = FacilioChainFactory.getExecutePMsChain();
		executePm.execute(context);

		id = (List<Long>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
		
		return SUCCESS;
	}
	
	public String updatePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		workorder.setRequester(null);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, preventivemaintenance);
		context.put(FacilioConstants.ContextNames.PM_REMINDERS, reminders);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
		
		Chain updatePM = FacilioChainFactory.getUpdatePreventiveMaintenanceChain();
		updatePM.execute(context);
		
		return SUCCESS;
	}
	
	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private PMJobsContext pmJob;
	public PMJobsContext getPmJob() {
		return pmJob;
	}
	public void setPmJob(PMJobsContext pmJob) {
		this.pmJob = pmJob;
	}
	
	public String updatePreventiveMaintenanceJob() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		context.put(FacilioConstants.ContextNames.PM_JOB, pmJob);
		context.put(FacilioConstants.ContextNames.PM_RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.PM_ID, pmId);
		
		Chain updatePM = FacilioChainFactory.getUpdatePreventiveMaintenanceJobChain();
		updatePM.execute(context);
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String preventiveMaintenanceSummary() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));
		
		Chain pmSummary = FacilioChainFactory.getPreventiveMaintenanceSummaryChain();
		pmSummary.execute(context);
		
		setPreventivemaintenance((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		setWorkorder((WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER));
		setTaskList((Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP));
		setReminders((List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS));
		
		return SUCCESS;
	}
	
	public String deletePreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Chain deletePM = FacilioChainFactory.getDeletePreventiveMaintenanceChain();
		deletePM.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
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
	
	public String getUpcomingPreventiveMaintenance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, getStartTime());
		//context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, startTime + (7*24*60*60));
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, getEndTime());
		
		if(getFilters() != null) {	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
 		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		Chain getPmchain = FacilioChainFactory.getGetUpcomingPreventiveMaintenanceListChain();
		getPmchain.execute(context);
		
		setPmMap((Map<Long,PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		setPmJobs((List<PMJobsContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST));
		setPmTriggerMap((Map<Long, PMTriggerContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST));
		setPmResourcesMap((Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES));
		
		return SUCCESS;
	}
	
	public String plannedMaintenanceList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		if(getFilters() != null) {	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
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
	
	private Map<Long, PreventiveMaintenance> pmMap;
	public Map<Long, PreventiveMaintenance> getPmMap() {
		return pmMap;
	}
	public void setPmMap(Map<Long, PreventiveMaintenance> pmMap) {
		this.pmMap = pmMap;
	}
	
	private List<PMJobsContext> pmJobs;
	public List<PMJobsContext> getPmJobs() {
		return pmJobs;
	}
	public void setPmJobs(List<PMJobsContext> pmJobs) {
		this.pmJobs = pmJobs;
	}

	private Map<Long, PMTriggerContext> pmTriggerMap;
	public Map<Long, PMTriggerContext> getPmTriggerMap() {
		return pmTriggerMap;
	}
	public void setPmTriggerMap(Map<Long, PMTriggerContext> pmTriggerMap) {
		this.pmTriggerMap = pmTriggerMap;
	}
	
	private Map<Long, ResourceContext> pmResourcesMap;
	public Map<Long, ResourceContext> getPmResourcesMap() {
		return pmResourcesMap;
	}
	public void setPmResourcesMap(Map<Long, ResourceContext> pmResourcesMap) {
		this.pmResourcesMap = pmResourcesMap;
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
		workorder.setStatus(TicketAPI.getStatus("Closed")); //We shouldn't allow close to be edited
		if (actualWorkDuration != -1) {
			workorder.setActualWorkDuration(actualWorkDuration);
		}
		
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
	
	private WorkorderTemplate workorderTemplate;
	public WorkorderTemplate getWorkorderTemplate() {
		return workorderTemplate;
	}
	public void setWorkorderTemplate(WorkorderTemplate workorderTemplate) {
		this.workorderTemplate = workorderTemplate;
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
		context.put(FacilioConstants.ContextNames.WO_DUE_STARTTIME, getStartTime());
		context.put(FacilioConstants.ContextNames.WO_DUE_ENDTIME, getEndTime());
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
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
 		
 		JSONObject pagination = new JSONObject();
 		pagination.put("page", getPage());
 		pagination.put("perPage", getPerPage());
 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
 		System.out.println("PAGINATION ####### "+ pagination);
 		
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
	
	private Map<Long, List<TaskContext>> taskList;
	public Map<Long, List<TaskContext>> getTaskList() {
		return taskList;
	}
	public void setTaskList(Map<Long, List<TaskContext>> taskList) {
		this.taskList = taskList;
	}
	
	private Map<Long, TaskSectionContext> sections;
	public Map<Long, TaskSectionContext> getSections() {
		return sections;
	}
	public void setSections(Map<Long, TaskSectionContext> sections) {
		this.sections = sections;
	}

	private Map<String, List<TaskContext>> tasks;
	public Map<String, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, List<TaskContext>> tasks) {
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
	
	private boolean includeParentFilter;
	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}
	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
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
	
	private long actualWorkDuration = -1;
	public long getActualWorkDuration() {
		return actualWorkDuration;
	}
	public void setActualWorkDuration(long actualWorkDuration) {
		this.actualWorkDuration = actualWorkDuration;
	}
	
	private long estimatedDuration = -1;
	public long getEstimatedDuration() {
		return estimatedDuration;
	}
	public void setEstimatedDuration(long estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}
	
	public String getEstimatedWorkDuration() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderId());
		
		Chain getWorkOrderChain = FacilioChainFactory.getWorkOrderDataChain();
		getWorkOrderChain.execute(context);
		
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		if(workorder != null) {
			long duration = TicketAPI.getEstimatedWorkDuration(workorder);
			setEstimatedDuration(duration);
		}
		
		return SUCCESS;
	}

}
