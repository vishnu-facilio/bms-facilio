package com.facilio.bmsconsoleV3.context.communityfeatures;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;

public class AdminDocumentsSharingContext extends CommunitySharingInfoContext {

    private AdminDocumentsContext adminDocument;

    public AdminDocumentsContext getAdminDocument() {
        return adminDocument;
    }

    public void setAdminDocument(AdminDocumentsContext adminDocument) {
        this.adminDocument = adminDocument;
    }
}
