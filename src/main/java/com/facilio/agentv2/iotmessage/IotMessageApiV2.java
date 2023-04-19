package com.facilio.agentv2.iotmessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.shadow.AwsIotDeviceCommandManager.Command;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.wms.message.WmsPublishResponse;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class IotMessageApiV2 {
    private static final Logger LOGGER = LogManager.getLogger(IotMessageApiV2.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

    public static boolean acknowdledgeMessage(long id, Status status) throws Exception {
        IotMessage iotMessage = getIotMessage(id);
        int rowUpdated = 0;
        Long currTime = System.currentTimeMillis();
        if(status == Status.MESSAGE_PROCESSING_SUCCESS){
            iotMessage.setCompletedTime(currTime);
        }
        iotMessage.setStatus(status.asInt());
        iotMessage.setAckTime(currTime);
        rowUpdated = updateIotMessage(iotMessage);
        try {
        		// Add this after handling in client properly
            //handleSuccessNotification(iotMessage);
        }catch (Exception e){
            LOGGER.info("Exception while handling success ",e);
        }
        if (rowUpdated > 0) {
            return true;
        }
        return false;
    }

    private static void handleSuccessNotification(IotMessage iotMessage) throws Exception {
        List<IotMessage> iotMessages = getSiblingIotMessages(iotMessage);
        if( ! iotMessages.isEmpty() ){
            long pendingCount = getPendingCount(iotMessages);
            if(pendingCount == 0){
                WmsPublishResponse wmsPublishResponse = new WmsPublishResponse();
                wmsPublishResponse.publish(IotMessageApiV2.getIotData(iotMessage.getParentId(), true),null);
                // send notification
            }
        }
        //send notification
    }

    public static IotData getIotData(long id, boolean fetchMessages) throws Exception {
        FacilioModule iotDataModule = ModuleFactory.getIotDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(iotDataModule.getTableName())
                .select(FieldFactory.getIotDataFields())
                .andCondition(CriteriaAPI.getIdCondition(id,iotDataModule));
        Map<String, Object> map = selectRecordBuilder.fetchFirst();
        IotData iotData = FieldUtil.getAsBeanFromMap(map, IotData.class);
        if (fetchMessages) {
        	IotMessage iotMessage = new IotMessage();
        	iotMessage.setParentId(id);
        	List<IotMessage> siblingIotMessages = getSiblingIotMessages(iotMessage);
        	iotData.setMessages(siblingIotMessages);
        }
        return iotData;
    }

    private static long getPendingCount(List<IotMessage> iotMessages) {
        long count = 0;
        for (IotMessage iotMessage : iotMessages) {
            if(iotMessage.getStatus() != Status.MESSAGE_PROCESSING_SUCCESS.asInt()){
                count++;
            }
        }
        return count;
    }

    private static List<IotMessage> getSiblingIotMessages(IotMessage iotMessage) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getIotMessageFields());
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(iotMessageModule.getTableName())
                .select(FieldFactory.getIotMessageFields())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.PARENT_ID), String.valueOf(iotMessage.getParentId()), NumberOperators.EQUALS));
        return FieldUtil.getAsBeanListFromMapList(builder.get(), IotMessage.class);
    }

    private static FacilioControllerType getControllerTypeFromIotMessage(IotMessage iotMessage) throws Exception {
        if (iotMessage.getMessageData().containsKey(AgentConstants.CONTROLLER_TYPE)) {
            return FacilioControllerType.valueOf((Integer) iotMessage.getMessageData().get(AgentConstants.CONTROLLER_TYPE));
        } else {
            throw new Exception(" iot message is not having controllerType");
        }
    }

    private static JSONArray getPointDataFromIotMessage(IotMessage iotMessage, FacilioCommand command) throws Exception {
        if (iotMessage.getMessageData().containsKey(AgentConstants.POINTS)) {
            JSONArray pointData = (JSONArray) iotMessage.getMessageData().get(AgentConstants.POINTS);
            if (pointData != null && (!pointData.isEmpty())) {
                return pointData;
            } else {
                throw new Exception(" pointData can't be null");
            }
        } else {
            throw new Exception(command.toString() + " no point data found for iot message ->" + command);
        }

    }


    public static long addIotData(IotData data) throws Exception {
        data.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        data.setCreatedTime(System.currentTimeMillis());
        long id = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getIotDataModule().getTableName())
                .fields(FieldFactory.getIotDataFields())
                .insert(FieldUtil.getAsProperties(data));
        data.setId(id);
        return id;
    }

    public static void addIotMessage(IotData data, List<IotMessage> messages) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getIotMessageModule().getTableName())
                .fields(FieldFactory.getIotMessageFields());
        long createdTime = System.currentTimeMillis();
        Map<Long, List<Long>> messageVsControls = new HashMap<>();
        for (IotMessage iotMessage : messages) {
            iotMessage.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            iotMessage.setParentId(data.getId());
            iotMessage.setStatus(Status.MESSAGE_SENT.asInt());
            iotMessage.setSentTime(createdTime);
            iotMessage.setAgentId(data.getAgentId());
            builder.addRecord(FieldUtil.getAsProperties(iotMessage));
        }
        builder.save();
        List<Map<String, Object>> records = builder.getRecords();
        for (int i = 0; i < records.size(); i++) {
            IotMessage iotMessage = messages.get(i);
            long id = (long) records.get(i).get(AgentConstants.ID);
            iotMessage.setId(id);
            if (iotMessage.getControlIds() != null) {
            	messageVsControls.put(id, iotMessage.getControlIds());
            }
        }

        if (!messageVsControls.isEmpty()) {
        	ControlActionUtil.updateControlMessageId(messageVsControls);
        }
    }


    public static IotMessage getIotMessage(Long id) throws Exception {
        List<IotMessage> result = getIotMessages(Collections.singletonList(id));
        if( (result != null) && (! result.isEmpty()) && (result.size()==1) ){
            return result.get(0);
        }else {
            throw new Exception(" unexpected result, cant have this many records ->"+result + ", id: " + id);
        }
    }

    private static List<IotMessage> getIotMessages(List<Long> ids) throws Exception {
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(iotMessageModule.getTableName())
                .select(FieldFactory.getIotMessageFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, iotMessageModule));
        List<Map<String, Object>> records = builder.get();
        if (records.isEmpty()) {
            return new ArrayList<>();
        } else {
            return FieldUtil.getAsBeanListFromMapList(records, IotMessage.class);
        }
    }


    public static int updateIotMessage(IotMessage iotMessage) throws Exception {
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        return new GenericUpdateRecordBuilder()
                .table(iotMessageModule.getTableName())
                .fields(FieldFactory.getIotMessageFields())
                .andCondition(CriteriaAPI.getIdCondition(iotMessage.getId(), iotMessageModule))
                .update(FieldUtil.getAsProperties(iotMessage));
    }


    public static void publishIotMessage(IotData data) throws Exception {
        long agentId = data.getAgentId();
        FacilioAgent agent = data.getAgent();
        MessageSource messageSource = AgentUtilV2.getMessageSource(agent);
        List<Map<String,Object>> topics = MessageQueueTopic.getTopics(Collections.singletonList(AccountUtil.getCurrentOrg().getOrgId()), messageSource);
        String topic = topics.get(0).get(AgentConstants.MESSAGE_TOPIC).toString();
        for (IotMessage message : data.getMessages()) {
            LogsApi.logIotCommand(agentId,message.getId(),data.getFacilioCommand(),Status.MESSAGE_SENT);
            message.getMessageData().put("msgid", message.getId());
            publishIotMessage(agent, topic, message, messageSource);
        }
    }

    private static void publishIotMessage(FacilioAgent agent, String client, IotMessage message, MessageSource messageSource) throws Exception {
    	JSONObject object = message.getMessageData();
        if (FacilioProperties.isOnpremise()) {
            publishToRabbitMQ(client, object);
        } else if (agent.getAgentTypeEnum().isAgentService()) {
            publishToAgentService(agent, client, object, (KafkaMessageSource) messageSource);
        } else {
            String topic = client + "/msgs";
            if (agent.getAgentTypeEnum() == AgentType.CUSTOM && object.containsKey("topic")) {
                topic = (String) object.remove("topic");
            }
            publishToAwsIot(client, topic, object);
        }
    }
    
    private static JSONObject executeCommandWorkflow(long workflowId, FacilioAgent agent, JSONObject object, FacilioCommand command) throws Exception {
    	WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(workflowId);

		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
		FacilioContext context = chain.getContext();

		List<Object> props = new ArrayList<>();
		props.add(object);
		props.add(FieldUtil.getAsProperties(agent));
		props.add(command.toString());

		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
		context.put(WorkflowV2Util.WORKFLOW_PARAMS, props);

		chain.execute();
		
		Map<String, Object> result = (Map<String, Object>) workflowContext.getReturnValue();
		if (result != null) {
			return FacilioUtil.parseJson(JSONObject.toJSONString(result));
		}
		return null;
    }

    private static void publishToAgentService(FacilioAgent agent, String client, JSONObject payload, KafkaMessageSource messageSource) {
        String topic = client + "-cmds";
        AgentUtilV2.publishToQueue(topic, agent.getName(), payload, messageSource);
    }

    public static void publishToRabbitMQ(String client, JSONObject object) throws Exception {
            String topic = client + ".msgs";
            LOGGER.info(FacilioProperties.getIotEndPoint() + " " + client + "-facilio" + " " + topic + " " + object);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(FacilioProperties.getIotEndPoint());
            factory.setUsername(FacilioProperties.getIotUser());
            factory.setPassword(FacilioProperties.getIotPassword());
            factory.setVirtualHost(FacilioProperties.getIotVirtualHost());
            factory.setPort(FacilioProperties.getIotEndPointPort());
            factory.setChannelRpcTimeout(10000);
            factory.setAutomaticRecoveryEnabled(false);
            factory.setChannelShouldCheckRpcResponseType(false);
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                String message = object.toString();
                channel.basicPublish(FacilioProperties.getIotExchange(), topic, null, message.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.info("Exception while publishing to rabbitmq, ", e);
                throw e;
            }
    }

    public static void publishToAwsIot(String client, String topic, JSONObject object) throws Exception {
        LOGGER.info(FacilioProperties.getIotEndPoint() +" " + "facilio-"+client + " " + topic + " " + object);
        AWSIotMqttClient mqttClient = new AWSIotMqttClient(FacilioProperties.getIotEndPoint(), "facilio-"+client, FacilioProperties.getIotUser(), FacilioProperties.getIotPassword());
        try {
            if (AccountUtil.getCurrentOrg().getOrgId() == 486) {
                LOGGER.info("Connections status " + mqttClient.getConnectionStatus().name());
                if (mqttClient.getConnectionStatus() != AWSIotConnectionStatus.CONNECTED) {
                    mqttClient.connect();
                }
            } else {
                mqttClient.connect();
            }
            LOGGER.info("Connected successfully");
            if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
                LOGGER.info("Publishing message");
                mqttClient.publish(new AWSIotMessage(topic, AWSIotQos.QOS0, object.toJSONString()));
            }
        } catch (AWSIotException e) {
            LOGGER.error("Excetion message : " + e.getMessage() + " : " + e.getErrorCode());
            LOGGER.error("Exception while publishing message ", e);
            throw e;
        } finally {
            try {
                if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
                    mqttClient.disconnect();
                }
            } catch (AWSIotException e) {
                LOGGER.error("Exception while disconnecting ", e);
            }
        }
    }

    public static List<Map<String, Object>> listIotMessages(long agentId, FacilioContext paginationContext) throws Exception {
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        FacilioModule iotDataModule = ModuleFactory.getIotDataModule();
        JSONObject pagination = (JSONObject) paginationContext.get(FacilioConstants.ContextNames.PAGINATION);
        if(agentId > 0) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(iotMessageModule.getTableName())
                    .select(FieldFactory.getIotMessageFields())
                    .innerJoin(iotDataModule.getTableName()).on(iotDataModule.getTableName() + ".ID=" + iotMessageModule.getTableName() + ".PARENT_ID")
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(iotDataModule), String.valueOf(agentId), NumberOperators.EQUALS));
            if (pagination != null ) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");

                int offset = ((page-1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }

                selectRecordBuilder.offset(offset);
                selectRecordBuilder.limit(perPage);
            }else {
                selectRecordBuilder.limit(50);
            }
            selectRecordBuilder.orderBy(AgentConstants.SENT_TIME + " DESC");
            return selectRecordBuilder.get();
        }
        else {
            LOGGER.info(" No iot message for agent ->"+agentId);
            return new ArrayList<>();
        }
    }
    public static long getCount(Long agentId) throws Exception {
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        FacilioModule iotDataModule = ModuleFactory.getIotDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(iotMessageModule.getTableName())
                .innerJoin(iotDataModule.getTableName()).on(iotDataModule.getTableName() + ".ID=" + iotMessageModule.getTableName() + ".PARENT_ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(iotDataModule), String.valueOf(agentId), NumberOperators.EQUALS))
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(iotMessageModule));
        List<Map<String, Object>> result = selectRecordBuilder.get();
        return (long) result.get(0).get(AgentConstants.ID);
    }
}
