package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class TenantValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            TenantContext tenant = PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId());
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
    public Object getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.TENANT;
    }

    @Override
    public Object getLinkName() {
        return "com.facilio.modules.TenantValueGenerator";
    }

    @Override
    public Object getModuleName() {
        return FacilioConstants.ContextNames.TENANT;
    }

    @Override
    public Object getIsHidden() {
        return false;
    }

    @Override
    public Object getOperatorId() {
        return 36;
    }
}
