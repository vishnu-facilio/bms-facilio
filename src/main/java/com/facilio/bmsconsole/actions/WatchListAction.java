package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class WatchListAction extends FacilioAction{

private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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

	private WatchListContext watchList;
	private List<WatchListContext> watchListRecords;
	
	private List<Long> watchListIds;
	
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

	public WatchListContext getWatchList() {
		return watchList;
	}
	public void setWatchList(WatchListContext watchList) {
		this.watchList = watchList;
	}
	public List<WatchListContext> getWatchListRecords() {
		return watchListRecords;
	}
	public void setWatchListRecords(List<WatchListContext> watchListRecords) {
		this.watchListRecords = watchListRecords;
	}
	public List<Long> getWatchListIds() {
		return watchListIds;
	}
	public void setWatchListIds(List<Long> watchListIds) {
		this.watchListIds = watchListIds;
	}
	public String addWatchLists() throws Exception {
		
		if(!CollectionUtils.isEmpty(watchListRecords)) {
			FacilioChain c = TransactionChainFactory.addWatchListChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, watchListRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.WATCHLIST_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateWatchLists() throws Exception {
		
		if(!CollectionUtils.isEmpty(watchListRecords)) {
			FacilioChain c = TransactionChainFactory.updateWatchListChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, watchListRecords);
			c.execute();
			setResult(FacilioConstants.ContextNames.WATCHLIST_RECORDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteWatchLists() throws Exception {
		
		if(!CollectionUtils.isEmpty(watchListIds)) {
			FacilioChain c = FacilioChainFactory.deleteWatchListChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, watchListIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getWatchListRecordList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getWatchListRecordsChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "watchlist");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "WatchList.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "watchlist.phone");
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
			List<WatchListContext> watchListRecords = (List<WatchListContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.WATCHLIST_RECORDS, watchListRecords);
		}
		
		return SUCCESS;
	}
	
	public String getWatchListRecordDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getInsuranceDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		WatchListContext watchList = (WatchListContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.WATCHLIST, watchList);
		
		return SUCCESS;
	}
}
