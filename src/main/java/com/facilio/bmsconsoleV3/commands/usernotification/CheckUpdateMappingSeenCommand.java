package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.bmsconsoleV3.util.UserNotificationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CheckUpdateMappingSeenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if (moduleName != null) {
            List<UserNotificationContext> notification = recordMap.get(moduleName);
            for (UserNotificationContext noti: notification) {
                List<Map<String, Object>> mapping = UserNotificationAPI.getUserNotificationMapping(noti.getUser().getId());
                if (mapping == null || mapping.isEmpty()) {
                    UserNotificationAPI.getInsertUserNotificationMapping(noti.getUser().getId());
                }

            }
        }


        return false;
    }
}
