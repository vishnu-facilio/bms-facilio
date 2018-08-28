package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class DeviceMonitorJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeviceMonitorJob.class.getName());
    private static final FacilioModule DEVICE_DETAILS_MODULE = ModuleFactory.getDeviceDetailsModule();
    private static final List<FacilioField> FIELDS = FieldFactory.getDeviceDetailsFields();
    private static final FacilioField ORG_ID_FIELD = FieldFactory.getOrgIdField(DEVICE_DETAILS_MODULE);
    private static final long DEFAULT_TIMEOUT = 30*60*100L;

    public void execute(JobContext jc) {

        if(jc.getOrgId() != -1) {
            Condition orgIdCondition = new Condition();
            orgIdCondition.setField(ORG_ID_FIELD);
            orgIdCondition.setOperator(NumberOperators.EQUALS);
            orgIdCondition.setValue(String.valueOf(jc.getOrgId()));

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(DEVICE_DETAILS_MODULE.getTableName()).andCondition(orgIdCondition).select(FIELDS);
            try {
                List<Map<String, Object>> data = builder.get();
                for(Map<String, Object> obj : data) {
                    String deviceId = (String)obj.get("deviceId");
                    String name = deviceId;
                    Object deviceName = obj.get("deviceName");
                    Long lastUpdatedTime = (Long)obj.get("lastUpdatedTime");
                    if((System.currentTimeMillis()-lastUpdatedTime) > DEFAULT_TIMEOUT) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("deviceId", deviceId);
                        jsonObject.put("lastProcessedTime", lastUpdatedTime);
                        if(deviceName != null) {
                            name = (String)deviceName;
                        }
                        jsonObject.put("deviceName", name);
                        sendEvent(jsonObject);
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Exception while getting device data", e);
            }

        }
    }

    private void sendEvent(JSONObject jsonObject) {
        LOGGER.info("Device data missing " + jsonObject);
    }
}
