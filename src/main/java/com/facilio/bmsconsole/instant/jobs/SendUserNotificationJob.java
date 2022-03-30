package com.facilio.bmsconsole.instant.jobs;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendUserNotificationJob extends InstantJob {
    private static final Logger LOGGER = LogManager.getLogger(SendUserNotificationJob.class.getName());

    @Override
    public void execute(FacilioContext facilioContext) throws Exception {
        Map<Long,List<UserMobileSetting>> userMobileSettings = (Map<Long, List<UserMobileSetting>>) facilioContext.get(FacilioConstants.ContextNames.USER_MOBILE_SETTING);
        List<UserNotificationContext> userNotification  = Constants.getRecordList(facilioContext);

        if (userMobileSettings!=null) {
            //LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.get("mobileInstanceId")).collect(Collectors.toList()));
                    // content.put("to",
                    // "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
                    for(UserNotificationContext userNotificationContext:userNotification) {
                        long uid = userNotificationContext.getUser().getId();
                        User userList = AccountUtil.getUserBean().getUser(userNotificationContext.getApplication(),uid);
                        if (userMobileSettings.containsKey(userList.getUid())) {
                            Map<String, Object> data = new HashMap<>();
                            List<UserMobileSetting> mobileSettings = userMobileSettings.get(userList.getUid());
                            for(UserMobileSetting mobileSetting : mobileSettings) {
                                data.put("to", mobileSetting.getMobileInstanceId());
                                userNotificationContext.addData(data);

                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=" + (mobileSetting.isFromPortal() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));

                                String url = "https://fcm.googleapis.com/fcm/send";

                                AwsUtil.doHttpPost(url, headers, null, userNotificationContext.toString());
                                LOGGER.debug("Sending push notification data ====> " + userNotificationContext);
                            }
                        }
                    }
        }
    }
}
