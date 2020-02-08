package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetHazardContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class AssetHazardAction extends FacilioAction{

	/**
	 * 
	 */
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
	
	private AssetHazardContext assetHazard;
	private List<AssetHazardContext> assetHazardList;
	private List<Long> assetHazardIds;
	
	public AssetHazardContext getAssetHazard() {
		return assetHazard;
	}
	public void setAssetHazard(AssetHazardContext assetHazard) {
		this.assetHazard = assetHazard;
	}
	public List<AssetHazardContext> getAssetHazardList() {
		return assetHazardList;
	}
	public void setAssetHazardList(List<AssetHazardContext> assetHazardList) {
		this.assetHazardList = assetHazardList;
	}
	public List<Long> getAssetHazardIds() {
		return assetHazardIds;
	}
	public void setAssetHazardIds(List<Long> assetHazardIds) {
		this.assetHazardIds = assetHazardIds;
	}
	
	public List<Long> getWorkOrderHazardIds() {
		return assetHazardIds;
	}
	public void setWorkOrderHazardIds(List<Long> workOrderHazardIds) {
		this.assetHazardIds = workOrderHazardIds;
	}

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
	
	public String addAssetHazardList() throws Exception {
		FacilioChain c = TransactionChainFactory.addAssetHazardListChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, assetHazardList);
		c.execute();
		setResult(FacilioConstants.ContextNames.ASSET_HAZARDS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	
	}
	
	public String deleteAssetHazardList() throws Exception {
		
		if(!CollectionUtils.isEmpty(assetHazardIds)) {
			FacilioChain c = FacilioChainFactory.deleteAssetHazardListChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, assetHazardIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	
	}
	
	public String assetHazardList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getAssetHazardListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "assetHazard");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Asset_Hazards.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "Asset_Hazards.hazard");
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
			List<AssetHazardContext> assetHazardList = (List<AssetHazardContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.ASSET_HAZARDS, assetHazardList);
		}
		
		return SUCCESS;
	}
	
	
}
