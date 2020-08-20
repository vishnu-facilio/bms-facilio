package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.workflowv2.contexts.Value;

public class PeopleValueGenerator extends ValueGenerator {
    @Override
    public String generateValueForCondition(long moduleId, int appType) {
        try {
            Long pplId = V3PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
            return String.valueOf(pplId);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
