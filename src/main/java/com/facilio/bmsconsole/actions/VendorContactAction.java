package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class VendorContactAction extends FacilioAction{

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

	private VendorContactContext vendorContact;
	private List<VendorContactContext> vendorContacts;
	
	public VendorContactContext getVendorContact() {
		return vendorContact;
	}
	public void setVendorContact(VendorContactContext vendorContact) {
		this.vendorContact = vendorContact;
	}
	public List<VendorContactContext> getVendorContacts() {
		return vendorContacts;
	}
	public void setVendorContacts(List<VendorContactContext> vendorContacts) {
		this.vendorContacts = vendorContacts;
	}
	public List<Long> getVendorContactIds() {
		return vendorContactIds;
	}
	public void setVendorContactIds(List<Long> vendorContactIds) {
		this.vendorContactIds = vendorContactIds;
	}

	private List<Long> vendorContactIds;
	
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
	
	private long appId;
	
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public String addVendorContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(vendorContacts)) {
			FacilioChain c = TransactionChainFactory.addVendorContactChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, vendorContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.VENDOR_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateVendorContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(vendorContacts)) {
			FacilioChain c = TransactionChainFactory.updateVendorContactChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, vendorContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.VENDOR_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteVendorContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(vendorContactIds)) {
			FacilioChain c = FacilioChainFactory.deleteVendorContactChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, vendorContactIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String vendorContactList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVendorContactListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "vendorcontact");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "People.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "vendorcontact.name");
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
			List<VendorContactContext> vendorContactList = (List<VendorContactContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.VENDOR_CONTACTS, vendorContactList);
		}
		
		return SUCCESS;
	}
	
	public String getVendorContactDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getVendorContactDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		VendorContactContext vendorContact = (VendorContactContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.VENDOR_CONTACT, vendorContact);
		
		return SUCCESS;
	}
	
	public String updateVendorPortalAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(vendorContacts)) {
			FacilioChain c = TransactionChainFactory.updateVendorContactAppAccessChain();
			//1 - Vendor portal
			c.getContext().put(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR, 1);
			c.getContext().put(FacilioConstants.ContextNames.APP_ID, getAppId());
			
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, vendorContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.VENDOR_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	
}
