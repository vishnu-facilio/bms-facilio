package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;

public class VisitorLoggingAction extends FacilioAction{

private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	private Boolean vendorPortal;
	public Boolean getVendorPortal() {
		if (vendorPortal == null) {
			return false;
		}
		return vendorPortal;
	}
	public void setVendorPortal(Boolean vendorPortal) {
		this.vendorPortal = vendorPortal;
	}
	
	private Boolean tenantPortal;
	public Boolean getTenantPortal() {
		if (tenantPortal == null) {
			return false;
		}
		return tenantPortal;
	}
	public void setTenantPortal(Boolean tenantPortal) {
		this.tenantPortal = tenantPortal;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	private VisitorLoggingContext visitorLogging;
	
	private List<VisitorLoggingContext> visitorLoggingRecords;
	
	public VisitorLoggingContext getVisitorLogging() {
		return visitorLogging;
	}
	public void setVisitorLogging(VisitorLoggingContext visitorLogging) {
		this.visitorLogging = visitorLogging;
	}
	public List<VisitorLoggingContext> getVisitorLoggingRecords() {
		return visitorLoggingRecords;
	}
	public void setVisitorLoggingRecords(List<VisitorLoggingContext> visitorLoggingRecords) {
		this.visitorLoggingRecords = visitorLoggingRecords;
	}
	public List<Long> getVisitorLoggingIds() {
		return visitorLoggingIds;
	}
	public void setVisitorLoggingIds(List<Long> visitorLoggingIds) {
		this.visitorLoggingIds = visitorLoggingIds;
	}

	private List<Long> visitorLoggingIds;
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private String passCode;
	
	public String getPassCode() {
		return passCode;
	}
	public void setPassCode(String passCode) {
		this.passCode = passCode;
	}
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}
	
	private String contactNumber;
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
	}
	
	private VisitorLoggingContext parentLog;
	
	public VisitorLoggingContext getParentLog() {
		return parentLog;
	}
	public void setParentLog(VisitorLoggingContext parentLog) {
		this.parentLog = parentLog;
	}
	
	private List<File> attachedFiles;
	private List<String> attachedFilesFileName;
	private List<String> attachedFilesContentType;
	private AttachmentType attachmentType;
	
	public int getAttachmentType() {
		if(attachmentType != null) {
			return attachmentType.getIntVal();
		}
		return -1;
	}
	public void setAttachmentType(int attachmentType) {
		this.attachmentType = AttachmentType.getType(attachmentType);
	}
	
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
	
	public String addVisitorLogging() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorLoggingRecords) || visitorLogging != null) {
			FacilioChain c = TransactionChainFactory.addVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			
			if (visitorLogging != null) {
				visitorLogging.parseFormData();
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitorLogging));
				c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, Collections.singletonList(visitorLogging));
				
			}else {
				parseFormData(visitorLoggingRecords);
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingRecords);
				c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingRecords);
				
			}
			c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
			c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
			c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
			c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
			c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.VISITOR_LOGGING_ATTACHMENTS);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	private void parseFormData(List<VisitorLoggingContext> logs) {
		if(CollectionUtils.isNotEmpty(logs)) {
			for(VisitorLoggingContext log : logs) {
				log.parseFormData();
			}
		}
	}
	public String addRecurringVisitorLogging() throws Exception {
		
		if(parentLog != null) {
			FacilioChain c = TransactionChainFactory.addRecurringVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			parentLog.parseFormData();
			if(parentLog.getTrigger() != null) {
				Date d1 = new Date(parentLog.getExpectedCheckInTime());
				Date d2 = new Date(parentLog.getExpectedCheckOutTime());
			    long diffMs = d2.getTime() - d1.getTime();
			    parentLog.setExpectedVisitDuration(diffMs);
				
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(parentLog));
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, Collections.singletonList(parentLog));
			
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateRecurringVisitorLogging() throws Exception {
		
		if(parentLog != null) {
			FacilioChain c = TransactionChainFactory.updateRecurringVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			parentLog.parseFormData();
			if(parentLog.getTrigger() != null) {
				Date d1 = new Date(parentLog.getExpectedCheckInTime());
				Date d2 = new Date(parentLog.getExpectedCheckOutTime());
			    long diffMs = d2.getTime() - d1.getTime();
			    parentLog.setExpectedVisitDuration(diffMs);
				
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(parentLog));
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, Collections.singletonList(parentLog));
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(parentLog.getId()));
			
			
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	private PMTriggerContext trigger;
	
	public PMTriggerContext getTrigger() {
		return trigger;
	}
	public void setTrigger(PMTriggerContext trigger) {
		this.trigger = trigger;
	}
	public void setTriggerContext(String triggerString) throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(triggerString);
		this.trigger = FieldUtil.getAsBeanFromJson(obj, PMTriggerContext.class);
	}
	public String updateVisitorLogging() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorLoggingRecords)) {
			parseFormData(visitorLoggingRecords);
			Boolean canCreateNewLog = VisitorManagementAPI.checkExistingVisitorLogging(visitorLoggingRecords.get(0).getId());
			if(canCreateNewLog != null) {
				if(canCreateNewLog) {
					addVisitorLogging();
				}
				else {
					throw new IllegalArgumentException("Invalid Re checkin");
				}
			}
			else {
				FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
				c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
				c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingRecords);
				c.getContext().put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingRecords);
				
				c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(visitorLoggingRecords.get(0).getId()));
				
				c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
				c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
				c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
				c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);
				c.getContext().put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.VISITOR_LOGGING_ATTACHMENTS);
				
				
				c.execute();
				setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
			}
		}
		return SUCCESS;
	}

	public String deleteVisitorLogging() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorLoggingIds)) {
			FacilioChain c = FacilioChainFactory.deleteVisitorLoggingChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, visitorLoggingIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getVisitorLoggingList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorLoggingListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorlogging");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
		chain.getContext().put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, getTenantPortal());
		chain.getContext().put("logType", 1);
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Logging.SYS_CREATED_TIME desc");
 		
		String filters = getFilters();
		

		if(StringUtils.isNotEmpty(filters)) {
			JSONObject json = new JSONObject();
			JSONParser parser = new JSONParser();
	 		json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 	}
		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		
		
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitorlogging.host");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		if(!getFetchCount()) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			chain.getContext().put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<VisitorLoggingContext> visitorLoggingRecords = (List<VisitorLoggingContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingRecords);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) chain.getContext().get("stateFlows"));
			setResult("stateFlows", getStateFlows());
		}
		
		return SUCCESS;
	}
	
	public String getVisitorInviteList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorLoggingListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorlogging");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
		chain.getContext().put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, getTenantPortal());
		chain.getContext().put("logType", 2);
		
			
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Logging.SYS_CREATED_TIME desc");
		
		String filters = getFilters();
		JSONObject json = new JSONObject();
		JSONObject isPreregistered = new JSONObject();
		JSONObject status = new JSONObject();
		
		JSONArray array = new JSONArray();
		array.add("true");
		
//		FacilioStatus upcomingStatus = VisitorManagementAPI.getLogStatus("Upcoming");
//		
//		JSONArray parentLogIdArray = new JSONArray();
//		parentLogIdArray.add(String.valueOf(upcomingStatus.getId()));
//		
		isPreregistered.put("operatorId", (long) BooleanOperators.IS.getOperatorId());
		isPreregistered.put("value", array);
		
//		status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
//		status.put("value", parentLogIdArray);
//		
		
		if(StringUtils.isNotEmpty(filters)) {
			JSONParser parser = new JSONParser();
	 		json = (JSONObject) parser.parse(getFilters());
	 	}
		json.put("isPreregistered", isPreregistered);
	//	json.put("moduleState", status);
		
		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
 		
		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitorlogging.host");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
		if(!getFetchCount()) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			chain.getContext().put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<VisitorLoggingContext> visitorLoggingRecords = (List<VisitorLoggingContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingRecords);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) chain.getContext().get("stateFlows"));
			setResult("stateFlows", getStateFlows());
		}
		
		return SUCCESS;
		
	}
	
	public String getVisitorInviteRequestList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorLoggingListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorlogging");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
		chain.getContext().put(FacilioConstants.ContextNames.IS_TENANT_PORTAL, getTenantPortal());
		chain.getContext().put("logType", 2);
			
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Logging.SYS_CREATED_TIME desc");
		
		String filters = getFilters();
		JSONObject json = new JSONObject();
		JSONObject isPreregistered = new JSONObject();
		JSONObject status = new JSONObject();
		
		JSONArray array = new JSONArray();
		array.add("true");
		
		FacilioStatus requestedStatus = VisitorManagementAPI.getLogStatus("InviteRequested");
		FacilioStatus rejectedStatus = VisitorManagementAPI.getLogStatus("InviteRejected");
		
		JSONArray possibleStatesIdArray = new JSONArray();
		possibleStatesIdArray.add(String.valueOf(requestedStatus.getId()));
		possibleStatesIdArray.add(String.valueOf(rejectedStatus.getId()));
		
		isPreregistered.put("operatorId", (long) BooleanOperators.IS.getOperatorId());
		isPreregistered.put("value", array);
		
		status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
		status.put("value", possibleStatesIdArray);
		
		
		if(StringUtils.isNotEmpty(filters)) {
			JSONParser parser = new JSONParser();
	 		json = (JSONObject) parser.parse(getFilters());
	 	}
		json.put("isPreregistered", isPreregistered);
		json.put("moduleState", status);
		
		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
 		
		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitorlogging.host");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<VisitorLoggingContext> visitorLoggingRecords = (List<VisitorLoggingContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingRecords);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) chain.getContext().get("stateFlows"));
			setResult("stateFlows", getStateFlows());
		}
		
		return SUCCESS;
		
	}
	
	public String getVisitorLoggingDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getVisitorLoggingDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		VisitorLoggingContext visitorLogging = (VisitorLoggingContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING, visitorLogging);
		
		
		return SUCCESS;
	}
	
	public String checkOutVisitorLogging() throws Exception {
		
		if(!StringUtils.isEmpty(contactNumber)) {
			FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
			VisitorManagementAPI.checkOutVisitorLogging(contactNumber, c.getContext());
			if(c.getContext().get("visitorLogging") != null) {
				c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, c.getContext().get("nextTransitionId"));
				VisitorLoggingContext visitorLoggingContext = (VisitorLoggingContext) c.getContext().get("visitorLogging");
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitorLoggingContext));
				c.execute();
			}
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String checkInCheckOutVisitorLogging() throws Exception {
		
		if(recordId > 0) {
			FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
			VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLoggingTriggers(recordId, null, false);
			if(vLog != null) {
				List<WorkflowRuleContext> nextStateRule = StateFlowRulesAPI.getAvailableState(vLog.getStateFlowId(), vLog.getModuleState().getId(), FacilioConstants.ContextNames.VISITOR_LOGGING, vLog, c.getContext());
				if(CollectionUtils.isNotEmpty(nextStateRule)) {
					vLog.setCheckInTime(System.currentTimeMillis());
					long nextTransitionId = nextStateRule.get(0).getId();
					c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, nextTransitionId);
					c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(vLog.getId()));
					c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(vLog));
					c.execute();
				}
				else {
					throw new IllegalArgumentException("Invalid log id");
				}
			}
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String qrScanLog() throws Exception {
		
		FacilioChain c = ReadOnlyChainFactory.qrScanVisitorLogChain();
		c.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		c.execute();
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING, c.getContext().get(FacilioConstants.ContextNames.VISITOR_LOGGING));
		setResult(FacilioConstants.ContextNames.TRANSITION_ID, c.getContext().get(FacilioConstants.ContextNames.TRANSITION_ID));
		return SUCCESS;
	}
	
	public String passCodeScanLog() throws Exception {
	
		FacilioChain c = ReadOnlyChainFactory.passCodeScanVisitorLogChain();
		c.getContext().put(FacilioConstants.ContextNames.PASSCODE, passCode);
		c.execute();
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING, c.getContext().get(FacilioConstants.ContextNames.VISITOR_LOGGING));
		setResult(FacilioConstants.ContextNames.TRANSITION_ID, c.getContext().get(FacilioConstants.ContextNames.TRANSITION_ID));
		
		return SUCCESS;
	}
	
}
