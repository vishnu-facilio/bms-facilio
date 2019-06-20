package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class NotificationConfigAction extends FacilioAction {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private NotificationConfigContext notificationConfig;

	public NotificationConfigContext getNotificationConfig() {
		return notificationConfig;
	}

	public void setNotificationConfig(NotificationConfigContext notificationConfig) {
		this.notificationConfig = notificationConfig;
	}

	private List<NotificationConfigContext> notificationConfigs;
	
	public List<NotificationConfigContext> getNotificationConfigs() {
		return notificationConfigs;
	}

	public void setNotificationConfigs(List<NotificationConfigContext> notificationConfigs) {
		this.notificationConfigs = notificationConfigs;
	}

	private long jobId;
	
	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
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
	
	private long parentId;
	private String moduleName;
	
	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long recordId;
	

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private List<Long> recordIds;
	
	public List<Long> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}

	public String addOrUpdateConfiguration() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, notificationConfigs);

		Chain chain = TransactionChainFactory.getAddNotificationConfigChain();
		chain.execute(context);

		setResult("Success", "success");

		return SUCCESS;
	}

	public String executeJob() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.NOTIFICATION_JOB_ID, jobId);

		Chain chain = TransactionChainFactory.triggerNotificationChain();
		chain.execute(context);

		setResult("Success", "success");

		return SUCCESS;
	}
	
	public String listConfiguration() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "notificationConfig");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Notification_Configurations.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "notificationConfig.id");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	
		Chain chain = ReadOnlyChainFactory.getNotificationConfigList();
		chain.execute(context);

		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<NotificationConfigContext> configList = (List<NotificationConfigContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.NOTIFICATION_CONFIG, configList);
		}
		return SUCCESS;
	}
	
	public String deleteConfiguration() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		Chain chain = TransactionChainFactory.getPurchaseRequestDeleteChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordId != -1 ? Collections.singletonList(recordId) : recordIds);
		return SUCCESS;
	}
	
}
