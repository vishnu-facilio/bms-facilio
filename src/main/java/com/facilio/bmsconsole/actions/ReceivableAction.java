package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ReceivableAction extends FacilioAction {
	private static final long serialVersionUID = 1L;

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long receivableId = -1;
	
	public long getReceivableId() {
		return receivableId;
	}
	public void setReceivableId(long receivableId) {
		this.receivableId = receivableId;
	}
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
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
	
	private long poId = -1;
	
	private List<ReceivableContext> receivables;
	
	
	public List<ReceivableContext> getReceivables() {
		return receivables;
	}
	public void setReceivables(List<ReceivableContext> receivables) {
		this.receivables = receivables;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	public String getReceivableList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "receivable");
 		
 		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Receivables.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 	 	
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "receivable.name");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		  setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		
		return SUCCESS;
	}
	
	public String getReceivableByPoId() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PO_ID,poId );
		
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String getReceivableById() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, receivableId);
		
		Chain chain = ReadOnlyChainFactory.getAllReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String addOrUpdateReceivables() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_LIST, receivables);
		
		Chain chain = TransactionChainFactory.getAddOrUpdateReceivablesChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECEIVABLES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	public String receivableCount() throws Exception {
		return getReceivableList();
	}
	
}
