package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetApplicationUserDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        Long ouId = (Long)context.get(FacilioConstants.ContextNames.ORG_USER_ID);

        if(appId == null || appId <= 0) {
            throw new IllegalArgumentException("Invalid app id");
        }

        if(ouId == null || ouId <= 0) {
            throw new IllegalArgumentException("Invalid org user id");
        }

        User user = AccountUtil.getOrgBean().getAppUser(AccountUtil.getCurrentOrg().getOrgId(), appId, ouId);
        context.put(FacilioConstants.ContextNames.USER, user);
        return false;
    }
}
