package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class UpdateUserStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand (Context context) throws Exception {
        Boolean userStatus = (Boolean) context.get(FacilioConstants.ContextNames.USER_STATUS);
        FacilioUtil.throwIllegalArgumentException(userStatus == null, "User status cannot be null for status change");
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        boolean result = false;
        if(userStatus) {
            result = AccountUtil.getUserBean().enableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
        }
        else {
            result = AccountUtil.getUserBean().disableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
        }
        PeopleAPI.updatePeopleOnUserStatusChange(user,userStatus);
        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }
}
