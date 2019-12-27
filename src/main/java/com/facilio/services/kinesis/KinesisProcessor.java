package com.facilio.services.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.agent.*;
import com.facilio.agentv2.DataProcessorV2;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.AckUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class KinesisProcessor implements IRecordProcessor {
    private static final Logger LOGGER = LogManager.getLogger(KinesisProcessor.class.getName());

    private long orgId;
    private String orgDomainName;
    private String shardId;
    private String errorStream;
    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private EventUtil eventUtil;
    private Boolean isStage = !FacilioProperties.isProduction();
    private  boolean isRestarted = true;
    private DataProcessorV2 dataProcessorV2;
    private DataProcessorUtil dataProcessorUtil;

    public static final String DATA_TYPE = "PUBLISH_TYPE";
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private JSONParser parser = new JSONParser();

    KinesisProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        dataProcessorUtil = new DataProcessorUtil(orgId,orgDomainName);
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap(null,null);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        try {
            dataProcessorV2 = new DataProcessorV2(orgId,orgDomainName);
        }catch (Exception e){
            dataProcessorV2 = null;
            LOGGER.info("Exception occurred ",e);
        }
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
    }

    public void toDataProcessor(ProcessRecordsInput processRecordsInput){
        for (Record record : processRecordsInput.getRecords()) {

            FacilioRecord facilioRecord = toFacilioRecord(record);

            if( facilioRecord != null){

              /*  if( ( DataProcessorUtil.isIsRestarted()) &&  (  DataProcessorUtil.isDuplicate(facilioRecord.getId())) ) {
                    try {
                        processRecordsInput.getCheckpointer().checkpoint(DataProcessorUtil.getLastRecordChecked());
                        DataProcessorUtil.setIsRestarted(false);
                        return;
                    } catch (Exception e) {
                        LOGGER.info("Exception occurred while making new checkPointer",e);
                    }
                }*/
                try{

                    if( ! dataProcessorUtil.processRecord(facilioRecord)){
                        LOGGER.info(record.getPartitionKey() + " processing record failed " + record.getSequenceNumber());
                       /* try {
                            LOGGER.info(record.getSequenceNumber()+" Exception while processing kinesis record -> " + getString(record.getData()));
                        }catch (Exception e){
                            LOGGER.info(" exception while parsing failed record "+record.getSequenceNumber());
                        }*/
                    }
                    try {
                        processRecordsInput.getCheckpointer().checkpoint(DataProcessorUtil.getLastRecordChecked());
                    }catch (Exception e){
                        LOGGER.info("Exception occurred while changing checkpoint",e);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while processing  ",e);
                }
            }

        }

    }

    static FacilioRecord toFacilioRecord(Record record) {
        if(record != null) {
            String data = "";
            try {
                JSONObject jsonData = byteDataToJSON( record.getData());
                jsonData.put("timestamp", "" + record.getApproximateArrivalTimestamp().getTime());
                jsonData.put("key", record.getPartitionKey());
                jsonData.put("sequenceNumber", record.getSequenceNumber());
                FacilioRecord facilioRecord = new FacilioRecord(record.getPartitionKey(), jsonData);
                facilioRecord.setId(record.getSequenceNumber());
                System.out.println(" json data " + facilioRecord.getData());
                return facilioRecord;
            } catch (Exception e) {
                LOGGER.info("Exception occurred while converting kinesis record to facilio record ", e);
            }
        }
    return null;
    }

    private static JSONObject byteDataToJSON(ByteBuffer byteData) throws Exception {
        JSONParser parser = new JSONParser();
        if ((byteData != null)) {
            JSONObject data = (JSONObject) parser.parse(getString(byteData));
            return data;
        }else {
            throw new Exception("ByteData can't be null");
        }
    }

    private static String getString(ByteBuffer byteData) throws CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        return decoder.decode(byteData).toString();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        toDataProcessor(processRecordsInput);
        return;
        /*for (Record record : processRecordsInput.getRecords()) {
            String data = "";
            StringReader reader = null;
            String recordId = record.getSequenceNumber();
            try {

                try {
                    boolean  isDuplicateMessage = DataProcessorUtil.isDuplicate(recordId);
                    if ( isDuplicateMessage ) {
                        if(isRestarted){
                            LOGGER.info(" Duplicate message received but can be processed due to server-restart "+recordId);
                            isRestarted = false;
                        }
                        else {
                            LOGGER.info(" Duplicate message received and cannot be reprocessed "+recordId);
                            continue;
                        }
                    }
                    else {
                        boolean originalFlag =false;
                        originalFlag = DataProcessorUtil.addAgentMessage(recordId);
                        if(!originalFlag){
                            LOGGER.info("tried adding duplicate message "+ recordId);
                            continue;
                        }
                    }
                }catch (Exception e1){
                    LOGGER.info("Exception Occurred ",e1);
                }

                data = decoder.decode(record.getData()).toString();
                if (data.isEmpty()) {
                    LOGGER.info(" Empty message received "+recordId);
                    DataProcessorUtil.updateAgentMessage(recordId, MessageStatus.DATA_EMPTY);
                    continue;
                }

                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                List<EventRuleContext> ruleList = bean.getActiveEventRules();
                if(ruleList != null){
                    eventRules = ruleList;
                }

                reader = new StringReader(data);
                parser.parse(data);
                JSONObject payLoad = (JSONObject) parser.parse(reader);
                try {
                    if (payLoad.containsKey(AgentConstants.VERSION) && (("2".equalsIgnoreCase((String) payLoad.get(AgentConstants.VERSION))))) {
                        if (processorV2 != null && isStage) {
                            LOGGER.info(" newProcessor payload -> " + payLoad);
                            try {
                                processorV2.processNewAgentData(payLoad);
                            } catch (Exception newProcessorException) {
                                LOGGER.info("Exception occurred ", newProcessorException);
                            }
                        }
                        continue;
                    }
                }catch (Exception ex){
                    LOGGER.info("Exception occurred ",ex);
                }
                String dataType = PublishType.event.getValue();
                if(payLoad.containsKey(EventUtil.DATA_TYPE)) {
                    dataType = (String)payLoad.remove(EventUtil.DATA_TYPE);
                }

                // Temp fix - bug: Publish_Type wrongly set to "agents"
                if("agents".equals(dataType)){
                    dataType = PublishType.agent.getValue();
                }
                //Temp fix  - bug: Publish_Type wrongly set to "agents"
                PublishType publishType = PublishType.valueOf(dataType);
                String agentName = orgDomainName; // trim missing
                if ( payLoad.containsKey(PublishType.agent.getValue())) {
                    agentName = (String)payLoad.remove(PublishType.agent.getValue()); // trim missing
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

                FacilioAgent agent = agentUtil.getFacilioAgent(agentName,null);
                if (agent == null) {
                    agent = getFacilioAgent(agentName);
                    long agentId = agentUtil.addAgent(agent);
                    if(agentId < 1L) {
                        LOGGER.info(" Error in AgentId generation ");
                        continue;
                    }
                    agent.setId(agentId);
                }
                if( agent != null) { // no metrics block there
                    agentUtil.addAgentMetrics(data.length(), agent.getId(), publishType.getKey());
                    String agentType = agent.getAgentType();
                    try {
                        if (orgId == 152L) {
                            LOGGER.info(recordId + " Agent Type -- " + agentType);
                        }
                        if (AgentType.Wattsense.getLabel().equalsIgnoreCase(agentType)) {
                            LOGGER.info(" wattsense agent found with payload " + payLoad);
                            payLoad = WattsenseUtil.reFormatPayload(payLoad, dataType);
                            LOGGER.info(" return wattsense payload "+payLoad);
                        }
                    }catch (Exception e){
                        LOGGER.info(" Exception occurred while getting agent Type",e);
                        continue;
                    }
                }

                if (orgId == 152l && publishType != PublishType.timeseries) {
                    LOGGER.info(payLoad);
                }

                long i = 0;

                HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
                long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);

                if(deviceLastMessageTime != lastMessageReceivedTime) {
                    switch (publishType) {
                        case timeseries:
                            processTimeSeries(record, payLoad, processRecordsInput, true);
                            // updateDeviceTable(record.getPartitionKey());
                            break;
                        case cov:
                            processTimeSeries(record, payLoad, processRecordsInput, false);
                            // updateDeviceTable(record.getPartitionKey());
                            break;
                        case agent:
                            i =  agentUtil.processAgent( payLoad,agentName);
                            processLog(payLoad,agent.getId());
                            break;
                        case devicepoints:
                            devicePointsUtil.processDevicePoints(payLoad, orgId, agent.getId());
                            break;
                        case ack:
                            ackUtil.processAck(payLoad, agentName, orgId);
                            processLog(payLoad,agent.getId());
                            break;
                        case event:
                            boolean alarmCreated = eventUtil.processEvents(record.getApproximateArrivalTimestamp().getTime(), payLoad, record.getPartitionKey(),orgId,eventRules);
                            if (alarmCreated) {
                                processRecordsInput.getCheckpointer().checkpoint(record);
                            }
                            break;

                    }

                    dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                    deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                } else {
                    LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType + " data : " + data);
                }
                if ( i == 0 ) {

                    FacilioContext context = new FacilioContext();
                    context.put(AgentKeys.NAME, agentName);
                    FacilioChain updateAgentTable = TransactionChainFactory.updateAgentTable();
                    updateAgentTable.execute(context);



                        *//*GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.AGENT_TABLE).fields(FieldFactory.getAgentDataFields())
                                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()),agentName,StringOperators.IS))
                                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));
                        Map<String,Object> toUpdate = new HashMap<>();
                        toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
                        toUpdate.put(AgentKeys.STATE, 1);
                        toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, lastMessageReceivedTime);
                        genericUpdateRecordBuilder.update(toUpdate);*//*

                }
                DataProcessorUtil.updateAgentMessage(recordId,MessageStatus.PROCESSED);
            } catch (Exception e) {
                try {
                    if(FacilioProperties.isProduction()) {
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
        }*/
        // LOGGER.debug("TOTAL PROCESSOR DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
    }

    private FacilioAgent getFacilioAgent(String agentName) {
        FacilioAgent agent = new FacilioAgent();
        agent.setAgentName(agentName);
        agent.setAgentConnStatus(Boolean.TRUE);
        agent.setAgentState(1);
        agent.setInterval(15L);
        agent.setWritable(false);
        return agent;
    }

    private void processLog(JSONObject object,Long agentId){
        JSONObject payLoad = (JSONObject) object.clone();
        if(orgId==169L){
            LOGGER.info("payload "+payLoad);
        }
        if((payLoad.containsKey(AgentKeys.COMMAND_STATUS) || payLoad.containsKey(AgentKeys.CONTENT))){
            int connectionCount = -1;
            //checks for key status in payload and if it 'agent'-publishype
            if( payLoad.containsKey(AgentKeys.COMMAND_STATUS) && ! payLoad.containsKey(AgentKeys.COMMAND)){

                Long status = (Long)payLoad.remove(AgentKeys.COMMAND_STATUS);
                if((1 == status)){ // Connected block -- getting Connection count
                    payLoad.put(AgentKeys.COMMAND_STATUS,CommandStatus.CONNECTED.getKey());
                    payLoad.put(AgentKeys.COMMAND,ControllerCommand.connect.getCommand());
                    if(payLoad.containsKey(AgentKeys.CONNECTION_COUNT)) {
                        connectionCount = Integer.parseInt(payLoad.get(AgentKeys.CONNECTION_COUNT).toString());
                    }

                    if (connectionCount == -1) {
                        payLoad.put(AgentKeys.CONTENT, AgentContent.Connected.getKey());
                    } else {
                        if (connectionCount == 1) {
                            payLoad.put(AgentKeys.CONTENT, AgentContent.Restarted.getKey());
                            AgentUtil.putLog(payLoad,orgId, agentId,false);
                        }
                        payLoad.put(AgentKeys.CONTENT, AgentContent.Connected.getKey()+connectionCount);
                    }

                } else if(0 == status) { // disconnected block -
                    payLoad.put(AgentKeys.COMMAND_STATUS,CommandStatus.DISCONNECTED.getKey());
                    payLoad.put(AgentKeys.CONTENT, AgentContent.Disconnected.getKey());
                    payLoad.put(AgentKeys.COMMAND,ControllerCommand.connect.getCommand());
                }
                else{ // avoids any status pther than 0 and 1
                    LOGGER.info("Exception Occured, wrong status in payload.--"+payLoad);
                    return;
                }
            }
            else if(  (!payLoad.containsKey(AgentKeys.COMMAND)) && payLoad.containsKey(AgentKeys.CONTENT)){
                long checkOrgId = 152;
                if(orgId == checkOrgId){
                    LOGGER.info("debugging payload--With content alone-1-"+payLoad);
                }
                payLoad.put(AgentKeys.CONTENT,AgentContent.Subscribed.getKey());
                payLoad.put(AgentKeys.COMMAND,ControllerCommand.subscribe.getCommand());
                if(orgId == checkOrgId){
                    LOGGER.info("debugging payload--With content alone-2-"+payLoad);
                }
            }
            // ack type - so content is always msgid.
            else {
                LOGGER.info(" ackLog processing ");
                payLoad.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.MESSAGE_ID));
            }
            AgentUtil.putLog(payLoad,orgId,agentId,false);
        }
    }


    private void processTimeSeries(Record record, JSONObject payLoad, ProcessRecordsInput processRecordsInput, boolean isTimeSeries) throws Exception {
        long timeStamp= record.getApproximateArrivalTimestamp().getTime();
        long startTime = System.currentTimeMillis();
        // LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + " TIME::::" +timeStamp);
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        /*if (AccountUtil.getCurrentOrg().getId() == 146 ) {
            LOGGER.info("Payload in processor : "+payLoad);
        }*/
        //bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer(), isTimeSeries);
        long timeTaken = (System.currentTimeMillis() - startTime);
        if(timeTaken >  100000L){
            LOGGER.info("timetaken : "+timeTaken);
        }
    }

        /*private void updateDeviceTable(String deviceId) {
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
        }*/

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        // System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
    }
    }


