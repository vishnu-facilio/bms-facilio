package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.impl.IamClient;
import org.apache.commons.chain.Context;

public class DeleteUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        boolean result = AccountUtil.getUserBean().deleteUser(user.getOuid(), true);
        String[] s = user.getIdentifier().split("_");
        IamClient.deleteDCLookup(user.getUserName(), AppDomain.GroupType.valueOf(Integer.parseInt(s[0])));
        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }
}
