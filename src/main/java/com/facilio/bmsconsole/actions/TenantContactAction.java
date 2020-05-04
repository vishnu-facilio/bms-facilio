package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class TenantContactAction extends FacilioAction{

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

	private TenantContactContext tenantContact;
	private List<TenantContactContext> tenantContacts;
	
	
	public TenantContactContext getTenantContact() {
		return tenantContact;
	}
	public void setTenantContact(TenantContactContext tenantContact) {
		this.tenantContact = tenantContact;
	}
	public List<TenantContactContext> getTenantContacts() {
		return tenantContacts;
	}
	public void setTenantContacts(List<TenantContactContext> tenantContacts) {
		this.tenantContacts = tenantContacts;
	}
	public List<Long> getTenantContactIds() {
		return tenantContactIds;
	}
	public void setTenantContactIds(List<Long> tenantContactIds) {
		this.tenantContactIds = tenantContactIds;
	}

	private List<Long> tenantContactIds;
	
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
	
	public String addTenantContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(tenantContacts) || tenantContact != null) {
			FacilioChain c = TransactionChainFactory.addTenantContactChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			for(TenantContactContext tc : tenantContacts) {
				tc.parseFormData();
				RecordAPI.handleCustomLookup(tc.getData(), FacilioConstants.ContextNames.TENANT_CONTACT);
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, tenantContacts);
				
			c.execute();
			setResult(FacilioConstants.ContextNames.TENANT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateTenantContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(tenantContacts)) {
			
			FacilioChain c = TransactionChainFactory.updateTenantContactChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		
			for(TenantContactContext tc : tenantContacts) {
				tc.parseFormData();
				RecordAPI.handleCustomLookup(tc.getData(), FacilioConstants.ContextNames.TENANT_CONTACT);
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, tenantContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.TENANT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteTenantContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(tenantContactIds)) {
			FacilioChain c = FacilioChainFactory.deleteTenantContactChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, tenantContactIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String tenantContactList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getTenantContactListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "tenantcontact");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Tenant_Contacts.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "tenantcontact.name");
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
			List<TenantContactContext> tenantContactList = (List<TenantContactContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.TENANT_CONTACTS, tenantContactList);
		}
		
		return SUCCESS;
	}
	
	public String getTenantContactDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getTenantContactDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		TenantContactContext tenantContact = (TenantContactContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.TENANT_CONTACT, tenantContact);
		
		return SUCCESS;
	}
	
	public String updateTenantPortalAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(tenantContacts)) {
			FacilioChain c = TransactionChainFactory.updateTenantContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, tenantContacts);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.TENANT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	
	
	public String updateOccupantPortalAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(tenantContacts)) {
			FacilioChain c = TransactionChainFactory.updateTenantContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, tenantContacts);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.TENANT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	
	
	
}
