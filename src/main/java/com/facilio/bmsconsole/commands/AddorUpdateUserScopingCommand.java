package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddorUpdateUserScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        //commenting until ui is live
        if(user.getScopingId() > 0) {
            //FacilioUtil.throwIllegalArgumentException(user.getScoping() == null, "Invalid scoping");
            Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APPLICATION_ID, -1l);
            if (appId == null || appId <= 0) {
                appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            }
            ApplicationApi.updateScopingForUser(user.getScopingId(), appId, user.getOuid());
        }
        return false;
    }
}
