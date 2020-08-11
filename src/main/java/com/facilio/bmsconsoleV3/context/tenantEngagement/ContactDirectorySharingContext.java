package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.v3.context.V3Context;

public class ContactDirectorySharingContext extends V3Context {

    private ContactDirectoryContext contactDirectory;

    public ContactDirectoryContext getContactDirectory() {
        return contactDirectory;
    }

    public void setContactDirectory(ContactDirectoryContext contactDirectory) {
        this.contactDirectory = contactDirectory;
    }

}
