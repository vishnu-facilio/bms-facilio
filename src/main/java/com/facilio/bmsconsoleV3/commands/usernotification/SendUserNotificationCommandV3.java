package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.PushNotificationHandler;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Boolean isPushNotification = (bodyParams!=null)? (Boolean) bodyParams.getOrDefault("isPushNotification",false) :false;
        if (isPushNotification && FacilioProperties.isProduction() ) {

                List<Long> recordIdsList = new ArrayList<>();
                for (UserNotificationContext userNotificationContext : records) {
                    long id = userNotificationContext.getId();
                    recordIdsList.add(id);
                }
                JSONObject ids = new JSONObject();
                ids.put("recordIds", recordIdsList);
                if (CollectionUtils.isNotEmpty(recordIdsList)) {
                    Message message = new Message();
                    message.setKey(PushNotificationHandler.KEY+"/"+recordIdsList.get(0));
                    message.setContent(ids);
                    LOGGER.info("Sending push notifications for ids to ims: " + recordIdsList);
                    Messenger.getMessenger().sendMessage(message);
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
}
