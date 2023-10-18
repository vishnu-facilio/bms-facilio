package com.facilio.fsm.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.SilentPushNotificationContext;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.SilentNotificationForFsmHandler;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SilentNotificationUtilForFsm {

    public static void sendNotificationForFsm(List<Long> peopleIds, SilentPushNotificationContext.ActionType actionType, Long interval, Long notifyInterval) throws Exception {

        if (CollectionUtils.isNotEmpty(peopleIds)) {

                SilentPushNotificationContext content = new SilentPushNotificationContext();
                Map<String, Object> data = new HashMap<>();
                data.put("peopleIds",peopleIds);
                data.put("eventType",actionType);
                data.put("type","FSM_LOCATION_TRACKING");
                data.put("interval",interval);
                data.put("notifyInterval",notifyInterval);
                content.setData(data);
                content.setActionType(actionType);


                long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
                if (orgId > 0L) {

                    Message message = new Message();
                    message.setKey(SilentNotificationForFsmHandler.KEY + "/" + peopleIds.get(0));
                    message.setOrgId(orgId);
                    message.setContent(content.toJSON());
                    Messenger.getMessenger().sendMessage(message);

                    ;
            }
        }

    }


}
