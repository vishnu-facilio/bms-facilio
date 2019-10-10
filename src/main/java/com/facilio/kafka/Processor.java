package com.facilio.kafka;

import com.facilio.agent.*;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.devicepoints.DevicePointsUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
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

import static com.facilio.agent.PublishType.event;


public class Processor extends FacilioProcessor {
    private AgentUtil agentUtil;
    //private com.facilio.agentnew.AgentUtil au;
    private DevicePointsUtil devicePointsUtil;
    private AckUtil ackUtil;
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private EventUtil eventUtil;
    private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();
    private long orgId;
    private String orgDomainName;
    private Boolean isStage = !FacilioProperties.isProduction();
    private JSONParser parser = new JSONParser();
    //private ControllerUtil cU;
    //private DeviceUtil dU;
    //private Map<Long,ControllerUtil> agentIdControllerUtilMap = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());

    private boolean isRestarted = true;


    public Processor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        String clientName = orgDomainName + "-processor-";
        String environment = FacilioProperties.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        agentUtil = new AgentUtil(orgId, orgDomainName);
        //au = new com.facilio.agentnew.AgentUtil(orgId,orgDomainName);
        //au.populateAgentContextMap(null);
        agentUtil.populateAgentContextMap(null,null);
        devicePointsUtil = new DevicePointsUtil();
        ackUtil = new AckUtil();
        eventUtil = new EventUtil();
        //dU = new DeviceUtil();
        setEventType("processor");
        LOGGER.info("Initializing processor " + orgDomainName);
    }


    /*public ControllerUtil getControllerUtil(long agentId){
        ControllerUtil cU;
        if(agentIdControllerUtilMap.containsKey(agentId)){
            LOGGER.info(" returning existing controllerUtil");
            cU = agentIdControllerUtilMap.get(agentId);
        }else {
            LOGGER.info(" creating new controllerUtil");
            cU = new ControllerUtil(agentId);
            agentIdControllerUtilMap.put(agentId,cU);
        }
        return cU;
    }*/

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
                        boolean originalFlag =false;
                                originalFlag = agentUtil.addAgentMessage(recordId);
                        if(!originalFlag){
                            LOGGER.info("tried adding duplicate message "+ recordId);
                            continue;
                        }
                    }
                }catch (Exception e1){
                    LOGGER.info("Exception Occured while adding agentMessage so, skipping record ",e1);
                    continue;
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
                String wattsenseAgentName = record.getPartitionKey();
               /* LOGGER.info(" wattsense log partitionKey - "+wattsenseAgentName);
                if(wattsenseAgentName != null){
                    FacilioAgent wattAgent = agentUtil.getFacilioAgent(wattsenseAgentName);
                    if(wattAgent != null) {
                        LOGGER.info("wattsense log agentId - " + wattAgent.getId());
                    }else{
                        LOGGER.info("wattsense log agent null ");
                    }
                }*/
                String agentName = orgDomainName.trim();
                /*if(  ( record.getPartitionKey() != null ) &&  ! record.getPartitionKey().equals(orgDomainName.trim())){
                    agentName = record.getPartitionKey();
                }*/
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
                                    /*if(payLoad.containsKey(AgentConstants.CONTROLLER) && payLoad.containsKey(AgentConstants.CONTROLLER_TYPE) ){ // change name to controller
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
                                    }*/
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
                               /* case controllers:
                                    LOGGER.info(" iamcvijaylogs   payload at case controllers "+payLoad);
                                    dU.processDevices(agent,payLoad);*/
                            }
                            dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
                            deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
                        }if (numberOfRows == 0) {
                            FacilioContext context = new FacilioContext();
                            context.put(AgentKeys.NAME, agentName);
                            FacilioChain updateAgentTable = TransactionChainFactory.updateAgentTable();
                            updateAgentTable.execute(context);

                            /*GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.AGENT_TABLE).fields(FieldFactory.getAgentDataFields())
                                  .andCondition()
                                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));*/

                            /*Map<String, Object> toUpdate = new HashMap<>();
                            toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                            toUpdate.put(AgentKeys.CONNECTION_STATUS, Boolean.TRUE);
                            toUpdate.put(AgentKeys.STATE, 1);*/

                            //genericUpdateRecordBuilder.update(toUpdate);



                        }
                        if( agent != null) {
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

    /*private com.facilio.agentnew.FacilioAgent getFacilioAgent(JSONObject payload, String agentName) {
        LOGGER.info(" \n\n ------------------------\n-------------------------\n");
        if (au.processAgent(payload,agentName)>0) {
            LOGGER.info("\n-\n-\n process agent postive -\n-\n-\n");
            return au.getFacilioAgent(agentName);
        }
        return null;
    }*/
}
