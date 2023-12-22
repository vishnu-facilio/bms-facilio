package com.facilio.bmsconsole.modulemapping.util;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.MultiFieldMapping;
import com.facilio.constants.FacilioConstants;

public class WorkOrderModuleMapping {

    public static ModuleMappingContext workOrderToInvoiceConversion() {
        return new ModuleMappings().addModuleMapping("workOrderToInvoiceConversion", "WorkOrder To Invoice Conversion", "invoice", true)
                .addFieldMapping("subject","subject")
                .addFieldMapping("siteId","siteId")
                .addFieldMapping("vendor","vendor")
                .addFieldMapping("tenant","tenant")
                .addFieldMapping("client","client")
                .addFieldMapping("id","workorder")
                ;
    }
    public static ModuleMappingContext workOrderToQuoteConversion() {
        return new ModuleMappings().addModuleMapping("workOrderToQuoteConversion", "WorkOrder To Quote Conversion", "quote", true)
                .addFieldMapping("subject","subject")
                .addFieldMapping("siteId","siteId")
                .addFieldMapping("vendor","vendor")
                .addFieldMapping("tenant","tenant")
                .addFieldMapping("client","client")
                .addFieldMapping("id","workorder")
                ;
    }

}
