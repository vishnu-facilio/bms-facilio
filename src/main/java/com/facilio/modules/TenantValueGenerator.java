package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class TenantValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
            if (tenant != null) {
                return String.valueOf(tenant.getId());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.TENANT;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TenantValueGenerator";
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
