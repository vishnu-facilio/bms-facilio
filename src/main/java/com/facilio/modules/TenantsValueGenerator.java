package com.facilio.modules;

import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

public class TenantsValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            List<V3TenantContext> tenants = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT, null, V3TenantContext.class, null);
            if(CollectionUtils.isNotEmpty(tenants)) {
                List<Long> tenantIds = tenants.stream().map(V3TenantContext::getId).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(tenantIds)) {
                    return StringUtils.join(tenantIds,',');
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
        return "All accessible tenants value generator";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TenantsValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TENANT;
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
