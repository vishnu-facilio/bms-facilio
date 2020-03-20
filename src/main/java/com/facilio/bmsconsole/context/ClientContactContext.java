package com.facilio.bmsconsole.context;

public class ClientContactContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean isClientPortalAccess;

	public Boolean getIsClientPortalAccess() {
		return isClientPortalAccess;
	}

	public void setIsClientPortalAccess(Boolean clientPortalAccess) {
		this.isClientPortalAccess = clientPortalAccess;
	}

	public boolean isClientPortalAccess() {
		if (isClientPortalAccess != null) {
			return isClientPortalAccess.booleanValue();
		}
		return false;
	}
	
	private ClientContext client;

	public ClientContext getClient() {
		return client;
	}

	public void setClient(ClientContext client) {
		this.client = client;
	}
	
	private Boolean isPrimaryContact;

	public Boolean getIsPrimaryContact() {
		return isPrimaryContact;
	}

	public void setIsPrimaryContact(Boolean isPrimaryContact) {
		this.isPrimaryContact = isPrimaryContact;
	}

	public boolean isPrimaryContact() {
		if (isPrimaryContact != null) {
			return isPrimaryContact.booleanValue();
		}
		return false;
	}
	

}
