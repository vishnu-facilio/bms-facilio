package com.facilio.bmsconsole.util;

import com.amazonaws.services.iot.client.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentKeys.AckMessageType;
import com.facilio.agent.AgentUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.serializable.SerializableConsumer;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.message.WmsPublishResponse;
import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.SQLException;
import java.util.*;

public class IoTMessageAPI {
	private static final Logger LOGGER = LogManager.getLogger(IoTMessageAPI.class.getName());
	
	private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB
	private static Boolean isStage = !AwsUtil.isProduction();
	
	private static PublishData constructIotMessage (List<Map<String, Object>> instances, IotCommandType command) throws Exception {
		return constructIotMessage((long) instances.get(0).get("controllerId"), command, instances, null);
	}
	
	private static PublishData constructIotMessage (long controllerId, JSONObject property) throws Exception {
		return constructIotMessage(controllerId, IotCommandType.PROPERTY, null, property);
	}
	
	private static PublishData constructIotMessage (long controllerId, IotCommandType command) throws Exception {
		return constructIotMessage(controllerId, command, null, null);
	}
	
	@SuppressWarnings("unchecked")
	private static PublishData constructIotMessage (long controllerId, IotCommandType command, List<Map<String, Object>> instances, JSONObject property) throws Exception {
		ControllerContext controller = ControllerAPI.getController(controllerId);
		
		PublishData data = new PublishData();
		data.setControllerId(controller.getId());
		data.setCommand(command);
		
		JSONObject object = new JSONObject();
		object.put("command", command.getName());
		object.put("deviceName", controller.getName());
		object.put("macAddress", controller.getMacAddr());
		object.put("subnetPrefix", controller.getSubnetPrefix());
		object.put("networkNumber", controller.getNetworkNumber());
		object.put("instanceNumber", controller.getInstanceNumber());
		object.put("broadcastAddress", controller.getBroadcastIp());
		object.put("clientId", controller.getId());
		
		if (command == IotCommandType.PROPERTY) {
			object.putAll(property);
		}
		else if (command != IotCommandType.DISCOVER && instances != null && !instances.isEmpty()) {
			
			JSONArray points = new JSONArray();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map<String, Object> instance : instances) {
				JSONObject point = new JSONObject();
				point.put("instanceType", instance.get("instanceType"));
				point.put("objectInstanceNumber", instance.get("objectInstanceNumber"));
				if (command == IotCommandType.CONFIGURE) {
					point.put("instance", instance.get("instance"));
					point.put("device", instance.get("device"));
					point.put("instanceDescription", instance.get("instanceDescription"));
				}
				else if (command == IotCommandType.SUBSCRIBE) {
					if (instance.containsKey("thresholdJson")) {
						JSONParser parser = new JSONParser();
						JSONObject threshold = (JSONObject) parser.parse((String) instance.get("thresholdJson"));
						point.putAll(threshold);
					}
				}
				else if (command == IotCommandType.SET && instance.containsKey("value")) {
					point.put("newValue", instance.get("value"));
					point.put("valueType", getValueType(modBean.getField((long) instance.get("fieldId")).getDataTypeEnum()));
				}
				points.add(point);
			}
			object.put("points", points);
			
		}

		String objString = object.toJSONString();
		if (objString.length() > MAX_BUFFER) {
			List<PublishMessage> messages = new ArrayList<>();
			JSONArray array = (JSONArray) object.get("points");
			int pointsSize = 0;
			JSONArray pointsArray = new JSONArray();
			for (int i = 0; i < array.size(); i++) {
				JSONObject point = (JSONObject) array.get(i);
				pointsSize = pointsSize + point.toJSONString().length();
				if (pointsSize < MAX_BUFFER) {
					pointsArray.add(point);
				} else {
					object.put("points", pointsArray);
					messages.add(getMessageObject(object));
					pointsArray.clear();
					pointsSize = point.toJSONString().length();
					pointsArray.add(point);
				}
			}
			if (pointsArray.size() > 0) {
				object.put("points", pointsArray);
				messages.add(getMessageObject(object));
			}
			data.setMessages(messages);
		} else {
			data.setMessages(Collections.singletonList(getMessageObject(object)));
		}
		return data;
	}

	private static PublishMessage getMessageObject(JSONObject object) {
		PublishMessage msg = new PublishMessage();
		JSONObject message = new JSONObject();
		message.putAll(object);
		msg.setData(message);
		return msg;
	}

	private static String getValueType(FieldType fieldType) {
		String type = null;
		switch(fieldType) {
			case NUMBER:
				type = "signed";
				break;
			case DECIMAL:
				type = "double";
				break;
			case BOOLEAN:
				type = "boolean";
				break;
			case STRING:
				type = "string";
		}
		return type;
	}
	
	public static int acknowdledgeData (long id, boolean isResponseAck) throws SQLException {
		Map<String, Object> prop;
		if  (isResponseAck) {
			prop = Collections.singletonMap("responseAckTime", System.currentTimeMillis());
		}
		else {
			prop = Collections.singletonMap("acknowledgeTime", System.currentTimeMillis());
		}
		FacilioModule module = ModuleFactory.getPublishDataModule();
		return new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPublishDataFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.update(prop)
				;
		
	}
	
	public static int acknowdledgeMessage (long id, String ackMessage) throws Exception {
		Map<String, Object> prop;
		boolean isExecuted = false;
		if (StringUtils.isNotEmpty(ackMessage) && AckMessageType.EXECUTED.equals(ackMessage)) {
			isExecuted = true;
			prop = Collections.singletonMap("responseAckTime", System.currentTimeMillis());
		}
		else {
			prop = Collections.singletonMap("acknowledgeTime", System.currentTimeMillis());
		}
		Chain updateChain = TransactionChainFactory.updateAckChain();
		FacilioContext context = new FacilioContext();
		context.put(AgentKeys.ID,id);
		context.put(FacilioConstants.ContextNames.ID,id);
		context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,prop);
		updateChain.execute(context);
		if (isExecuted) {
			handlePublishDataOnMessageAck(id);
		}
		
		return 1;
	}
	
	private static void handlePublishDataOnMessageAck(long messageId) throws Exception {
		FacilioModule module = ModuleFactory.getPublishMessageModule();
		List<FacilioField> fields = FieldFactory.getPublishMessageFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(messageId, module))
														;
		Map<String, Object> prop = builder.fetchFirst();
		if (prop != null && !prop.isEmpty()) {
			PublishMessage message = FieldUtil.getAsBeanFromMap(prop, PublishMessage.class);
			List<FacilioField> selectFields = FieldFactory.getCountField(module);
			long parentId = message.getParentId();
			builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(selectFields)
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("responseAckTime"), CommonOperators.IS_EMPTY))
					;
			prop = builder.fetchFirst();
			long count = (long) prop.get("count");
			if (count == 0) {
				acknowdledgeData(parentId, true);
				
				module = ModuleFactory.getPublishDataModule();
				builder = new GenericSelectRecordBuilder()
						.table(module.getTableName())
						.select(FieldFactory.getPublishDataFields())
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCondition(CriteriaAPI.getIdCondition(parentId, module));
						;
				prop =  builder.fetchFirst();
				PublishData data = FieldUtil.getAsBeanFromMap(prop, PublishData.class);
				
				try {
					if (data.getCommandEnum() != null && data.getCommandEnum() == IotCommandType.SET) {
						publishGetData(data.getControllerId(), message);
					}
				}
				catch(Exception e) {
					LOGGER.error("Exception while publishing get ", e);
				}
				
				sendPublishNotification(data, null);
			}
		}
	}
	
	private static void publishGetData(long controllerId, PublishMessage message) throws Exception {
		JSONObject msg = message.getData();
		JSONArray points = (JSONArray) msg.get("points");
		JSONObject pointInstance = (JSONObject) points.get(0);
		
		Map<String, Object> instance = new HashMap<>();
		instance.put("instanceType", pointInstance.get("instanceType"));
		instance.put("objectInstanceNumber", pointInstance.get("objectInstanceNumber"));
		instance.put("controllerId", controllerId);
		
		IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IotCommandType.GET);
	}
	
	private static void addPublishData (PublishData data) throws Exception {
		data.setOrgId(AccountUtil.getCurrentOrg().getId());
		data.setCreatedTime(System.currentTimeMillis());
		
		Map<String, Object> props = FieldUtil.getAsProperties(data);
		new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPublishDataModule().getTableName())
				.fields(FieldFactory.getPublishDataFields())
				.insert(props);
		data.setId((long) props.get("id"));
	}
	
	private static void addAndPublishMessage (long parentId, List<PublishMessage> messages) throws Exception {
		GenericInsertRecordBuilder msgBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPublishMessageModule().getTableName())
				.fields(FieldFactory.getPublishMessageFields())
				;
		for (PublishMessage msg : messages) {
			msg.setOrgId(AccountUtil.getCurrentOrg().getId());
			msg.setSentTime(System.currentTimeMillis());
			msg.setParentId(parentId);
			
			msgBuilder.addRecord(FieldUtil.getAsProperties(msg));
		}
		msgBuilder.save();
			
		for (int i = 0; i < messages.size(); i++) {
			long id = (long) msgBuilder.getRecords().get(i).get("id");
			PublishMessage msg = messages.get(i);
			msg.setId(id);
			msg.getData().put("msgid", id);
			publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), msg.getData());
		}
	}
	
	public static PublishData publishIotMessage(List<Map<String, Object>> instances, IotCommandType command) throws Exception {
		return publishIotMessage(instances, command, null, null);
	}
	
	public static PublishData publishIotMessage(List<Map<String, Object>> instances, IotCommandType command, SerializableConsumer<PublishData> success, SerializableConsumer<PublishData> failure) throws Exception {
		PublishData data = constructIotMessage(instances, command);
		return publishIotMessage(data, success, failure);
	}
	
	public static PublishData publishIotMessage(long controllerId, IotCommandType command) throws Exception {
		PublishData data = constructIotMessage(controllerId, command);
		return publishIotMessage(data, null, null);
	}
	
	public static PublishData publishIotMessage(long controllerId, IotCommandType command, SerializableConsumer<PublishData> success, SerializableConsumer<PublishData> failure) throws Exception {
		PublishData data = constructIotMessage(controllerId, command);
		return publishIotMessage(data, success, failure);
	}
	
	public static PublishData publishIotMessage(long controllerId, JSONObject property, SerializableConsumer<PublishData> success, SerializableConsumer<PublishData> failure) throws Exception {
		PublishData data = constructIotMessage(controllerId, property);
		return publishIotMessage(data, success, failure);
	}
	
	private static PublishData publishIotMessage(PublishData data, SerializableConsumer<PublishData> success, SerializableConsumer<PublishData> failure) throws Exception {
		addPublishData(data);
		addAndPublishMessage(data.getId(), data.getMessages());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PUBLISH_DATA, data);
		
		if (success != null) {
			context.put(FacilioConstants.ContextNames.PUBLISH_SUCCESS, success);
		}
		if (failure != null) {
			context.put(FacilioConstants.ContextNames.PUBLISH_FAILURE, failure);
		}
		FacilioTimer.scheduleInstantJob("PublishedDataChecker", context);
		
		return data;
	}
	
	public static void publishMessagesDirectly (Collection<PublishMessage> collection) throws Exception {
		for (PublishMessage msg : collection) {
			msg.getData().put("msgid", msg.getId());
			publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), msg.getData());
		}
	}
	
 	private static void publishIotMessage(String client, JSONObject object) throws Exception {
	    String topic = client+"/msgs";
		LOGGER.info(AwsUtil.getConfig("iot.endpoint") +" " + client+"-facilio" + " " + topic + " " + object);
		AWSIotMqttClient mqttClient = new AWSIotMqttClient(AwsUtil.getConfig("iot.endpoint"), client+"-facilio", AwsUtil.getConfig("iot.accessKeyId"), AwsUtil.getConfig("iot.secretKeyId"));
		try {
			mqttClient.connect();
			if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
				mqttClient.publish(new AWSIotMessage(topic, AWSIotQos.QOS0, object.toJSONString()));
				AgentUtil.putLog(object, AccountUtil.getCurrentOrg().getOrgId(), Long.parseLong(object.get(AgentKeys.AGENT_ID).toString()), true);
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
 	
 	public static void sendPublishNotification(PublishData publishData, JSONObject info) {
		
		try {
			WmsPublishResponse data = new WmsPublishResponse();
			data.publish(publishData, info);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while sending publish notification", e);
		}
	}
 	
 	public static void sendFailureNotification(PublishData publishData) {
		
		try {
			WmsPublishResponse data = new WmsPublishResponse();
			data.publishFailure(publishData);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while sending publish notification", e);
		}
	}
 	
 	public enum IotCommandType {
 		CONFIGURE("configure"),
 		SET("set"),
 		GET("get"),
 		PROPERTY("property"),
 		DISCOVER("discoverPoints"),
 		SUBSCRIBE("subscribe"),
 		UNSUBSCRIBE("unsubscribe")
 		;
 		
 		private String name;
 		IotCommandType(String name) {
 			this.name = name;
 		}
 		
 		public String getName() {
			return name;
		}
 		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static IotCommandType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}
 	
 	public enum ThresholdOperator {
 		SUM,		// 1
 		SUBTRACTION,
 		MULTIPLICATION,
 		DIVISION,
 		GREATER_THAN,
 		LESS_THAN,
 		GREATER_THAN_EQUALS,
 		LESS_THAN_EQUALS,
 		EQUALS,
 		PERCENTAGE,
 		CONSTANT		// 11
 		;
 		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ThresholdOperator valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}
}
