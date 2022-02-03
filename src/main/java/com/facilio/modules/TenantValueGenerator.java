package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;

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
}
