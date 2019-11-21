package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.mysql.fabric.xmlrpc.base.Array;

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
	private String triggerString;
	
	public String getTriggerString() {
		return triggerString;
	}
	public void setTriggerString(String triggerString) {
		this.triggerString = triggerString;
	}
	public String addVisitorLogging() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorLoggingRecords)) {
			FacilioChain c = TransactionChainFactory.addVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String addRecurringVisitorLogging() throws Exception {
		
		if(parentLog != null) {
			FacilioChain c = TransactionChainFactory.addRecurringVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			
			if(triggerString != null) {
				setTriggerContext(triggerString);
				parentLog.setTrigger(this.getTrigger());
				
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(parentLog));
			
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateRecurringVisitorLogging() throws Exception {
		
		if(parentLog != null) {
			FacilioChain c = TransactionChainFactory.updateRecurringVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			
			if(triggerString != null) {
				setTriggerContext(triggerString);
				parentLog.setTrigger(this.getTrigger());
				
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(parentLog));
			
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
			FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
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
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Logging.SYS_CREATED_TIME desc");
 		
		String filters = getFilters();
		JSONObject json = new JSONObject();
		JSONObject status = new JSONObject();
		
		FacilioStatus upcomingStatus = VisitorManagementAPI.getLogStatus("CheckedIn");
		
		JSONArray parentLogIdArray = new JSONArray();
		parentLogIdArray.add(String.valueOf(upcomingStatus.getId()));
		
		status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
		status.put("value", parentLogIdArray);
		
		
		if(StringUtils.isNotEmpty(filters)) {
			JSONParser parser = new JSONParser();
	 		json = (JSONObject) parser.parse(getFilters());
	 	}
		json.put("moduleState", status);
		
		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		
		
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
	
	public String getVisitorInviteList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorLoggingListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorlogging");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Logging.SYS_CREATED_TIME desc");
		
		String filters = getFilters();
		JSONObject json = new JSONObject();
		JSONObject isPreregistered = new JSONObject();
		JSONObject status = new JSONObject();
		
		JSONArray array = new JSONArray();
		array.add("true");
		//FacilioStatus upcomingStatus = VisitorManagementAPI.getLogStatus("Upcoming");
		
		//JSONArray parentLogIdArray = new JSONArray();
		//parentLogIdArray.add(String.valueOf(upcomingStatus.getId()));
		
		isPreregistered.put("operatorId", (long) BooleanOperators.IS.getOperatorId());
		isPreregistered.put("value", array);
		
		//status.put("operatorId", (long) PickListOperators.IS.getOperatorId());
		//status.put("value", parentLogIdArray);
		
		
		if(StringUtils.isNotEmpty(filters)) {
			JSONParser parser = new JSONParser();
	 		json = (JSONObject) parser.parse(getFilters());
	 	}
		json.put("isPreregistered", isPreregistered);
		//json.put("moduleState", status);
		
		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		
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
	
	public String qrScanLog() throws Exception {
		
		FacilioChain c = ReadOnlyChainFactory.qrScanVisitorLogChain();
		c.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		c.execute();
		setResult(FacilioConstants.ContextNames.VISITOR_LOGGING, c.getContext().get(FacilioConstants.ContextNames.VISITOR_LOGGING));
		return SUCCESS;
	}

}
