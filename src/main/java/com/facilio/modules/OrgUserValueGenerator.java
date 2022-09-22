package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class OrgUserValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if(AccountUtil.getCurrentUser() != null) {
                return String.valueOf(AccountUtil.getCurrentUser().getOuid());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.CURRENT_ORG_USER_USER;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.OrgUserValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.USERS;
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
