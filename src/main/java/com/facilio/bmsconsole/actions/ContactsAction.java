package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ContactsAction extends FacilioAction{

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

	private ContactsContext contact;
	private List<ContactsContext> contacts;
	
	private List<Long> contactIds;
	
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

	public ContactsContext getContact() {
		return contact;
	}
	public void setContact(ContactsContext contact) {
		this.contact = contact;
	}
	public List<ContactsContext> getContacts() {
		return contacts;
	}
	public void setContacts(List<ContactsContext> contacts) {
		this.contacts = contacts;
	}
	public List<Long> getContactIds() {
		return contactIds;
	}
	public void setContactIds(List<Long> contactIds) {
		this.contactIds = contactIds;
	}
	public String addContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(contacts)) {
			FacilioChain c = TransactionChainFactory.addContactsChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, contacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(contacts)) {
			FacilioChain c = TransactionChainFactory.updateContactsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, contacts);
			c.execute();
			setResult(FacilioConstants.ContextNames.CONTACTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteContacts() throws Exception {
		
		if(!CollectionUtils.isEmpty(contactIds)) {
			FacilioChain c = FacilioChainFactory.deleteContactsChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, contactIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getContactsList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getContactsListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "contact");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Contacts.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "contact.tenant");
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
			List<ContactsContext> contacts = (List<ContactsContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.CONTACTS, contacts);
		}
		
		return SUCCESS;
	}
	
	public String getContactDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getContactDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		ContactsContext contact = (ContactsContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.CONTACT, contact);
		
		return SUCCESS;
	}
	
	public String updatePortalAccess() throws Exception {
		ContactsAPI.updatePortalUserAccess(contact);
		return SUCCESS;
	}
	
	public String markAsPrimaryContact() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.CONTACT, contact);
			FacilioChain updatePrimaryContact = FacilioChainFactory.updatePrimaryContactChain();
			updatePrimaryContact.execute(context);
			setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
			return SUCCESS;
		}
		catch (Exception e) {
			setResult("error",e.getMessage());
			return ERROR;
		}
	}

	
}
