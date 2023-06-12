package com.facilio.wmsv2.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateOnOfflineRecordHandler extends BaseHandler{
    private static final Logger LOGGER = LogManager.getLogger(UpdateOnOfflineRecordHandler.class.getName());

    @Override
    public void processOutgoingMessage(Message message) {
        try {
            Long moduleId = ((Number) message.getContent().get("moduleId")).longValue();
            String moduleName = (String) message.getContent().get("moduleName");
            List<Long> recordIds = (List<Long>) message.getContent().get("recordIds");
            String registerTableName = ModuleFactory.getOfflineRecordRegisterModule().getTableName();
            String userMobileSettingTableName = IAMAccountConstants.getUserMobileSettingModule().getTableName();
            String type = (String) message.getContent().get("type");

            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(FieldFactory.getOfflineRecordRegisterFields());
            fields.addAll(IAMAccountConstants.getUserMobileSettingFields());

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(registerTableName)
                    .innerJoin(IAMAccountConstants.getUserMobileSettingModule().getTableName())
                    .on(userMobileSettingTableName + ".USER_MOBILE_SETTING_ID = " + registerTableName + ".USER_MOBILE_SETTING_ID")
                    .andCondition(CriteriaAPI.getCondition(registerTableName + ".RECORD_ID", "recordId", StringUtils.join(recordIds, ','), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(registerTableName + ".MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));

            List<Map<String, Object>> registeredRecords = null;
            try {
                registeredRecords = selectBuilder.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (CollectionUtils.isNotEmpty(registeredRecords)) {
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                for (Map<String, Object> registeredRecord : registeredRecords) {
                    if (registeredRecord != null) {
                        String mobileInstanceId = (String) registeredRecord.get("mobileInstanceId");
                        Boolean isFromPortal = (Boolean) registeredRecord.get("fromPortal");

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("moduleName", moduleName);
                        data.put("recordId", registeredRecord.get("recordId"));
                        data.put("orgId", orgId);
                        data.put("type", "OFFLINE_" + type + "_UPDATE");

                        JSONObject content = new JSONObject();
                        content.put("data", data);
                        content.put("to", mobileInstanceId);
                        content.put("content_available", true);

                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "key=" + (isFromPortal ? FacilioProperties.getPortalPushNotificationKey() : FacilioProperties.getPushNotificationKey()));

                        String url = "https://fcm.googleapis.com/fcm/send";

                        try {
                            LOGGER.info("push notification using wms :" + url + headers + content);
                            AwsUtil.doHttpPost(url, headers, null, content.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
