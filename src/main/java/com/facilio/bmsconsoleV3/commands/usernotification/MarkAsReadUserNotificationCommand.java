package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.util.UserNotificationAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class MarkAsReadUserNotificationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long userId = (Long) context.get(FacilioConstants.ContextNames.USER);
        if (userId > 0) {
            UserNotificationAPI.getUpdateStatusByUser(userId);
        }
        return false;
    }
}
