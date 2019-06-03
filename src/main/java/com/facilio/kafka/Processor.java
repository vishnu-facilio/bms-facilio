package com.facilio.kafka;

import com.facilio.agent.*;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.agent.PublishType.event;


public class Processor extends FacilioProcessor {
    private AgentUtil agentUtil;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private EventUtil eventUtil;
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private long orgId;
    private String orgDomainName;
    private Boolean isStage = !AwsUtil.isProduction();
    private JSONParser parser = new JSONParser();


    private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());

    private boolean isRestarted = true;



    public Processor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        String clientName = orgDomainName + "-processor-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        agentUtil = new AgentUtil(orgId, orgDomainName);
        agentUtil.populateAgentContextMap(null);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        setEventType("processor");
        LOGGER.info("Initializing processor " + orgDomainName);
    }



    @Override
    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
                boolean alarmCreated = false;
                long numberOfRows = 0;
                String recordId = record.getId();
            try {

                try {
                    boolean  isDuplicateMessage = agentUtil.isDuplicate(recordId);
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
                        agentUtil.addAgentMessage(recordId);
                    }
                }catch (Exception e1){
                    LOGGER.info("Exception Occured ",e1);
                }
                String data = "";
                data = record.getData().toString();
                if (data.isEmpty()) {
                    LOGGER.info(" Empty message received "+recordId);
                    agentUtil.updateAgentMessage(recordId, MessageStatus.DATA_EMPTY);
                    continue;
                }
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                List<EventRuleContext> ruleList = bean.getActiveEventRules();
                if (ruleList != null) {
                    eventRules = ruleList;
                }

                JSONObject payLoad = (JSONObject) parser.parse(data);


                String dataType = event.getValue();
                if (payLoad.containsKey(EventUtil.DATA_TYPE)) {
                    dataType = (String) payLoad.remove(EventUtil.DATA_TYPE);
                }
                // Temp fix - bug: Publish_Type wrongly set to "agents"
                if("agents".equals(dataType)){
                    dataType = PublishType.agent.getValue();
                }
                //Temp fix  - bug: Publish_Type wrongly set to "agents"
                PublishType publishType = PublishType.valueOf(dataType);
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

                FacilioAgent agent = agentUtil.getFacilioAgent(agentName);
                if (agent == null ) {
                    agent = getFacilioAgent(agentName);
                    agent.setId(agentUtil.addAgent(agent));
                }
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
                                    devicePointsUtil.processDevicePoints(payLoad, orgId, agent.getId());
                                    break;
                                case ack:
                                    ackUtil.processAck(payLoad, orgId);
                                    processLog(payLoad,agent.getId());
                                    break;
                                case agent:
                                    numberOfRows = agentUtil.processAgent(payLoad,agentName);
                                    processLog(payLoad,agent.getId());
                                    break;
                                case event:
                                    alarmCreated = eventUtil.processEvents(record.getTimeStamp(), payLoad, record.getPartitionKey(), orgId, eventRules);
                                    break;
                            }
                            dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                            deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                        }if (numberOfRows == 0) {
                            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.AGENT_TABLE).fields(FieldFactory.getAgentDataFields())
                                  .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()),agentName,StringOperators.IS))
                                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));
                            Map<String, Object> toUpdate = new HashMap<>();
                            toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                            toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
                            toUpdate.put(AgentKeys.STATE, 1);
                            genericUpdateRecordBuilder.update(toUpdate);

                        }
                        if(isStage && agent != null) {
                            agentUtil.addAgentMetrics(dataLength, agent.getId(), publishType.getKey());
                        }

                        agentUtil.updateAgentMessage(recordId,MessageStatus.PROCESSED);
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

        }
    }

    private void processLog(JSONObject payLoad,Long agentId){

        if(isStage && (payLoad.containsKey(AgentKeys.COMMAND_STATUS) || payLoad.containsKey(AgentKeys.CONTENT))){
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
            // agent type - with message alone - temp fix
            else if(payLoad.containsKey(AgentKeys.CONTENT)){
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
                long checkOrgId = 152;
                if(orgId == checkOrgId){
                    LOGGER.info("debugging payload--ACK--"+payLoad);
                }
                payLoad.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.MESSAGE_ID));
            }
            AgentUtil.putLog(payLoad,orgId,agentId,false);
        }
    }

    private void processTimeSeries(FacilioRecord record) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",orgId);
        bean.processTimeSeries(getConsumer(), record);
    }

    private FacilioAgent getFacilioAgent(String agentName) {
        FacilioAgent agent = new FacilioAgent();
        agent.setAgentName(agentName);
        agent.setAgentConnStatus(Boolean.TRUE);
        agent.setAgentState(1);
        agent.setAgentDataInterval(15L);
        agent.setWritable(false);
        return agent;
    }
}
