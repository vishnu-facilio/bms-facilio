package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class V3ClientContactContext extends V3PeopleContext {
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

    public Boolean isClientPortalAccess() {
        if (isClientPortalAccess != null) {
            return isClientPortalAccess.booleanValue();
        }
        return false;
    }

    private V3ClientContext client;

    public V3ClientContext getClient() {
		return client;
	}

	public void setClient(V3ClientContext client) {
		this.client = client;
	}

	private Boolean isPrimaryContact;

    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public Boolean isPrimaryContact() {
        if (isPrimaryContact != null) {
            return isPrimaryContact.booleanValue();
        }
        return false;
    }

}
