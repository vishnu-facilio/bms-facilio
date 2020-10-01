package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class UpdateUserStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand (Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);

        FacilioUtil.throwIllegalArgumentException(user == null, "User cannot be null for status change");
        FacilioUtil.throwIllegalArgumentException(user.getOuid() <= 0, "Invalid ouid for status change");
        FacilioUtil.throwIllegalArgumentException(user.getId() == AccountUtil.getCurrentUser().getId(), "Cannot change status of logged in user by the same user");

        User iamUser = AccountUtil.getUserBean().getUser(user.getOuid(), false);
        FacilioUtil.throwIllegalArgumentException(iamUser == null, "Invalid ouid for status change");
        FacilioUtil.throwIllegalArgumentException(AccountConstants.DefaultRole.SUPER_ADMIN.equals(iamUser.getRole().getName()), "Cannot change status of SuperAdmin");

        boolean result = false;
        if(user.getUserStatus()) {
            result = AccountUtil.getUserBean().enableUser(AccountUtil.getCurrentOrg().getOrgId(), iamUser.getUid());
        }
        else {
            result = AccountUtil.getUserBean().disableUser(AccountUtil.getCurrentOrg().getOrgId(), iamUser.getUid());
        }
        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }
}
