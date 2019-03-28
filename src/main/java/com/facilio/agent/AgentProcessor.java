package com.facilio.agent;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;
import com.facilio.kinesis.ErrorDataProducer;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.util.AckUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

public class AgentProcessor implements IRecordProcessor {
    private static final Logger LOGGER = LogManager.getLogger(AgentProcessor.class.getName());
    private long orgId;
    private String orgDomainName;
    private String shardId;
    private String errorStream;
    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private final List<FacilioField> fields = new ArrayList<>();
    private final Condition orgIdCondition = new Condition();
    private final FacilioField deviceIdField = new FacilioField();
    private final HashMap<String, Long> deviceMap = new HashMap<>();
    private FacilioModule deviceDetailsModule;
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;

    public AgentProcessor(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap();
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName + "-agent";
        thread.setName(threadName);
        this.shardId = initializationInput.getShardId();

        deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();

        orgIdCondition.setField(FieldFactory.getOrgIdField(deviceDetailsModule));
        orgIdCondition.setOperator(NumberOperators.EQUALS);
        orgIdCondition.setValue(String.valueOf(orgId));

        deviceIdField.setName("deviceId");
        deviceIdField.setDataType(FieldType.STRING);
        deviceIdField.setColumnName("DEVICE_ID");
        deviceIdField.setModule(deviceDetailsModule);

        fields.addAll(FieldFactory.getDeviceDetailsFields());

        deviceMap.putAll(getDeviceMap());
    }

    private void sendToKafka(Record record, String data) {
        JSONObject dataMap = new JSONObject();
        try {
            dataMap.put("timestamp", "" + record.getApproximateArrivalTimestamp().getTime());
            dataMap.put("key", record.getPartitionKey());
            dataMap.put("data", data);
            dataMap.put("sequenceNumber", record.getSequenceNumber());
            ErrorDataProducer.send(new ProducerRecord<>(errorStream, record.getPartitionKey(), dataMap.toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + dataMap);
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        long processStartTime = System.currentTimeMillis();
        for (Record record : processRecordsInput.getRecords()) {
            String data = "";
            try {

                data = decoder.decode(record.getData()).toString();
                if(data.isEmpty()){
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject payLoad = (JSONObject) parser.parse(data);
                String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);

                String deviceId = orgDomainName;
                if (payLoad.containsKey("deviceId")) {
                    deviceId = payLoad.get("deviceId").toString();
                }
                long lastMessageReceivedTime = System.currentTimeMillis();
                if (payLoad.containsKey("timestamp")) {
                    Object lastTime = payLoad.get("timestamp");
                    lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
                }

                if(dataType!=null ) {
                    HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
                    long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);
                    int i = 0;
                    if(deviceLastMessageTime != lastMessageReceivedTime) {
                        switch (dataType) {
                            case AgentKeys.AGENT:
                                // i =  agentUtil.processAgent( payLoad);
                                break;
                            case AgentKeys.DEVICE_POINTS:
                                // devicePointsUtil.processDevicePoints(payLoad, orgId, deviceMap);
                                break;
                            case AgentKeys.ACK:
                                 ackUtil.processAck(payLoad, orgId);
                                break;

                        }
                        dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                        deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                    } else {
                        LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType);
                    }
                    if(i == 0 ) {
                        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.TABLE_NAME).fields(FieldFactory.getAgentDataFields()).andCustomWhere( AgentKeys.NAME+"= '"+payLoad.get(AgentKeys.AGENT)+"'");
                        Map<String,Object> toUpdate = new HashMap<>();
                        toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME,System.currentTimeMillis());
                        genericUpdateRecordBuilder.update(toUpdate);

                    }
				processRecordsInput.getCheckpointer().checkpoint(record);
                }
            } catch (Exception e) {
                try {
                    if(AwsUtil.isProduction()) {
                        LOGGER.info("Sending data to " + errorStream);
                        sendToKafka(record, data);
                    }
                } catch (Exception e1) {
                    LOGGER.info("Exception while sending data to " + errorStream, e1);
                }
                CommonCommandUtil.emailException("AgentProcessor", "Error in processing records : "
                        +record.getSequenceNumber()+ " in TimeSeries ", e, data);
                LOGGER.info("Exception occurred ", e);
            } finally {
                updateDeviceTable(record.getPartitionKey());
            }
        }
        LOGGER.info("TOTAL Agent DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
    }



    private void updateDeviceTable(String deviceId) {
        try {
            LOGGER.info("Device ID : " + deviceId);
            if (deviceId == null || deviceId.isEmpty()) {
                return;
            }
            if (!deviceMap.containsKey(deviceId)) {
                addDeviceId(deviceId);
            }
            if (deviceMap.containsKey(deviceId)) {
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

    private Map<String, Long> getDeviceMap() {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            return bean.getDeviceMap();
        } catch (Exception e) {
            LOGGER.info("Exception while getting device data", e);
            return new HashMap<>();
        }
    }

    private void addDeviceId(String deviceId) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        deviceMap.put(deviceId, bean.addDeviceId(deviceId));
    }
}
