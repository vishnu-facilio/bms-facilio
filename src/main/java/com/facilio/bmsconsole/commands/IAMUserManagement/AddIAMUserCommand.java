package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import org.apache.commons.chain.Context;

public class AddIAMUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);
        boolean sendInvitation = (boolean) context.get(FacilioConstants.ContextNames.SEND_INVITE);
        String password = (String) context.get(FacilioConstants.ContextNames.PASSWORD);
        if(user != null && user.getUser() != null) {
            long orgId = user.getUser().getOrgId();
            long appId = user.getApplicationId();
            ApplicationUserUtil.addAppUser(orgId, appId, false,user, sendInvitation, password);
        }

            return false;
    }
}







