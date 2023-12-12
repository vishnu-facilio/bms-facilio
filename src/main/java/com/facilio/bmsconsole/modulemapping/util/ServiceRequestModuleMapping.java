package com.facilio.bmsconsole.modulemapping.util;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.MultiFieldMapping;
import com.facilio.constants.FacilioConstants;

import java.util.*;

public class ServiceRequestModuleMapping {

    public static ModuleMappingContext serviceRequestToWoTemplate() {
        return new ModuleMappings().addModuleMapping("sr", "saf", "workOrder", true)
                .addFieldMapping("siteId", "siteId")
                .addFieldMapping("subject", "subject")
                .addFieldMapping("description", "description")
                .addFieldMapping("urgency", "priority", lookuppicklistTolookuppicklist())
                .addFieldMapping("id", "serviceRequest")
                ;
    }

    // Lookup picklist to lookup picklist
    private static MultiFieldMapping lookuppicklistTolookuppicklist() {
        //service request urgency field to workorder category field
        MultiFieldMapping mapping = new MultiFieldMapping();
        mapping.add("Emergency", "High");
        mapping.add("Not Urgent", "Low");
        mapping.add("Urgent",  "Medium");
        return mapping;
    }

}
