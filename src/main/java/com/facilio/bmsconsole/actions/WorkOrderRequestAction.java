package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class WorkOrderRequestAction extends ActionSupport {
	public String newWorkOrderRequest() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain newTicket = FacilioChainFactory.getNewWorkOrderRequestChain();
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
	
	public String approveWorkOrderRequest() throws Exception {
		workorderrequest.setRequestStatus(WorkOrderRequestContext.RequestStatus.APPROVED);
		FacilioContext context = new FacilioContext();
		//set Event
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.APPROVE_WORK_ORDER_REQUEST);
		return updateWorkOrderRequest(context);
	}
	
	public String rejectWorkOrderRequest() throws Exception {
		if (workorderrequest == null) {
			workorderrequest = new WorkOrderRequestContext();
		}
		workorderrequest.setRequestStatus(WorkOrderRequestContext.RequestStatus.REJECTED);
		FacilioContext context = new FacilioContext();
		//set Event
		return updateWorkOrderRequest(context);
	}
	
	public String updateWorkOrderRequest() throws Exception {
		
		FacilioContext context = new FacilioContext();
		return updateWorkOrderRequest(context);
	}
	
	private String updateWorkOrderRequest(FacilioContext context) throws Exception {
//		System.out.println(id);
//		System.out.println(workorderrequest);
		
		context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workorderrequest);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Chain updateWorkOrder = FacilioChainFactory.getUpdateWorkOrderRequestChain();
		updateWorkOrder.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}
	
	public String addWorkOrderRequest() throws Exception {
		
		System.out.println("######## attachedFile : "+attachedFiles);
		System.out.println("######## attachedFilesFileName : "+attachedFilesFileName);
		System.out.println("######## workorderrequest : "+workorder_request);
		if (workorder_request != null) {
			setWorkorderrequest(workorder_request);
		}
		workorderrequest.setRequestStatus(WorkOrderRequestContext.RequestStatus.OPEN);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REQUESTER, workorderrequest.getRequester());
		context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workorderrequest);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
// 		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderRequestChain();
		addWorkOrder.execute(context);
		setWorkOrderRequestId(workorderrequest.getId());
		return SUCCESS;
	}
	
	private List<File> attachedFiles;
	private List<String> attachedFilesFileName;
	private List<String> attachedFilesContentType;
	
	public List<File> getAttachedFiles() {
		return attachedFiles;
	}
	public void setAttachedFiles(List<File> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}
	public List<String> getAttachedFilesFileName() {
		return attachedFilesFileName;
	}
	public void setAttachedFilesFileName(List<String> attachedFilesFileName) {
		this.attachedFilesFileName = attachedFilesFileName;
	}
	public List<String> getAttachedFilesContentType() {
		return attachedFilesContentType;
	}
	public void setAttachedFilesContentType(List<String> attachedFilesContentType) {
		this.attachedFilesContentType = attachedFilesContentType;
	}
	
	
//	private File attachedFile;
//	public String attachedFileFileName;
//	public String attachedFileContentType;
//	
//	
//	public File getAttachedFile() {
//		return attachedFile;
//	}
//	public void setAttachedFile(File attachedFile) {
//		this.attachedFile = attachedFile;
//	}
//	
//	public String getAttachedFileFileName() {
//		return attachedFileFileName;
//	}
//	public void setAttachedFileFileName(String attachedFileFileName) {
//		this.attachedFileFileName = attachedFileFileName;
//	}
//	public String getAttachedFileContentType() {
//		return attachedFileContentType;
//	}
//	public void setAttachedFileContentType(String attachedFileContentType) {
//		this.attachedFileContentType = attachedFileContentType;
//	}

	private WorkOrderRequestContext workorderrequest;
	public WorkOrderRequestContext getWorkorderrequest() {
		return workorderrequest;
	}
	public void setWorkorderrequest(WorkOrderRequestContext workorderrequest) {
		this.workorderrequest = workorderrequest;
	}
	public void setWorkorderrequest(String workorder_request) {
		this.workorderrequest = convert(workorder_request);
	}

	private String workorder_request;
	public String getWorkorder_request() {
		return workorder_request;
	}
	public void setWorkorder_request(String workorder_request) {
		this.workorder_request = workorder_request;
	}
	
	public WorkOrderRequestContext convert(String workOrderReqStr)
	{
		WorkOrderRequestContext wo = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(workOrderReqStr);
			wo = FieldUtil.getAsBeanFromJson(obj, WorkOrderRequestContext.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wo;
	}
	
	private long workOrderRequestId;
	public long getWorkOrderRequestId() {
		return workOrderRequestId;
	}
	public void setWorkOrderRequestId(long workOrderRequestId) {
		this.workOrderRequestId = workOrderRequestId;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}

	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	
	public String viewWorkOrderRequest() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getWorkOrderRequestId());
		
		Chain getWorkOrderChain = FacilioChainFactory.getWorkOrderRequestDetailsChain();
		getWorkOrderChain.execute(context);
		
		setWorkorderrequest((WorkOrderRequestContext) context.get(FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		
		return SUCCESS;
	}
	
	public String workOrderRequestList() throws Exception {
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
 			searchObj.put("fields", "workorderrequest.subject,workorderrequest.description");
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
 		Chain workOrderListChain = FacilioChainFactory.getWorkOrderRequestListChain();
 		workOrderListChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setWorkOrderRequests((List<WorkOrderRequestContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_REQUEST_LIST));
		
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		
		return SUCCESS;
	}
	
	private List<WorkOrderRequestContext> workOrderRequests;
	public List<WorkOrderRequestContext> getWorkOrderRequests() {
		return workOrderRequests;
	}
	public void setWorkOrderRequests(List<WorkOrderRequestContext> workOrderRequests) {
		this.workOrderRequests = workOrderRequests;
	}
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.WORK_ORDER_REQUEST;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewWorkOrderLayout();
	}
	
	private String displayName = "All Work Order Requests";
	public String getViewDisplayName() {
		return displayName;
	}
	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}

	String filters;
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
}
