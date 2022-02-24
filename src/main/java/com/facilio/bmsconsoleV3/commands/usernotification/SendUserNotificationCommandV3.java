package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        if (moduleName != null) {
            List<UserNotificationContext> records = recordMap.get(moduleName);
        }
        JSONObject obj = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);
        if (obj != null && FacilioProperties.isProduction()) {
            String ids = (String) obj.get("id");
            List<Long> idLongList = Stream.of(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(idLongList)) {
                ApplicationContext applicationContext = ApplicationApi.getApplicationForId((long) obj.get("application"));
                String appLinkName = applicationContext.getLinkName();
                List<UserMobileSetting> mobileInstanceSettings = NotificationAPI.getMobileInstanceIDs(idLongList,appLinkName);
                LOGGER.info("Sending push notifications for ids : "+ids);
                sendNotification(obj,mobileInstanceSettings);
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

    public void sendNotification(JSONObject obj,List<UserMobileSetting> mobileInstanceSettings) throws Exception {

        if (CollectionUtils.isNotEmpty(mobileInstanceSettings)) {
            //LOGGER.info("Sending push notifications for mobileIds : "+mobileInstanceSettings.stream().map(pair -> pair.get("mobileInstanceId")).collect(Collectors.toList()));
            for (UserMobileSetting mobileInstanceSetting : mobileInstanceSettings) {
                if (mobileInstanceSetting != null) {
                    // content.put("to",
                    // "exA12zxrItk:APA91bFzIR6XWcacYh24RgnTwtsyBDGa5oCs5DVM9h3AyBRk7GoWPmlZ51RLv4DxPt2Dq2J4HDTRxW6_j-RfxwAVl9RT9uf9-d9SzQchMO5DHCbJs7fLauLIuwA5XueDuk7p5P7k9PfV");
                    obj.put("to", mobileInstanceSetting.getMobileInstanceId());

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key="+ (mobileInstanceSetting.isFromPortal() ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));

                    String url = "https://fcm.googleapis.com/fcm/send";

                    AwsUtil.doHttpPost(url, headers, null, obj.toJSONString());
                    LOGGER.debug("Sending push notification data ====> " + obj.toJSONString() );
                }
            }
        }

    }
}
