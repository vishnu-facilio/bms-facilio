package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.v3.context.V3Context;

public class AdminDocumentsSharingContext extends V3Context {

    private AdminDocumentsContext adminDocument;

    public AdminDocumentsContext getAdminDocument() {
        return adminDocument;
    }

    public void setAdminDocument(AdminDocumentsContext adminDocument) {
        this.adminDocument = adminDocument;
    }
}
