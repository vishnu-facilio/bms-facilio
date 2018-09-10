package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DeviceMonitorJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeviceMonitorJob.class.getName());
    private static final FacilioModule DEVICE_DETAILS_MODULE = ModuleFactory.getDeviceDetailsModule();
    private static final List<FacilioField> FIELDS = FieldFactory.getDeviceDetailsFields();
    private static final FacilioField ORG_ID_FIELD = FieldFactory.getOrgIdField(DEVICE_DETAILS_MODULE);
    private static final FacilioField ID_FIELD = FieldFactory.getIdField(DEVICE_DETAILS_MODULE);
    private static final Condition IN_USE = CriteriaAPI.getCondition("IN_USE", "IN_USE", String.valueOf(1), NumberOperators.EQUALS);
    private static final long DEFAULT_TIMEOUT = 40*60*1000L;

    public void execute(JobContext jc) {

        if(jc.getOrgId() != -1) {
            Condition orgIdCondition = new Condition();
            orgIdCondition.setField(ORG_ID_FIELD);
            orgIdCondition.setOperator(NumberOperators.EQUALS);
            orgIdCondition.setValue(String.valueOf(jc.getOrgId()));

            FacilioField lastUpdatedTimeField = new FacilioField();
            lastUpdatedTimeField.setName("lastUpdatedTime");
		    lastUpdatedTimeField.setDataType(FieldType.NUMBER);
		    lastUpdatedTimeField.setColumnName("LAST_UPDATED_TIME");
		    lastUpdatedTimeField.setModule(DEVICE_DETAILS_MODULE);

            Condition lastUpdatedTimeCondition = new Condition();
            lastUpdatedTimeCondition.setField(lastUpdatedTimeField);
            lastUpdatedTimeCondition.setOperator(NumberOperators.LESS_THAN_EQUAL);
            lastUpdatedTimeCondition.setValue(String.valueOf((System.currentTimeMillis()- DEFAULT_TIMEOUT)));

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(DEVICE_DETAILS_MODULE.getTableName()).andCondition(orgIdCondition).andCondition(lastUpdatedTimeCondition).andCondition(IN_USE).select(FIELDS);
            try {
                List<Map<String, Object>> data = builder.get();
                for(Map<String, Object> obj : data) {
                    String deviceId = (String)obj.get("deviceId");
                    String name = deviceId;
                    Object deviceName = obj.get("deviceName");
                    Long lastUpdatedTime = (Long)obj.get("lastUpdatedTime");
                    Long lastAlertedTime = (Long)obj.get("lastAlertedTime");
                    Object alertFrequency = obj.get("alertFrequency");
                    Long deviceDetailsId = (Long) obj.get("id");
                    long alertTime = DEFAULT_TIMEOUT;
                    if(alertFrequency != null) {
                        try {
                            alertTime = (Long) alertFrequency;
                        } catch (ClassCastException e){
                            LOGGER.info("Exception while converting " + alertFrequency, e);
                        }
                    }
                    long currentTime = System.currentTimeMillis();
                    if(((currentTime - lastUpdatedTime) > alertTime) && ((currentTime - lastAlertedTime) > alertTime)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("deviceId", deviceId);
                        jsonObject.put("lastProcessedTime", lastUpdatedTime);
                        if(deviceName != null) {
                            name = (String)deviceName;
                        }
                        jsonObject.put("deviceName", name);
                        jsonObject.put("message","Data missing for controller " + name);
                        jsonObject.put("severity","Major");
                        sendEvent(jsonObject);
                        updateLastAlertedTime(deviceDetailsId);
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Exception while getting device data", e);
            }

        }
    }

    private void sendEvent(JSONObject jsonObject) {
        LOGGER.info("Device data missing " + jsonObject);
        try {
            FacilioContext addEventContext = new FacilioContext();
            addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, jsonObject);
            Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
            getAddEventChain.execute(addEventContext);
        } catch (Exception e) {
            LOGGER.info("Exception while adding event");
        }
    }

    private void updateLastAlertedTime(long deviceDetailsId) {

        Condition idCondition = new Condition();
        idCondition.setField(ID_FIELD);
        idCondition.setOperator(NumberOperators.EQUALS);
        idCondition.setValue(String.valueOf(deviceDetailsId));

        FacilioField lastUpdatedTimeField = new FacilioField();
        lastUpdatedTimeField.setName("lastUpdatedTime");
        lastUpdatedTimeField.setDataType(FieldType.NUMBER);
        lastUpdatedTimeField.setColumnName("LAST_UPDATED_TIME");
        lastUpdatedTimeField.setModule(DEVICE_DETAILS_MODULE);

        Condition lastUpdatedTimeCondition = new Condition();
        lastUpdatedTimeCondition.setField(lastUpdatedTimeField);
        lastUpdatedTimeCondition.setOperator(NumberOperators.LESS_THAN_EQUAL);
        lastUpdatedTimeCondition.setValue(String.valueOf((System.currentTimeMillis()- DEFAULT_TIMEOUT)));

        Map<String, Object> values = new HashMap<>();
        values.put("lastAlertedTime", System.currentTimeMillis());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(DEVICE_DETAILS_MODULE.getTableName()).fields(FIELDS).andCondition(idCondition).andCondition(lastUpdatedTimeCondition);
        try {
            builder.update(values);
        } catch (SQLException e) {
            LOGGER.info("Exception while updating alert time ", e);
        }
    }
}
