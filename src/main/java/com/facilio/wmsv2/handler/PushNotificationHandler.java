package com.facilio.wmsv2.handler;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public  class PushNotificationHandler extends BaseHandler{

    private static final Logger LOGGER = LogManager.getLogger(PushNotificationHandler.class.getName());

    @Override
    public void processOutgoingMessage(Message message)  {
        try {
            List<Long> recordIds = (List<Long>) message.getContent().get("recordIds");
            FacilioContext Context=  V3Util.getSummary(FacilioConstants.ContextNames.USER_NOTIFICATION, recordIds);
            List<UserNotificationContext> userNotificationContextList = Constants.getRecordListFromContext(Context,FacilioConstants.ContextNames.USER_NOTIFICATION);
            Map<Long,List<UserNotificationContext>> userNotificationContextMap = new HashMap<>();
            userNotificationContextMap = userNotificationContextList.stream().collect(
                    Collectors.groupingBy(UserNotificationContext::getApplication,HashMap::new, Collectors.toCollection(
                            ArrayList::new)));
            if(MapUtils.isNotEmpty(userNotificationContextMap))
            {
                for(long appId: userNotificationContextMap.keySet()) {
                    ApplicationContext applicationContext = ApplicationApi.getApplicationForId(appId);
                    String appLinkName = applicationContext.getLinkName();
                    List<Long> userIdList = new ArrayList<>();
                    for (UserNotificationContext userNotificationContext : userNotificationContextMap.get(appId)) {
                        long userId = userNotificationContext.getUser().getId();
                        userIdList.add(userId);
                    }
                    List<UserMobileSetting> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(userIdList, appLinkName);
                    Map<Long,List<UserMobileSetting>> userMobileSettingMap = new HashMap<>();
                    userMobileSettingMap = mobileInstanceSettings.stream().collect(
                            Collectors.groupingBy(UserMobileSetting::getUserId,HashMap::new, Collectors.toCollection(
                                    ArrayList::new)));
                    sendNotification(userNotificationContextMap.get(appId),userMobileSettingMap,appLinkName,appId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending Push Notification : ", e);
        }
    }
    public void sendNotification(List<UserNotificationContext> userNotificationList,Map<Long,List<UserMobileSetting>> mobileInstanceSettings,String appLinkName,long appId) throws Exception {
            if (MapUtils.isNotEmpty(mobileInstanceSettings)) {
                for (UserNotificationContext userNotification : userNotificationList) {
                    JSONObject obj = new JSONObject();
                    if (appLinkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
                        obj = UserNotificationContext.getFcmObject(userNotification);
                    } else {
                        obj = UserNotificationContext.getFcmObjectMaintainence(userNotification);
                    }
                    long uid = userNotification.getUser().getId();
                    User user = AccountUtil.getUserBean().getUser(appId, uid);
                    if (mobileInstanceSettings.containsKey(user.getUid())) {
                        List<UserMobileSetting> mobileSettings = mobileInstanceSettings.get(user.getUid());
                        for (UserMobileSetting mobileSetting : mobileSettings) {
                            obj.put("to", mobileSetting.getMobileInstanceId());
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", "key=" + (mobileSetting.isFromPortal() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));
                            String url = "https://fcm.googleapis.com/fcm/send";
                            LOGGER.info("push notification using wms :" + url + headers + obj);
                            AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
                            LOGGER.debug("Sending push notification data ====> " + obj.toJSONString());
                        }
                    }
                }
            }
        }
    }