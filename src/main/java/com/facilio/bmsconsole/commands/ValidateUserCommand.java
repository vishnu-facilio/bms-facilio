package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;

public class ValidateUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        String operation = (String) context.get(FacilioConstants.ContextNames.USER_OPERATION);

        FacilioUtil.throwIllegalArgumentException(user == null, MessageFormat.format("User cannot be null for {0}", operation));
        FacilioUtil.throwIllegalArgumentException(user.getOuid() <= 0, MessageFormat.format("Invalid ouid for {0}}", operation));
        FacilioUtil.throwIllegalArgumentException(user.getId() == AccountUtil.getCurrentUser().getId(), MessageFormat.format("Logged In user cannot be used for {0}", operation));

        User iamUser = AccountUtil.getUserBean().getUser(user.getOuid(), false);
        FacilioUtil.throwIllegalArgumentException(iamUser == null, MessageFormat.format("Invalid ouid for {0}", operation));
        FacilioUtil.throwIllegalArgumentException(AccountConstants.DefaultRole.SUPER_ADMIN.equals(iamUser.getRole().getName()), MessageFormat.format("SuperAdmin user cannot be used for {0}", operation));
        context.put(FacilioConstants.ContextNames.USER, iamUser);
        return false;
    }
}
