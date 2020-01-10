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
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class IotMessageApiV2 {
    private static final Logger LOGGER = LogManager.getLogger(IotMessageApiV2.class.getName());

    private static final FacilioModule MODULE = ModuleFactory.getIotMessageModule();

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

    public static boolean acknowdledgeMessage(long id, Status status) throws Exception {
        LOGGER.info(" processing ak in NewIotMEssageAPI");
        int rowUpdated = 0;
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getIotMessageFields());
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldMap.get(AgentConstants.STATUS));
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(fields.get(0).getName(), status.asInt());
        Long currTime = System.currentTimeMillis();
        switch (status) {
            case MESSAGE_PROCESSING_SUCCESS:
                toInsert.put(AgentConstants.COMPLETED_TIME, currTime);
                toInsert.put(AgentConstants.ACK_TIME, currTime);
                fields.add(fieldMap.get(AgentConstants.COMPLETED_TIME));
                rowUpdated = updateIotMessage(id, fields, toInsert);
                break;
            case MESSAGE_PROCESSING:
            case MESSAGE_PROCESSING_FAILED:
            case MESSAGE_RECEIVED:
                toInsert.put(AgentConstants.ACK_TIME, currTime);
                fields.add(fieldMap.get(AgentConstants.ACK_TIME));
                rowUpdated = updateIotMessage(id, fields, toInsert);
                break;


        }
        LOGGER.info(" updating message for ack ->" + rowUpdated);
        if (rowUpdated > 0) {
            updatePointAcks(id);
            return true;
        }
        return false;
    }

    private static void updatePointAcks(long id) throws Exception {
        // {"identifier":"1_#_1002_#_0_#_192.168.1.2","agent":"iamcvijay","agentId":1,"networkNumber":0,"ipAddress":"192.168.1.2","id":3,"instanceNumber":1002,"type":1,"command":255,"timestamp":1577086482410,"points":
        // [{"controllerId":3,"instanceType":8,"id":1,"instanceNumber":1002},{"controllerId":3,"instanceType":10,"id":2,"instanceNumber":1},{"controllerId":3,"instanceType":16,"id":3,"instanceNumber":1}]}
        IotMessage iotMessage = getIotMessage(id);
        LOGGER.info("updating pointAcks iot message ->" + iotMessage);
        if (iotMessage != null) {
            FacilioCommand command = FacilioCommand.valueOf(iotMessage.getCommand());
            if ((command != FacilioCommand.SUBSCRIBE) && (command != FacilioCommand.CONFIGURE)) {
                return;
            }
            if (iotMessage.getMessageData().containsKey(AgentConstants.POINTS)) {
                JSONArray pointData = getPointDataFRomIotMessage(iotMessage, command);
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
        if (iotMessage.getMessageData().containsKey(AgentConstants.TYPE)) {
            return FacilioControllerType.valueOf((Integer) iotMessage.getMessageData().get(AgentConstants.TYPE));
        } else {
            throw new Exception(" iot message is not having controllerType");
        }
    }

    private static JSONArray getPointDataFRomIotMessage(IotMessage iotMessage, FacilioCommand command) throws Exception {
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
        LOGGER.info(" adding iot data");
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
            LOGGER.info(" adding message " + FieldUtil.getAsProperties(iotMessage));
            iotMessage.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            iotMessage.setParentId(parentId);
            iotMessage.setSentTime(createdTime);
            iotMessage.setId(builder.insert(FieldUtil.getAsProperties(iotMessage)));
        }
    }

    public static IotMessage getIotMessage(long id) throws Exception {
        List<IotMessage> iotmessages = getIotMessages(Collections.singletonList(id));
        if ((!iotmessages.isEmpty())) {
            if ((iotmessages.size() == 1)) {
                return iotmessages.get(0);
            } else {
                throw new Exception("unexpected results, can't get two records");
            }
        } else {
            return null;
        }
    }

    private static List<IotMessage> getIotMessages(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FieldFactory.getIotMessageFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, MODULE));
        List<Map<String, Object>> records = builder.get();
        if (records.isEmpty()) {
            return new ArrayList<>();
        } else {
            return FieldUtil.getAsBeanListFromMapList(records, IotMessage.class);
        }
    }

    public static int updateIotMessage(long msgId, List<FacilioField> fields, Map<String, Object> map) throws Exception {
        return new GenericUpdateRecordBuilder()
                .table(MODULE.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(msgId, MODULE))
                .update(map);
    }


    public static void publishIotMessage(IotData data) throws Exception {
        long agentId = data.getAgentId();
        for (IotMessage message : data.getMessages()) {
            LogsApi.logIotCommand(agentId,message.getId(),data.getFacilioCommand(),null);
            message.getMessageData().put("msgid", message.getId());
            publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), message.getMessageData());
            //FacilioContext context = new FacilioContext();
            //context.put(FacilioConstants.ContextNames.PUBLISH_DATA, data);
            //FacilioTimer.scheduleInstantJob("PublishedDataChecker", context);
        }
    }

    private static void publishIotMessage(String client, JSONObject object) throws Exception {
		if (!FacilioProperties.isProduction()) {
			return;
		}

       /* LOGGER.info(" Iot Message is "+object);
        if(true){ //TODO to be removed
            LOGGER.info(" skipping iot comm for local testing ");
            return;
        }*/
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

}
