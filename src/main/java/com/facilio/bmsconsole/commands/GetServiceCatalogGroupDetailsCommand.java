package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ServiceCatalogGroupContext;
import com.facilio.bmsconsole.util.ServiceCatalogApi;
import com.facilio.constants.FacilioConstants;

public class GetServiceCatalogGroupDetailsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if (id > 0) {
            ServiceCatalogGroupContext serviceCatalogGroup = ServiceCatalogApi.getCategoryDetails(id);
            context.put(FacilioConstants.ContextNames.SERVICE_CATALOG_GROUP, serviceCatalogGroup);
        }
        return false;
    }
}
