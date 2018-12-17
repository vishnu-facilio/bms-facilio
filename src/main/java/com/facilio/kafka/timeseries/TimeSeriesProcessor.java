package com.facilio.kafka.timeseries;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;
import com.facilio.kafka.FacilioKafkaConsumer;
import com.facilio.kafka.FacilioKafkaProducer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class TimeSeriesProcessor extends FacilioProcessor {

    private final List<FacilioField> fields = new ArrayList<>();
    private final Condition orgIdCondition = new Condition();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    private final FacilioField orgIdField = FieldFactory.getOrgIdField();


    private static final Logger LOGGER = LogManager.getLogger(TimeSeriesProcessor.class.getName());

    public TimeSeriesProcessor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        String clientName = orgDomainName +"-timeseries-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        initializeModules();
    }



    private void initializeModules() {

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
        orgIdField.setModule(deviceDetailsModule);

        orgIdCondition.setField(orgIdField);
        orgIdCondition.setOperator(NumberOperators.EQUALS);
        orgIdCondition.setValue(String.valueOf(getOrgId()));

        deviceIdField.setName("deviceId");
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());

        deviceMap.putAll(getDeviceMap());
    }

    public void processRecords(List<FacilioRecord> records) {
        for(FacilioRecord record : records) {
            processRecord(record);
        }
    }



    private void processRecord (FacilioRecord record) {
        String partitionKey = "";
        JSONObject payLoad = record.getData();
        try {
            partitionKey = record.getPartitionKey();
            String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);
            if(dataType!=null ) {
                switch (dataType) {
                    case "timeseries":
                        processTimeSeries(record);
                        break;
                    case "devicepoints":
                        processDevicePoints(payLoad);
                        break;
                    case "ack":
                        processAck(payLoad);
                        break;
                }
            }
        } catch (Exception e) {
            CommonCommandUtil.emailException("KTimeSeriesProcessor", "Error in processing records in TimeSeries ", e, payLoad.toJSONString());
            LOGGER.info("Exception occurred ", e);
        } finally {
            updateDeviceTable(partitionKey);
        }
    }


    private void processTimeSeries(FacilioRecord record) throws Exception {
        long timeStamp = record.getTimeStamp();
        JSONObject payLoad = record.getData();
        if (payLoad.containsKey("timestamp")) {
            timeStamp = Long.parseLong(String.valueOf(payLoad.get("timestamp")));
        }
        LOGGER.info(" timeseries data " + payLoad);
        // ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        // bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer());
    }


    private void processAck(JSONObject payLoad) throws Exception {
        Long msgId = (Long) payLoad.get("msgid");
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
        bean.acknowledgePublishedMessage(msgId);
    }

    private void processDevicePoints (JSONObject payLoad) throws Exception {
        long instanceNumber = (Long)payLoad.get("instanceNumber");
        String destinationAddress = "";
        if(payLoad.containsKey("macAddress")) {
            destinationAddress = (String) payLoad.get("macAddress");
        }
        long subnetPrefix = (Long)payLoad.get("subnetPrefix");
        long networkNumber = -1;
        if(payLoad.containsKey("networkNumber")) {
            networkNumber = (Long) payLoad.get("networkNumber");
        }
        String broadcastAddress = (String) payLoad.get("broadcastAddress");
        String deviceName = (String) payLoad.get("deviceName");

        String deviceId = instanceNumber+"_"+destinationAddress+"_"+networkNumber;
        if( ! deviceMap.containsKey(deviceId)) {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
            ControllerContext controller = bean.getController(deviceId);
            if(controller == null) {
                controller = new ControllerContext();
                controller.setName(deviceName);
                controller.setBroadcastIp(broadcastAddress);
                controller.setDestinationId(destinationAddress);
                controller.setInstanceNumber(instanceNumber);
                controller.setNetworkNumber(networkNumber);
                controller.setSubnetPrefix(Math.toIntExact(subnetPrefix));
                controller.setMacAddr(deviceId);
                controller = bean.addController(controller);
            }
            long controllerSettingsId = controller.getId();
            if(controllerSettingsId > -1) {
                JSONArray points = (JSONArray)payLoad.get("points");
                LOGGER.info("Device Points : "+points);
                TimeSeriesAPI.addUnmodeledInstances(points, controllerSettingsId);
            }
        }
    }
    private void updateDeviceTable(String deviceId) {
        try {
            if( ! deviceMap.containsKey(deviceId)) {
                addDeviceId(deviceId);
            }
            if(deviceMap.containsKey(deviceId)) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("lastUpdatedTime", System.currentTimeMillis());
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(deviceDetailsModule.getTableName())
                        .fields(fields).andCondition(getDeviceIdCondition(deviceId)).andCondition(orgIdCondition);
                builder.update(map);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while updating time for device id " + deviceId, e);
        }
    }

    private Condition getDeviceIdCondition(String deviceId) {
        return  CriteriaAPI.getCondition("DEVICE_ID", "DEVICE_ID", deviceId, StringOperators.IS);
    }

    private HashMap<String, Long> getDeviceMap() {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName()).andCondition(orgIdCondition).select(fields);
        HashMap<String, Long> deviceData = new HashMap<>();
        try {
            List<Map<String, Object>> data = builder.get();
            for(Map<String, Object> obj : data) {
                String deviceId = (String)obj.get("deviceId");
                Long id = (Long)obj.get("id");
                deviceData.put(deviceId, id);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting device data", e);
        }

        return deviceData;
    }

    private void addDeviceId(String deviceId) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(fields);
        HashMap<String, Object> device = new HashMap<>();
        device.put("orgId", getOrgId());
        device.put("deviceId", deviceId);
        device.put("inUse", true);
        device.put("lastUpdatedTime", System.currentTimeMillis());
        device.put("lastAlertedTime", 0L);
        device.put("alertFrequency", 2400000L);
        long id = builder.insert(device);
        deviceMap.put(deviceId, id);
    }
}
