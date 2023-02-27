package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;


import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.endpoint.SessionManager;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facilio.wmsv2.constants.Topics.PushNotification.pushNotification;

public class SendUserNotificationCommandV3 extends FacilioCommand implements PostTransactionCommand {

    private static final Logger LOGGER = LogManager.getLogger(SendUserNotificationCommandV3.class.getName());
    private Context context;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;
        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        List<UserNotificationContext> records = new ArrayList<>();
        if (moduleName != null) {
          records = recordMap.get(moduleName);
        }
        Boolean isPushNotification = (Boolean) bodyParams.get("isPushNotification");
        if (isPushNotification && FacilioProperties.isProduction()) {

            if( AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PUSHNOTIFICATION_WMS )) {
                List<Long> recordId = new ArrayList<>();
                for (UserNotificationContext userNotificationContext : records) {
                    long id = userNotificationContext.getId();
                    recordId.add(id);
                }
                JSONObject ids = new JSONObject();
                ids.put("recordIds", recordId);
                if (CollectionUtils.isNotEmpty(recordId)) {
                    Message message = new Message();
                    message.setTopic(pushNotification);
                    message.setContent(ids);
                    LOGGER.info("Sending push notifications for ids to wms: " + recordId);
                    SessionManager.getInstance().sendMessage(message);
                }
            }
            else {
                List<Long> idLongList = new ArrayList<>();
            for (UserNotificationContext userNotificationContext : records) {
                long id = userNotificationContext.getUser().getId();
                idLongList.add(id);
            }

            if (CollectionUtils.isNotEmpty(idLongList)) {
                ApplicationContext applicationContext = ApplicationApi.getApplicationForId(records.get(0).getApplication());
                String appLinkName = applicationContext.getLinkName();
                List<UserMobileSetting> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(idLongList, appLinkName);
                Map<Long,List<UserMobileSetting>> userMobileSettingMap = new HashMap<>();
                userMobileSettingMap = mobileInstanceSettings.stream().collect(
                        Collectors.groupingBy(UserMobileSetting::getUserId,HashMap::new,Collectors.toCollection(
                                ArrayList::new)));
                LOGGER.info("Sending push notifications for ids : " + idLongList);
                sendNotification(records, userMobileSettingMap);
               }
            }
        }
//        if(CollectionUtils.isNotEmpty(records)) {
//            for (UserNotificationContext notify : records) {
//                List<Pair<String, Boolean>> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(Collections.singletonList(notify.getUser().getId()));
//                LOGGER.info("Sending push notifications for ids : ");
//                if (mobileInstanceSettings != null && !mobileInstanceSettings.isEmpty()) {
//                    LOGGER.info("Sending push notifications for mobileIds : " + mobileInstanceSettings.stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
//                    for (Pair<String, Boolean> mobileInstanceSetting : mobileInstanceSettings) {
//                        if (mobileInstanceSetting != null) {
//                            JSONObject json = FieldUtil.getAsJSON(notify);
//                            json.put("to", mobileInstanceSetting.getLeft());
//
//                            Map<String, String> headers = new HashMap<>();
//                            headers.put("Content-Type", "application/json");
//                            headers.put("Authorization", "key=" + (mobileInstanceSetting.getRight() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));
//
//                            String url = "https://fcm.googleapis.com/fcm/send";
//
//                            AwsUtil.doHttpPost(url, headers, null, json.toJSONString());
//                        }
//                    }
//                }
//                // Web socket call for push notification in web app
////                WmsNotification wmsNotif = new WmsNotification().setNotification(FieldUtil.getAsJSON(notify));
////                wmsNotif.setSessionType(LiveSession.LiveSessionType.APP);
////                WmsApi.sendNotification(Collections.singletonList(notify.getUser().getId()), wmsNotif);
//            }
//        }
        return false;
    }

    public void sendNotification(List<UserNotificationContext> userNotificationList,Map<Long,List<UserMobileSetting>> mobileInstanceSettings) throws Exception {
        FacilioContext context = new FacilioContext();
        Constants.setRecordList(context,userNotificationList);
        context.put(FacilioConstants.ContextNames.USER_MOBILE_SETTING,mobileInstanceSettings);
        FacilioTimer.scheduleInstantJob("default","SendUserNotificationJob",context);
    }
}
