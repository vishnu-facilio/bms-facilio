package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.BooleanUtils;

public class UpdateIAMUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PeopleUserContext user = (PeopleUserContext) context.get(FacilioConstants.ContextNames.USER);
        if(user!=null && !BooleanUtils.isTrue(user.getUser().getIsSuperUser()) && user.getRole() != null && !user.getRole().isSuperAdmin()){
            ApplicationUserUtil.updateAppUser(user);
        }
        return false;
    }
}
