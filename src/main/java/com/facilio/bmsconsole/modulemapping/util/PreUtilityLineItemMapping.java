package com.facilio.bmsconsole.modulemapping.util;

import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.MultiFieldMapping;
import com.facilio.constants.FacilioConstants;

public class PreUtilityLineItemMapping {

    public static ModuleMappingContext preUtilityLineItemsToUtilityLineItems() {
        return new ModuleMappings().addModuleMapping("preUtilityLineItemsToUtilityLineItemMapping", "Pre Utility Line Items To Utility Line Items", FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS, true)

                ;
    }
}
