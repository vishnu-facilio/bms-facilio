package com.facilio.wmsv2.handler;

import com.facilio.accounts.dto.UserMobileSetting;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.OfflineRecordRegisterContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
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
import java.util.stream.Collectors;

public class UpdateOnOfflineRecordHandler extends BaseHandler{
    private static final Logger LOGGER = LogManager.getLogger(UpdateOnOfflineRecordHandler.class.getName());

    @Override
    public void processOutgoingMessage(Message message) {
        try {
            Long moduleId = ((Number) message.getContent().get("moduleId")).longValue();
            String moduleName = (String) message.getContent().get("moduleName");
            List<Long> recordIds = (List<Long>) message.getContent().get("recordIds");
            String type = (String) message.getContent().get("type");

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getOfflineRecordRegisterFields())
                    .table(ModuleFactory.getOfflineRecordRegisterModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", StringUtils.join(recordIds, ','), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));


            List<OfflineRecordRegisterContext> registeredRecords = null;
            List<UserMobileSetting> mobileSettings = new ArrayList<>();

            try {
                registeredRecords = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), OfflineRecordRegisterContext.class);
                if(CollectionUtils.isEmpty(registeredRecords)){
                    LOGGER.debug("recordIds -> " + recordIds);
                    LOGGER.debug("select query of registeredRecords -> " + selectBuilder.constructSelectStatement());
                }
                if(CollectionUtils.isNotEmpty(registeredRecords)) {
                    List<Long> mobileInstanceIds = registeredRecords.stream().map(OfflineRecordRegisterContext::getUserMobileSettingId).collect(Collectors.toList());
                    mobileSettings = IAMUserUtil.getUserMobileSettings(mobileInstanceIds);
                }
            } catch (Exception e) {
                LOGGER.error("Error fetching mobile Instance",e);
            }

            if(CollectionUtils.isEmpty(mobileSettings)){
                LOGGER.debug("Mobile Instance not found");
                return;
            }

            HashMap<Long,UserMobileSetting> mobileSettingHashMap = new HashMap<>();
            for(UserMobileSetting mobileSetting: mobileSettings){
                mobileSettingHashMap.put(mobileSetting.getUserMobileSettingId(),mobileSetting);
            }

            if (CollectionUtils.isNotEmpty(registeredRecords)) {
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                List<Long> recordsToBeDeleted = new ArrayList<>();
                for (OfflineRecordRegisterContext registeredRecord : registeredRecords) {
                    if (registeredRecord != null) {
                        UserMobileSetting mobileSetting = mobileSettingHashMap.get((registeredRecord.getUserMobileSettingId()));

                        if(mobileSetting == null){
                            recordsToBeDeleted.add(registeredRecord.getId());
                            continue;
                        }

                        String mobileInstanceId = mobileSetting.getMobileInstanceId();
                        Boolean isFromPortal = mobileSetting.getFromPortal();

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("moduleName", moduleName);
                        data.put("recordId", registeredRecord.getRecordId());
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
                if(CollectionUtils.isNotEmpty(recordsToBeDeleted)){
                    deleteOfflineRegisteredRecord(recordsToBeDeleted);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception on sending push notification ",e);
        }
    }
    public static void deleteOfflineRegisteredRecord(List<Long> ids) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids,","), NumberOperators.EQUALS));

        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getOfflineRecordRegisterModule().getTableName())
                .andCriteria(criteria);

        deleteBuilder.delete();
    }
}
