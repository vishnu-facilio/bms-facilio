package com.facilio.agentv2.iotmessage;

import com.amazonaws.services.iot.client.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotMessageApiV2
{
    private static final Logger LOGGER = LogManager.getLogger(IotMessageApiV2.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

    public static boolean acknowdledgeMessage (long id, Status status, JSONObject payLoad) throws Exception {
        LOGGER.info(" processing ak in NewIotMEssageAPI");
        int rowUpdated = 0;
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getIotMessageFields());
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldMap.get(AgentConstants.STATUS));
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(fields.get(0).getName(), status.asInt());
        Long currTime = System.currentTimeMillis();
        switch (status){
            case MESSAGE_RECEIVED:
                toInsert.put(AgentConstants.ACK_TIME,currTime);
                fields.add(fieldMap.get(AgentConstants.ACK_TIME));
                rowUpdated = updateIotMessage(id,fields,toInsert);
                break;
            case MESSAGE_PROCESSING:
            case MESSAGE_PROCESSING_FAILED:
            case MESSAGE_PROCESSING_SUCCESS:
                toInsert.put(AgentConstants.COMPLETED_TIME,currTime);
                fields.add(fieldMap.get(AgentConstants.COMPLETED_TIME));
                rowUpdated = updateIotMessage(id,fields,toInsert);
                break;
        }
        return rowUpdated > 0;
    }



        public static long addIotData(IotData data) throws Exception{
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
        for (IotMessage iotMessage : messages) {
            LOGGER.info(" adding message "+messages);
            iotMessage.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
            iotMessage.setParentId(parentId);
            iotMessage.setId(builder.insert(FieldUtil.getAsProperties(iotMessage)));
        }
    }

    public static int updateIotMessage(long msgId, List<FacilioField> fields, Map<String,Object> map) throws Exception{
        return new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getIotMessageModule().getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition(msgId,ModuleFactory.getIotMessageModule()))
                .update(map);
    }


    public static void publishIotMessage(IotData data) throws Exception {
        for(IotMessage message : data.getMessages()) {
            message.getMessageData().put("msgid", message.getId());
            publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), message.getMessageData());
            //FacilioContext context = new FacilioContext();
            //context.put(FacilioConstants.ContextNames.PUBLISH_DATA, data);
            //FacilioTimer.scheduleInstantJob("PublishedDataChecker", context);
        }
    }

    private static void publishIotMessage(String client, JSONObject object) throws Exception {
		/*if (!FacilioProperties.isProduction()) {
			return;
		}*/

        Long agentId;
        if(object.containsKey(AgentKeys.AGENT_ID)){
            agentId = (Long) object.remove(AgentKeys.AGENT_ID);
        }
        else if (object.containsKey(AgentConstants.AGENT_ID)){
            agentId = (Long) object.get(AgentConstants.AGENT_ID);
        }
        else {
            return;
        }
        LOGGER.info(" Iot Message is "+object);
        if( FacilioProperties.isOnpremise()) {
            //publishToRabbitMQ(client, object);
        } else {
            //publishToAwsIot(client, object);
        }
		/*if(agentId != null && agentId > 0) {
			AgentUtil.putLog(object, AccountUtil.getCurrentOrg().getOrgId(), agentId, true);
		}*/

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
