package com.facilio.kafka.timeseries;


import com.facilio.accounts.util.AccountUtil;
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
import com.facilio.kafka.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class TimeSeriesProcessor extends FacilioProcessor {


    private long orgId;
    private String orgDomainName;
    private String topic;
    private String consumerGroup;
    private String errorStream;

    private TopicPartition topicPartition;
    private KafkaConsumer consumer;
    private KafkaProducer producer;

    private final List<FacilioField> fields = new ArrayList<>();
    private final Condition orgIdCondition = new Condition();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    private final FacilioField orgIdField = FieldFactory.getOrgIdField();


    private static final Logger LOGGER = LogManager.getLogger(TimeSeriesProcessor.class.getName());

    public TimeSeriesProcessor(long orgId, String orgDomainName) {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        topic = AwsUtil.getIotKinesisTopic(orgDomainName);
        errorStream = topic +"-error";
        String clientName = orgDomainName +"-timeseries-";
        String environment = AwsUtil.getConfig("environment");
        consumerGroup = clientName + environment;
        topicPartition = new TopicPartition(topic, 0);

        consumer = new KafkaConsumer(getConsumerProperties(ServerInfo.getHostname(), consumerGroup));
        producer = new KafkaProducer(getProducerProperties());
    }

    private void sendToKafka(JSONObject data) {
        JSONObject dataMap = new JSONObject();
        try {
            if(data.containsKey("timestamp")) {
                dataMap.put("timestamp", data.get("timestamp"));
            } else {
                dataMap.put("timestamp", System.currentTimeMillis());
            }
            dataMap.put("key", data.get("key"));
            dataMap.put("data", data);
            producer.send(new ProducerRecord<>(errorStream, data.get("key"), dataMap.toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + dataMap);
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    private void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName +"-timeseries";
        thread.setName(threadName);

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
        orgIdField.setModule(deviceDetailsModule);

        orgIdCondition.setField(orgIdField);
        orgIdCondition.setOperator(NumberOperators.EQUALS);
        orgIdCondition.setValue(String.valueOf(orgId));

        deviceIdField.setName("deviceId");
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());

        deviceMap.putAll(getDeviceMap());
    }

    public void run() {
        try {
            AccountUtil.setCurrentAccount(orgId);
            initialize();
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(5000);
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject data = (JSONObject) parser.parse(record.value());
                            String kinesisData = (String) data.get("data");
                            LOGGER.info(" timeseries data " + kinesisData);
                            // processRecords(kinesisData);
                        } catch (ParseException e) {
                            LOGGER.log(Priority.INFO, "Exception while parsing data to JSON " + record.value(), e);
                        } finally {
                            consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(record.offset() + 1)));
                        }
                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException in) {
                        LOGGER.info("Interrupted exception ", in);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception while starting timeseries processor ", e);
        }
    }


    private void processRecords(String data) {
        JSONObject payLoad = new JSONObject();
        String partitionKey = "";
        try {
            JSONParser parser = new JSONParser();
            payLoad = (JSONObject) parser.parse(data);
            partitionKey = (String) payLoad.get("key");
            String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);
            if(dataType!=null ) {
                switch (dataType) {
                    case "timeseries":
                        processTimeSeries(payLoad);
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
            try {
                if(AwsUtil.isProduction() && (payLoad.size() > 0)) {
                    LOGGER.info("Sending data to " + errorStream);
                    sendToKafka(payLoad);
                }
            } catch (Exception e1) {
                LOGGER.info("Exception while sending data to " + errorStream, e1);
            }
            CommonCommandUtil.emailException("KTimeSeriesProcessor", "Error in processing records in TimeSeries ", e, data);
            LOGGER.info("Exception occurred ", e);
        } finally {
            updateDeviceTable(partitionKey);
        }
    }


    private void processTimeSeries(JSONObject payLoad) throws Exception {
        long timeStamp = System.currentTimeMillis();
        if (payLoad.containsKey("timestamp")) {
            timeStamp = Long.parseLong(String.valueOf(payLoad.get("timestamp")));
        }
        LOGGER.info(" timeseries data " + payLoad);
        // ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        // bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer());
    }


    private void processAck(JSONObject payLoad) throws Exception {
        Long msgId = (Long) payLoad.get("msgid");
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
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
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
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
        device.put("orgId", orgId);
        device.put("deviceId", deviceId);
        device.put("inUse", true);
        device.put("lastUpdatedTime", System.currentTimeMillis());
        device.put("lastAlertedTime", 0L);
        device.put("alertFrequency", 2400000L);
        long id = builder.insert(device);
        deviceMap.put(deviceId, id);
    }
}
