package com.facilio.bmsconsole.context;

import java.util.List;

public class FeedbackKioskContext extends VisitorKioskContext {

    private List<ServiceCatalogContext> serviceCatalogs;
    public List<ServiceCatalogContext> getServiceCatalogs() {
        return serviceCatalogs;
    }
    public void setServiceCatalogs(List<ServiceCatalogContext> serviceCatalogs) {
        this.serviceCatalogs = serviceCatalogs;
    }
}
