package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.bmsconsoleV3.util.UserNotificationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class CheckUpdateMappingSeenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if (moduleName != null) {
            List<UserNotificationContext> notifications = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(notifications)) {
                UserNotificationContext notification = notifications.get(0);
                Map<String, Object> mapping = UserNotificationAPI.getUserNotificationMapping(notification.getUser().getId());
                if (MapUtils.isEmpty(mapping)) {
                    UserNotificationAPI.insertUserNotificationMapping(notification.getUser().getId(), notification.getSysCreatedTime() - 1);
                }
            }
        }


        return false;
    }
}
