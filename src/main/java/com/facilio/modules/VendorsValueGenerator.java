package com.facilio.modules;

import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

public class VendorsValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            List<V3VendorContext> vendors = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.VENDORS, null, V3VendorContext.class, null);
            if(CollectionUtils.isNotEmpty(vendors)) {
                List<Long> vendorIds = vendors.stream().map(V3VendorContext::getId).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(vendorIds)) {
                    return StringUtils.join(vendorIds,',');
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "All vendors value generator";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.VendorsValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.VENDORS;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }
}
