package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ClientContactAction extends FacilioAction{

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

	private ClientContactContext clientContact;
	private List<ClientContactContext> clientContacts;
	
	public ClientContactContext getClientContact() {
		return clientContact;
	}
	public void setClientContact(ClientContactContext clientContact) {
		this.clientContact = clientContact;
	}
	public List<ClientContactContext> getClientContacts() {
		return clientContacts;
	}
	public void setClientContacts(List<ClientContactContext> clientContacts) {
		this.clientContacts = clientContacts;
	}

	private List<Long> clientContactIds;
	
	public List<Long> getClientContactIds() {
		return clientContactIds;
	}
	public void setClientContactIds(List<Long> clientContactIds) {
		this.clientContactIds = clientContactIds;
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
	
	private long clientPortalAppId;
	
	public long getClientPortalAppId() {
		return clientPortalAppId;
	}
	public void setClientPortalAppId(long clientPortalAppId) {
		this.clientPortalAppId = clientPortalAppId;
	}
	public String addClientContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(clientContacts)) {
			FacilioChain c = TransactionChainFactory.addClientContactChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			c.getContext().put(FacilioConstants.ContextNames.CLIENT_PORTAL_APP_ID, getClientPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			for(ClientContactContext cc : clientContacts) {
				cc.parseFormData();
				RecordAPI.handleCustomLookup(cc.getData(), FacilioConstants.ContextNames.CLIENT_CONTACT);
			}
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clientContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.CLIENT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateClientContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(clientContacts)) {
			FacilioChain c = TransactionChainFactory.updateClientContactChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.CLIENT_PORTAL_APP_ID, getClientPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			for(ClientContactContext cc : clientContacts) {
				cc.parseFormData();
				RecordAPI.handleCustomLookup(cc.getData(), FacilioConstants.ContextNames.CLIENT_CONTACT);
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clientContacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.CLIENT_CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteClientContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(clientContactIds)) {
			FacilioChain c = FacilioChainFactory.deleteClientContactChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, clientContactIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String clientContactList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getClientContactListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "clientcontact");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Client_Contacts.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "clientcontact.name");
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
			List<ClientContactContext> clientContactList = (List<ClientContactContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.CLIENT_CONTACTS, clientContactList);
		}
		
		return SUCCESS;
	}
	
	public String getClientContactDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getClientContactDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		ClientContactContext clientContact = (ClientContactContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.CLIENT_CONTACT, clientContact);
		
		return SUCCESS;
	}
	
	public String updateClientAppAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(clientContacts)) {
			FacilioChain c = TransactionChainFactory.updateClientContactAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, clientContacts);
			c.getContext().put(FacilioConstants.ContextNames.CLIENT_PORTAL_APP_ID, getClientPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.CLIENT_CONTACT, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	
	
}
