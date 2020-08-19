package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.util.UserNotificationAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class UpdateSeenNotificationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("unseen")) {
            UserNotificationAPI.getUpdateUserNotificationMapping(AccountUtil.getCurrentUser().getId());
            return true;
        }
        return false;
    }
}
