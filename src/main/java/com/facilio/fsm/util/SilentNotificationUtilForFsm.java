package com.facilio.fsm.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.SilentPushNotificationContext;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.FacilioModule;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SilentNotificationUtilForFsm {

    public static void sendNotificationForFsm(FacilioModule module, List<Long> recordIds, SilentPushNotificationContext.ActionType actionType, Long interval, Long notifyInterval) throws Exception {

        if (CollectionUtils.isNotEmpty(recordIds)) {
            for (Long recordId : recordIds) {
                SilentPushNotificationContext content = new SilentPushNotificationContext();
                Map<String, Object> data = new HashMap<>();
                data.put("id",recordId);
                data.put("eventType",actionType);
                data.put("type","FSM_LOCATION_TRACKING");
                data.put("interval",interval);
                data.put("notifyInterval",notifyInterval);
                content.setData(data);
                content.setActionType(actionType);


                long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
                if (orgId > 0L) {
                    Messenger.getMessenger().sendMessage(new Message()
                            .setKey("__fsm_silent_notification_logs__" + "/" + module.getModuleId() + "/" + recordId)
                            .setOrgId(orgId)
                            .setContent(content.toJSON()));
                }
            }
        }

    }


}
