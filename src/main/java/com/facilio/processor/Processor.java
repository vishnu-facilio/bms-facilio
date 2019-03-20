package com.facilio.processor;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fw.BeanFactory;
import com.facilio.kinesis.ErrorDataProducer;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.util.AckUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.facilio.events.tasker.tasks.EventUtil;

public class Processor implements IRecordProcessor {

        private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());

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
        private EventUtil eventUtil;

        public static final String DATA_TYPE = "PUBLISH_TYPE";
        private List<EventRuleContext> eventRules = new ArrayList<>();


        Processor(long orgId, String orgDomainName){
            this.orgId = orgId;
            this.orgDomainName = orgDomainName;
            this.errorStream = orgDomainName + "-error";
            agentUtil = new AgentUtil(orgId, orgDomainName);
            agentUtil.populateAgentContextMap();
            devicePointsUtil = new DevicePointsUtil();
            ackUtil = new AckUtil();
            eventUtil = new EventUtil();
        }

        private void sendToKafka(Record record, String data) {
            JSONObject dataMap = new JSONObject();
            try {
                dataMap.put("timestamp", ""+record.getApproximateArrivalTimestamp().getTime());
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
        public void initialize(InitializationInput initializationInput) {
            Thread thread = Thread.currentThread();
            String threadName = orgDomainName +"-processor";
            thread.setName(threadName);
            this.shardId = initializationInput.getShardId();

            deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
            //orgIdField.setModule(deviceDetailsModule);

            orgIdCondition.setField(FieldFactory.getOrgIdField(deviceDetailsModule));
            orgIdCondition.setOperator(NumberOperators.EQUALS);
            orgIdCondition.setValue(String.valueOf(orgId));

            deviceIdField.setName(AgentKeys.DEVICE_ID);
            deviceIdField.setDataType(FieldType.STRING);
            deviceIdField.setColumnName("DEVICE_ID");
            deviceIdField.setModule(deviceDetailsModule);

            fields.addAll(FieldFactory.getDeviceDetailsFields());

            deviceMap.putAll(getDeviceMap());
        }

        @Override
        public void processRecords(ProcessRecordsInput processRecordsInput) {

            for (Record record : processRecordsInput.getRecords()) {
                String data = "";
                try {

                    data = decoder.decode(record.getData()).toString();
                    if(data.isEmpty()){
                        continue;
                    }

                    ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                    List<EventRuleContext> ruleList = bean.getActiveEventRules();
                    if(ruleList != null){
                        eventRules = ruleList;
                    }

                    JSONParser parser = new JSONParser();
                    JSONObject payLoad = (JSONObject) parser.parse(data);

                    String dataType = AgentKeys.EVENT;
                    if(payLoad.containsKey(EventUtil.DATA_TYPE)) {
                        dataType = (String)payLoad.remove(EventUtil.DATA_TYPE);
                    }

                    String agentName = orgDomainName;
                    if ( payLoad.containsKey(AgentKeys.AGENT)) {
                       agentName = (String)payLoad.remove(AgentKeys.AGENT);
                    }

                    String deviceId = orgDomainName;
                    if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
                        deviceId = (String) payLoad.remove(AgentKeys.DEVICE_ID);
                    }

                    long lastMessageReceivedTime = System.currentTimeMillis();
                    if (payLoad.containsKey(AgentKeys.TIMESTAMP)) {
                        Object lastTime = payLoad.get(AgentKeys.TIMESTAMP);
                        lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
                    }

                    FacilioAgent agent = agentUtil.getFacilioAgent(agentName);
                    if (agent == null && ! AgentKeys.AGENT.equals(dataType)) {
                        agent = getFacilioAgent(agentName);
                        agentUtil.addAgent(agent);
                    }

                    long i = 0;

                    HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
                    long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);

                    if(deviceLastMessageTime != lastMessageReceivedTime) {
                        switch (dataType) {
                            case AgentKeys.TIMESERIES:
                                processTimeSeries(record, payLoad, processRecordsInput, true);
                                updateDeviceTable(record.getPartitionKey());
                                break;
                            case AgentKeys.COV:
                                processTimeSeries(record, payLoad, processRecordsInput, false);
                                updateDeviceTable(record.getPartitionKey());
                                break;
                            case AgentKeys.AGENT:
                                i =  agentUtil.processAgent( payLoad);
                                break;
                            case AgentKeys.DEVICE_POINTS:
                                devicePointsUtil.processDevicePoints(payLoad, orgId, deviceMap, agent.getId());
                                break;
                            case AgentKeys.ACK:
                                ackUtil.processAck(payLoad, orgId);
                                break;
                            case AgentKeys.EVENT:
                                boolean alarmCreated = eventUtil.processEvents(record.getApproximateArrivalTimestamp().getTime(), payLoad, record.getPartitionKey(),orgId,eventRules);
                                if (alarmCreated) {
                                    processRecordsInput.getCheckpointer().checkpoint(record);
                                }
                                break;

                        }
                        dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                        deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                    } else {
                        LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType);
                    }
                    if ( i == 0 ) {
                        GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.TABLE_NAME).fields(FieldFactory.getAgentDataFields()).andCustomWhere( AgentKeys.NAME+"= '"+payLoad.get(AgentKeys.AGENT)+"'");
                        Map<String,Object> toUpdate = new HashMap<>();
                        toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
                        toUpdate.put(AgentKeys.STATE, 1);
                        toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, lastMessageReceivedTime);
                        genericUpdateRecordBuilder.update(toUpdate);

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
                    CommonCommandUtil.emailException("processor", "Error in processing records : "
                            +record.getSequenceNumber()+ " in TimeSeries ", e, data);
                    LOGGER.info("Exception occurred ", e);
                }
            }
            // LOGGER.debug("TOTAL PROCESSOR DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
        }

    private FacilioAgent getFacilioAgent(String agentName) {
        FacilioAgent agent = new FacilioAgent();
        agent.setAgentName(agentName);
        agent.setAgentConnStatus(Boolean.TRUE);
        agent.setAgentState(1);
        agent.setAgentDataInterval(900000L);
        return agent;
    }

    private void processTimeSeries(Record record, JSONObject payLoad, ProcessRecordsInput processRecordsInput, boolean isTimeSeries) throws Exception {
            long timeStamp=	record.getApproximateArrivalTimestamp().getTime();
            long startTime = System.currentTimeMillis();
            // LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + " TIME::::" +timeStamp);
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer(), isTimeSeries);
            LOGGER.info("timetaken : "+(System.currentTimeMillis() - startTime));
        }

        private void updateDeviceTable(String deviceId) {
            try {
                // LOGGER.info("Device ID : "+deviceId);
                if (deviceId == null || deviceId.isEmpty()) {
                    return;
                }
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

        @Override
        public void shutdown(ShutdownInput shutdownInput) {
            // System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
        }

    }


