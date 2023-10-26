package com.facilio.ims.handler;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsoleV3.context.SilentPushNotificationContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class SilentNotificationForFsmHandler extends ImsHandler {
    private static final Logger LOGGER = LogManager.getLogger(SilentNotificationForFsmHandler.class.getName());

    public static final String KEY = "__fsm_silent_notification_logs__";

    @Override
    public void processMessage(Message message) {
        try {

            List<Long> peopleIds = (List<Long>) message.getContent().get("peopleIds");
            if (peopleIds != null) {
                String appLinkName = FacilioConstants.ApplicationLinkNames.FSM_APP;
                ApplicationContext app = ApplicationApi.getApplicationForLinkName(appLinkName);
                if(app!= null) {
                    Long appId = app.getId();
                    if(appId != null && appId > 0) {
                        List<User> users = V3PeopleAPI.getUserList(peopleIds, appId);
                        if (users != null) {
                            List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
                            String eventType = (String) message.getContent().get("eventType");
                        Long interval = Long.parseLong (String.valueOf(message.getContent().get("interval")));
                        Long notifyInterval = Long.parseLong(String.valueOf(message.getContent().get("notifyInterval")));
                        List<UserMobileSetting> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(userIds, appLinkName);

                            if (CollectionUtils.isNotEmpty(mobileInstanceSettings)) {
                                Map<Long, List<UserMobileSetting>> userMobileSettingMap = new HashMap<>();
                                userMobileSettingMap = mobileInstanceSettings.stream().collect(
                                        Collectors.groupingBy(UserMobileSetting::getUserId, HashMap::new, Collectors.toCollection(
                                                ArrayList::new)));

                                for (User user : users) {
                                    if (user != null && userMobileSettingMap.containsKey(user.getUid())) {
                                        List<UserMobileSetting> mobileSettings = userMobileSettingMap.get(user.getUid());
                                        for (UserMobileSetting mobileSetting : mobileSettings) {

                                            String mobileInstanceId = mobileSetting.getMobileInstanceId();

                                            HashMap<String, Object> data = message.getContent();
                                            data.put("id", user.getPeopleId());
                                            data.put("eventType", eventType);
                                            data.put("type", "FSM_LOCATION_TRACKING");
                                            data.put("interval", interval);
                                            data.put("notifyInterval", notifyInterval);

                                            JSONObject content = new JSONObject();
                                            content.put("data", data);
                                            content.put("to", mobileInstanceId);
                                            content.put("content_available", true);

                                            Map<String, String> headers = new HashMap<>();
                                            headers.put("Content-Type", "application/json");
                                            headers.put("Authorization", "key=" + (FacilioProperties.getPushNotificationKey()));

                                            String url = "https://fcm.googleapis.com/fcm/send";
                                            AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception on sending silent notification for fsm ", e);
        }

    }
}
