package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class CurrentUserValueGenerator extends ValueGenerator{

    @Override
    public String generateValueForCondition(int appType) {
        try {
            if(AccountUtil.getCurrentUser() != null) {
                return String.valueOf(AccountUtil.getCurrentUser().getId());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.CURRENT_USER;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.CurrentUserValueGenerator";
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
