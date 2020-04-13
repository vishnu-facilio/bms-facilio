package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class InsuranceAction extends FacilioAction{

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

	private InsuranceContext insurance;
	private List<InsuranceContext> insurances;
	
	private List<Long> insuranceIds;
	
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

	public InsuranceContext getInsurance() {
		return insurance;
	}
	public void setInsurance(InsuranceContext insurance) {
		this.insurance = insurance;
	}
	public List<InsuranceContext> getInsurances() {
		return insurances;
	}
	public void setInsurances(List<InsuranceContext> insurances) {
		this.insurances = insurances;
	}
	public List<Long> getInsuranceIds() {
		return insuranceIds;
	}
	public void setInsuranceIds(List<Long> insuranceIds) {
		this.insuranceIds = insuranceIds;
	}
	public String addInsurances() throws Exception {
		
		if(!CollectionUtils.isEmpty(insurances)) {
			FacilioChain c = TransactionChainFactory.addInsurancesChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, insurances);
			c.execute();
			setResult(FacilioConstants.ContextNames.INSURANCES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateInsurances() throws Exception {
		
		if(!CollectionUtils.isEmpty(insurances)) {
			List<Long> recordIds = new ArrayList<Long>();
			for(InsuranceContext insurance : insurances) {
				recordIds.add(insurance.getId());
			}
			FacilioChain c = TransactionChainFactory.updateInsurancesChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, insurances);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
			c.execute();
			setResult(FacilioConstants.ContextNames.INSURANCES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	private long stateTransitionId = -1;
	public long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	public String deleteInsurances() throws Exception {
		
		if(!CollectionUtils.isEmpty(insuranceIds)) {
			FacilioChain c = FacilioChainFactory.deleteInsuranceChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, insuranceIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getInsuranceList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getInsuranceListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "insurance");
		chain.getContext().put(FacilioConstants.ContextNames.IS_VENDOR_PORTAL, getVendorPortal());
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Insurance.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "insurance.vendor");
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
			List<InsuranceContext> insurances = (List<InsuranceContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.INSURANCES, insurances);
		}
		
		return SUCCESS;
	}
	
	public String getInsuranceDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getInsuranceDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		InsuranceContext insurance = (InsuranceContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.INSURANCE, insurance);
		
		return SUCCESS;
	}
}
