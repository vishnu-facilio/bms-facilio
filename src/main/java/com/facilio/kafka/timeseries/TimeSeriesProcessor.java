package com.facilio.kafka.timeseries;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;
import com.facilio.kafka.FacilioKafkaConsumer;
import com.facilio.kafka.FacilioKafkaProducer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeriesProcessor extends FacilioProcessor {

    private final List<FacilioField> fields = new ArrayList<>();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    /*private final FacilioField orgIdField = FieldFactory.getOrgIdField();*/


    private static final Logger LOGGER = LogManager.getLogger(TimeSeriesProcessor.class.getName());

    public TimeSeriesProcessor(long orgId, String orgDomainName, String type) {
        super(orgId, orgDomainName);
        String clientName = orgDomainName +"-timeseries-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        initializeModules();
        setEventType(type);
        LOGGER.info("Initializing timeseries processor " + orgDomainName);
    }



    private void initializeModules() {

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
        //orgIdField.setModule(deviceDetailsModule);

        //orgIdCondition.setField(orgIdField);
        //orgIdCondition.setOperator(NumberOperators.EQUALS);
        //orgIdCondition.setValue(String.valueOf(getOrgId()));

        deviceIdField.setName("deviceId");
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());

        deviceMap.putAll(getDeviceMap());
    }

    public void processRecords(List<FacilioRecord> records) {
        if(records.size() > 0) {
            LOGGER.info("record size : " + records.size());
        }
        for(FacilioRecord record : records) {
            processRecord(record);
        }
    }



    private void processRecord (FacilioRecord record) {
        String partitionKey = "";
        // LOGGER.info(" timeseries data " + record.getData());
        JSONObject payLoad = record.getData();
        try {
            partitionKey = record.getPartitionKey();
            String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);
            if(dataType != null ) {
                switch (dataType) {
                    case "timeseries":
                        processTimeSeries(record);
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
         ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
         bean.processTimeSeries(getConsumer(), record);
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
                        .fields(fields).andCondition(getDeviceIdCondition(deviceId));
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
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName()).select(fields);
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
