package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.HazardContext;
import com.facilio.bmsconsole.context.PrecautionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class PrecautionAction extends FacilioAction{

	
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
	
	private PrecautionContext precaution;
	private List<PrecautionContext> precautions;
	private List<Long> precautionIds;
	
	public PrecautionContext getPrecaution() {
		return precaution;
	}
	public void setPrecaution(PrecautionContext precaution) {
		this.precaution = precaution;
	}
	public List<PrecautionContext> getPrecautions() {
		return precautions;
	}
	public void setPrecautions(List<PrecautionContext> precautions) {
		this.precautions = precautions;
	}
	public List<Long> getPrecautionIds() {
		return precautionIds;
	}
	public void setPrecautionIds(List<Long> precautionIds) {
		this.precautionIds = precautionIds;
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

	public String addPrecautions() throws Exception {
		
		FacilioChain c = TransactionChainFactory.addPrecautionChain();
		c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, precautions);
		c.execute();
		setResult(FacilioConstants.ContextNames.PRECAUTIONS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	
	public String updatePrecautions() throws Exception {
		
		FacilioChain c = TransactionChainFactory.updatePrecautionChain();
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, precautions);
		c.execute();
		setResult(FacilioConstants.ContextNames.PRECAUTIONS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}

	public String deletePrecaution() throws Exception {
		
		if(!CollectionUtils.isEmpty(precautionIds)) {
			FacilioChain c = FacilioChainFactory.deletePrecautionChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, precautionIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getPrecautionList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getPrecautionListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "precaution");
		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Precaution.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "precaution.name");
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
			List<PrecautionContext> precautions = (List<PrecautionContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.PRECAUTIONS, precautions);
		}
		
		return SUCCESS;
	}
	
	public String getPrecautionDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getPrecautionDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		PrecautionContext precautionContext = (PrecautionContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PRECAUTION, precautionContext);
		
		return SUCCESS;
	}

}
