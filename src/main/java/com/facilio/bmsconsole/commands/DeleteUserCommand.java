package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        boolean result = AccountUtil.getUserBean().deleteUser(user.getOuid(), true);
        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }
}
