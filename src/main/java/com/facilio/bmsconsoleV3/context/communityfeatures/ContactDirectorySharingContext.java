package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class ContactDirectorySharingContext extends CommunitySharingInfoContext {

    private ContactDirectoryContext contactDirectory;

    public ContactDirectoryContext getContactDirectory() {
        return contactDirectory;
    }

    public void setContactDirectory(ContactDirectoryContext contactDirectory) {
        this.contactDirectory = contactDirectory;
    }

}
