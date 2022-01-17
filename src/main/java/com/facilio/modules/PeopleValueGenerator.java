package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class PeopleValueGenerator extends ValueGenerator {
    @Override
    public String generateValueForCondition(int appType) {
        try {
            Long pplId = V3PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
            return String.valueOf(pplId);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.PEOPLE;
    }

    @Override
    public Object getLinkName() {
        return "com.facilio.modules.PeopleValueGenerator";
    }

    @Override
    public Object getModuleName() {
        return FacilioConstants.ContextNames.PEOPLE;
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
