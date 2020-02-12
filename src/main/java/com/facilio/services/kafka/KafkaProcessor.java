package com.facilio.services.kafka;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.*;
import com.facilio.agentv2.DataProcessorV2;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.server.ServerInfo;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Renamed from Processor
public class KafkaProcessor extends FacilioProcessor {
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private EventUtil eventUtil;
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private long orgId;
    private String orgDomainName;
    private static final Logger LOGGER = LogManager.getLogger(KafkaProcessor.class.getName());
    private DataProcessorV2 dataProcessorV2;
    private DataProcessorUtil dataProcessorUtil;

    public KafkaProcessor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        String clientName = orgDomainName + "-processor-";
        String environment = FacilioProperties.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap(null,null);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        setEventType("processor");
        try {
            dataProcessorV2 = new DataProcessorV2(orgId, orgDomainName);
        }catch (Exception e){
            dataProcessorV2 = null;
            LOGGER.info(" Exception occurred ",e);
        }
        dataProcessorUtil = new DataProcessorUtil(orgId,orgDomainName);
        LOGGER.info("Initializing processor " + orgDomainName);
    }


    @Override
    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
            String recordId = record.getId();
            LOGGER.info(" getting messages via kafka "+recordId);
            /*if( ( DataProcessorUtil.isIsRestarted()) && ( DataProcessorUtil.isDuplicate(record.getId())) ) {
                try {
                    FacilioRecord record1 = new FacilioRecord("0",new JSONObject());
                    record1.setId(DataProcessorUtil.getLastRecordChecked());
                    getConsumer().commit(record);
                    DataProcessorUtil.setIsRestarted(false);
                    return;
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while making new checkPointer",e);
                }
            }*/

            try {
                AccountUtil.getCurrentAccount().clearStateVariables();
                if (!dataProcessorUtil.processRecord(record)) {
                    LOGGER.info("Exception while processing ->" + record.getData());
                }
                getConsumer().commit(record);
            } catch (Exception e){
                LOGGER.info("Exception occurred while processing  ",e);
            } finally {
                Account account = AccountUtil.getCurrentAccount();
                String message = " select: " + account.getSelectQueries() + " time: " + account.getSelectQueriesTime() +
                        " update: " + account.getUpdateQueries() + " time: " + account.getUpdateQueriesTime() +
                        " insert: " + account.getInsertQueries() + " time: " + account.getInsertQueriesTime() +
                        " delete: " + account.getDeleteQueries() + " time: " + account.getDeleteQueriesTime() +
                        " rget: " + account.getRedisGetCount() + " time: " + account.getRedisGetTime() +
                        " rput: " + account.getRedisPutCount() + " time: " + account.getRedisPutTime() +
                        " rdel: " + account.getRedisDeleteCount() + " time: " + account.getRedisDeleteTime();
                LOGGER.info("record : " + recordId + message);
            }



            /*boolean alarmCreated = false;
            long numberOfRows = 0;
            try {

                String data = "";
                data = record.getData().toString();
                if (data.isEmpty()) {
                    LOGGER.info(" Empty message received "+recordId);
                    DataProcessorUtil.updateAgentMessage(recordId, MessageStatus.DATA_EMPTY);
                    continue;
                }
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                List<EventRuleContext> ruleList = bean.getActiveEventRules();
                if (ruleList != null) {
                    eventRules = ruleList;
                }

                JSONObject payLoad = (JSONObject) parser.parse(data);
                if(payLoad.containsKey(AgentConstants.VERSION) && ( ("2".equalsIgnoreCase((String)payLoad.get(AgentConstants.VERSION))))){
                    if(processorV2 != null){
                        LOGGER.info(" newProcekinesisssor payload -> "+payLoad);
                        try {
                            processorV2.processNewAgentData(payLoad);
                        }catch (Exception newProcessorException){
                            LOGGER.info("Exception occurred ",newProcessorException);
                        }
                    }
                    continue;
                }

                String dataType = event.getValue();
                if (payLoad.containsKey(EventUtil.DATA_TYPE)) {
                    dataType = (String) payLoad.remove(EventUtil.DATA_TYPE);
                }
                // Temp fix - bug: Publish_Type wrongly set to "agents"
                if("agents".equals(dataType)){
                    dataType = PublishType.agent.getValue();
                }
                PublishType publishType = PublishType.valueOf(dataType);
                String wattsenseAgentName = record.getPartitionKey();
                String agentName = orgDomainName.trim();
                if (payLoad.containsKey(PublishType.agent.getValue())) {
                    agentName = payLoad.remove(PublishType.agent.getValue()).toString().trim();
                }

                String deviceId = orgDomainName;
                if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
                    deviceId = payLoad.get(AgentKeys.DEVICE_ID).toString();
                }

                long lastMessageReceivedTime = System.currentTimeMillis();
                if (payLoad.containsKey(AgentKeys.TIMESTAMP)) {
                    Object lastTime = payLoad.get(AgentKeys.TIMESTAMP);
                    lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
                }

                FacilioAgent agent = agentUtil.getFacilioAgent(agentName,null);
                //com.facilio.agentnew.FacilioAgent agent = au.getFacilioAgent(agentName);
                if (agent == null ) {
                    agent = getFacilioAgent(agentName);
                    long agentId = agentUtil.addAgent(agent);
                    if(agentId < 1L) {
                        LOGGER.info(" Error in AgentId generation ");
                        continue;
                    }
                    agent.setId(agentId);
                }
                //cU = getControllerUtil(agent.getId());
                int dataLength = data.length();
                HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
                long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);

                if (deviceLastMessageTime != lastMessageReceivedTime) {
                    String partitionKey = "";
                    try {
                        partitionKey = record.getPartitionKey();
                        if (dataType != null) {
                            switch (publishType) {
                                case timeseries:
                                    processTimeSeries(record);
                                    break;
                                case devicepoints:
                                    *//*if(payLoad.containsKey(AgentConstants.CONTROLLER) && payLoad.containsKey(AgentConstants.CONTROLLER_TYPE) ){ // change name to controller
                                        String controllerIdentifier = String.valueOf(payLoad.get(AgentConstants.CONTROLLER));
                                        if(controllerIdentifier != null){
                                            BmsController controller = cU.getController(agent.getId(),controllerIdentifier, FacilioControllerType.valueOf(Math.toIntExact((Long) payLoad.get(AgentConstants.CONTROLLER_TYPE))));
                                            if(controller == null){
                                                LOGGER.info(" Exception occurred, No such Controller ");
                                                continue;
                                            }
                                            payLoad.put(AgentConstants.DEVICE_NAME,controllerIdentifier);
                                            PointsUtil pU = new PointsUtil(agent.getId(),controller.getId());
                                            pU.processPoints(payLoad,controller);
                                            break;
                                        }
                                    }else {
                                        LOGGER.info(" Exception occurred , payload is missing " + AgentConstants.CONTROLLER + "," + AgentConstants.CONTROLLER_TYPE);
                                    }*//*
                                    devicePointsUtil.processDevicePoints(payLoad, orgId, agent.getId());
                                    break;
                                case ack:
                                    ackUtil.processAck(payLoad, agentName, orgId);
                                    processLog(payLoad,agent.getId(),recordId);
                                    break;
                                case agent:
                                    numberOfRows = agentUtil.processAgent(payLoad,agentName);
                                    processLog(payLoad,agent.getId(),recordId);
                                    break;
                                case event:
                                    alarmCreated = eventUtil.processEvents(record.getTimeStamp(), payLoad, record.getPartitionKey(), orgId, eventRules);
                                    break;
                               *//* case controllers:
                                    LOGGER.info(" iamcvijaylogs   payload at case controllers "+payLoad);
                                    dU.processDevices(agent,payLoad);*//*
                            }
                            dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                            deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                        }if (numberOfRows == 0) {
                            FacilioChain updateAgentTable = TransactionChainFactory.updateAgentTable();
                            FacilioContext context = updateAgentTable.getContext();
                            context.put(AgentKeys.NAME, agentName);
                            updateAgentTable.execute();

                            *//*GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.AGENT_TABLE).fields(FieldFactory.getAgentDataFields())
                                  .andCondition()
                                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));*//*

                            *//*Map<String, Object> toUpdate = new HashMap<>();
                            toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                            toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
                            toUpdate.put(AgentKeys.STATE, 1);*//*

                            //genericUpdateRecordBuilder.update(toUpdate);



                        }
                        agentUtil.addAgentMetrics(dataLength, agent.getId(), publishType.getKey());
                        DataProcessorUtil.updateAgentMessage(recordId,MessageStatus.PROCESSED);
                    }

                    catch (Exception e) {
                        CommonCommandUtil.emailException("Processor", "Error in processing records ", e, payLoad.toJSONString());
                        LOGGER.info("Exception occurred ", e);
                    } finally {
                        if (alarmCreated) {
                            getConsumer().commit(record);
                        }
                    }
                }
                else {
                    LOGGER.info("Duplicate message for device " + deviceId + " and type " + dataType);
                }
            }
            catch (Exception e) {
                LOGGER.info("Exception occured", e);
            }
            */

        }
    }

    private void processLog(JSONObject object,Long agentId,String recordId){
        JSONObject payLoad = (JSONObject) object.clone();
        if((payLoad.containsKey(AgentKeys.COMMAND_STATUS) || payLoad.containsKey(AgentKeys.CONTENT))){
            int connectionCount = -1;
            //checks for key status in payload and if it is of 'agent'-publishype
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
                        if (connectionCount == 1) { // first connection after reconnect - add restarted first
                            payLoad.put(AgentKeys.CONTENT, AgentContent.Restarted.getKey());
                            AgentUtil.putLog(payLoad,orgId, agentId,false);
                        } //add another with connection count
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
            // agent type - with message alone - temp fix
            else if(  (!payLoad.containsKey(AgentKeys.COMMAND)) && payLoad.containsKey(AgentKeys.CONTENT)){
                payLoad.put(AgentKeys.CONTENT,AgentContent.Subscribed.getKey());
                payLoad.put(AgentKeys.COMMAND,ControllerCommand.subscribe.getCommand());
            }
            // ack type - so content is always msgid.
            else {
                payLoad.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.MESSAGE_ID));
            }
            AgentUtil.putLog(payLoad,orgId,agentId,false);
        }
    }

    private void processTimeSeries(FacilioRecord record) throws Exception {
        long orgCheck = 78;
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",orgId);
        bean.processTimeSeries(record);
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


}
