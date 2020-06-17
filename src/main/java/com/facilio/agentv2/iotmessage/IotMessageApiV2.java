package com.facilio.agentv2.iotmessage;

import com.amazonaws.services.iot.client.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.wms.message.WmsPublishResponse;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            handleSuccessNotification(iotMessage);
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
                wmsPublishResponse.publish(IotMessageApiV2.getIotData(iotMessage.getParentId()),null);
                // send notification
            }
        }
        //send notification
    }

    private static IotData getIotData(long parentId) throws Exception {
        FacilioModule iotDataModule = ModuleFactory.getIotDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(iotDataModule.getTableName())
                .select(FieldFactory.getIotDataFields())
                .andCondition(CriteriaAPI.getIdCondition(parentId,iotDataModule));
        Map<String, Object> map = selectRecordBuilder.fetchFirst();
        IotData iotData = FieldUtil.getAsBeanFromMap(map, IotData.class);
        IotMessage iotMessage = new IotMessage();
        iotMessage.setParentId(parentId);
        List<IotMessage> siblingIotMessages = getSiblingIotMessages(iotMessage);
        iotData.setMessages(siblingIotMessages);
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

    private static void updatePointAcks(IotMessage iotMessage) throws Exception {
        // {"identifier":"1_#_1002_#_0_#_192.168.1.2","agent":"iamcvijay","agentId":1,"networkNumber":0,"ipAddress":"192.168.1.2","id":3,"instanceNumber":1002,"type":1,"command":255,"timestamp":1577086482410,"points":
        // [{"controllerId":3,"instanceType":8,"id":1,"instanceNumber":1002},{"controllerId":3,"instanceType":10,"id":2,"instanceNumber":1},{"controllerId":3,"instanceType":16,"id":3,"instanceNumber":1}]}
        if (iotMessage != null) {
            FacilioCommand command = FacilioCommand.valueOf(iotMessage.getCommand());
            if ((command != FacilioCommand.SUBSCRIBE) && (command != FacilioCommand.CONFIGURE)) {
                return;
            }
            if (iotMessage.getMessageData().containsKey(AgentConstants.POINTS)) {
                JSONArray pointData = getPointDataFromIotMessage(iotMessage, command);
                List<Long> pointIds = new ArrayList<>();
                for (Object pointDatumObject : pointData) {
                    JSONObject pointDatum = (JSONObject) pointDatumObject;
                    if (pointDatum.containsKey(AgentConstants.ID)) {
                        pointIds.add((Long) pointDatum.get(AgentConstants.ID));
                    }
                }
                if (!pointIds.isEmpty()) {
                    switch (command) {
                        case CONFIGURE:
                            PointsAPI.updatePointsConfigurationComplete(pointIds);
                            return;
                        case SUBSCRIBE:
                            PointsAPI.updatePointSubsctiptionComplete(pointIds);
                            return;
                        default:
                            LOGGER.info(" no update for command ->" + command.toString());
                            return;
                    }
                } else {
                    throw new Exception(" point ids cant be empty while ack processing for->" + command.toString());
                }
            } else {
                throw new Exception(" points data missing from iotMessage data ->" + iotMessage.getMessageData());
            }
        } else {
            throw new Exception(" iot Message can't be null");
        }
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

    public static void addIotMessage(long parentId, List<IotMessage> messages) throws Exception {
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getIotMessageModule().getTableName())
                .fields(FieldFactory.getIotMessageFields());
        long createdTime = System.currentTimeMillis();
        for (IotMessage iotMessage : messages) {
            iotMessage.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            iotMessage.setParentId(parentId);
            iotMessage.setStatus(Status.MESSAGE_SENT.asInt());
            iotMessage.setSentTime(createdTime);
            iotMessage.setId(builder.insert(FieldUtil.getAsProperties(iotMessage)));
        }
    }


    public static IotMessage getIotMessage(Long id) throws Exception {
        List<IotMessage> result = getIotMessages(Collections.singletonList(id));
        if( (result != null) && (! result.isEmpty()) && (result.size()==1) ){
            return result.get(0);
        }else {
            throw new Exception(" unexpected result, cant have this many records ->"+result);
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
        for (IotMessage message : data.getMessages()) {
            LogsApi.logIotCommand(agentId,message.getId(),data.getFacilioCommand(),Status.MESSAGE_SENT);
            message.getMessageData().put("msgid", message.getId());
            publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), message.getMessageData());
            //FacilioContext context = new FacilioContext();
            //context.put(FacilioConstants.ContextNames.PUBLISH_DATA, data);
            //FacilioTimer.scheduleInstantJob("PublishedDataChecker", context);
        }
    }

    private static void publishIotMessage(String client, JSONObject object) throws Exception {
		if (!FacilioProperties.isProduction()) {
		    LOGGER.info(" not production "+object);
			return;
		}

        if (FacilioProperties.isOnpremise()) {
            publishToRabbitMQ(client, object);
        } else {
            publishToAwsIot(client, object);
        }
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

    public static void publishToAwsIot(String client, JSONObject object) throws Exception {
        String topic = client+"/msgs";
        LOGGER.info(FacilioProperties.getIotEndPoint() +" " + client+"-facilio" + " " + topic + " " + object);
        AWSIotMqttClient mqttClient = new AWSIotMqttClient(FacilioProperties.getIotEndPoint(), client+"-facilio", FacilioProperties.getIotUser(), FacilioProperties.getIotPassword());
        try {
            mqttClient.connect();
            if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
                mqttClient.publish(new AWSIotMessage(topic, AWSIotQos.QOS0, object.toJSONString()));
            }
        } catch (AWSIotException e) {
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
        List<Long> getIotDataIds = getIotDataIds(agentId);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(iotMessageModule.getTableName())
                .select(FieldFactory.getIotMessageFields());
        if( ! getIotDataIds.isEmpty()) {
            FacilioField parentIdField = FieldFactory.getAsMap(FieldFactory.getIotMessageFields()).get(AgentConstants.PARENT_ID);
            builder.andCondition(CriteriaAPI.getCondition(parentIdField,getIotDataIds,NumberOperators.EQUALS));
            JSONObject pagination = (JSONObject) paginationContext.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null ) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");

                int offset = ((page-1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }

                builder.offset(offset);
                builder.limit(perPage);
            }else {
    			builder.limit(50);
    		}
            builder.orderBy(AgentConstants.SENT_TIME + " DESC");
            return builder.get();
        }else {
            LOGGER.info(" No iot message for agent ->"+agentId);
            return new ArrayList<>();
        }
    }

    private static List<Long> getIotDataIds(long agentId) throws Exception {
        FacilioModule iotDataModule = ModuleFactory.getIotDataModule();
        FacilioField idField = FieldFactory.getIdField();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(iotDataModule.getTableName())
                .select(Collections.singletonList(idField));
        if(agentId > 0){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(iotDataModule), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        List<Long> ids = new ArrayList<>();
        List<Map<String, Object>> data = builder.get();
        if (data != null && (! data.isEmpty()) ) {
            for (Map<String, Object> datum : data) {
                ids.add((Long) datum.get(idField.getName()));
            }
        }
        return ids;
    }

    public static long getCount(Long agentId) throws Exception {
        FacilioModule iotMessageModule = ModuleFactory.getIotMessageModule();
        List<Long> iotDataIds = getIotDataIds(agentId);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(iotMessageModule.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(iotMessageModule))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishMessageParentIdField(iotMessageModule),iotDataIds,NumberOperators.EQUALS));
        List<Map<String, Object>> result = selectRecordBuilder.get();
        return (long) result.get(0).get(AgentConstants.ID);
    }
}
