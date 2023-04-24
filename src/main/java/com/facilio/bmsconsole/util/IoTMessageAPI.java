package com.facilio.bmsconsole.util;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.agentv2.AgentConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.CommandStatus;
import com.facilio.agent.FacilioAgent;
import com.facilio.agent.PublishType;
import com.facilio.agent.protocol.ProtocolUtil;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.wms.message.WmsPublishResponse;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class IoTMessageAPI {
	private static final Logger LOGGER = LogManager.getLogger(IoTMessageAPI.class.getName());
	
	private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB
	
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
		object.put("portNumber", controller.getPortNumber());
		object.put("networkNumber", controller.getNetworkNumber());
		object.put("instanceNumber", controller.getInstanceNumber());
		object.put("ipAddress", controller.getIpAddress());
		object.put("type", controller.getControllerType());
		object.put("subnetPrefix", 1l);
		object.put("broadcastAddress", "1");
		object.put("timestamp", System.currentTimeMillis());
		
		if (controller.getAgentId() != -1) {
			FacilioAgent agent = AgentUtil.getAgentDetails(controller.getAgentId());
			object.put("agent", agent.getName());
			// Temp...till it is handled in agent
			if (command == IotCommandType.PING) {
				object.put(EventUtil.DATA_TYPE, PublishType.ack.getValue());
				object.put("pingAgent", agent.getName());
			}
			object.put(AgentKeys.AGENT_ID, agent.getId()); // Agent_Id key must be changes to camelcase.
		}

		if (command == IotCommandType.PROPERTY) {
			object.putAll(property);
		}
		else if (command != IotCommandType.DISCOVER && instances != null && !instances.isEmpty()) {
			JSONArray points = ProtocolUtil.getPointsToPublish(controller, instances, command);
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
	
	

	public static PublishMessage getMessageObject(JSONObject object) {
		PublishMessage msg = new PublishMessage();
		JSONObject message = new JSONObject();
		message.putAll(object);
		msg.setData(message);
		return msg;
	}

	public static int acknowdledgeData (long id, String ackField) throws SQLException {
		FacilioModule module = ModuleFactory.getPublishDataModule();
		return new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPublishDataFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.update(Collections.singletonMap(ackField, System.currentTimeMillis()))
				;
		
	}
	
	public static void acknowdledgeMessage (long id, String ackMessage, JSONObject payLoad) throws Exception {
		boolean isExecuted = false;
		boolean isPing = false;
		IotCommandType commandType = null;
		
		String command = (String) payLoad.get(AgentKeys.COMMAND);
		
		if (StringUtils.isNotEmpty(command)) {
			commandType = IotCommandType.getCommandType(command);
			isPing = commandType != null && IotCommandType.PING == commandType;
		}
		if (StringUtils.isNotEmpty(ackMessage) && CommandStatus.EXECUTED.equals(ackMessage)) {
			isExecuted = true;
		}
		
		if (isPing) {
			String agent = (String) payLoad.get("agent");
			String pingAgent = (String) payLoad.get("pingAgent");
			if (!pingAgent.equals(agent)) {
				return;
			}

			if (isExecuted) {
				acknowdledgeData(id, "pingAckTime");
				PublishData publishData = getPublishData(id, true);
				publishIotMessage(publishData, -1);
			}
		} else {
			updateMessageAckTime(id, isExecuted ? "responseAckTime" : "acknowledgeTime");
			if (isExecuted) {
				handlePublishDataOnMessageAck(id, commandType);
			}
		}
	}

	private static void updateMessageAckTime(long id, String fieldName) throws SQLException {
		FacilioModule module = ModuleFactory.getPublishMessageModule();

		new GenericUpdateRecordBuilder()
        .table(module.getTableName())
        .fields(FieldFactory.getPublishMessageFields())
        .andCondition(CriteriaAPI.getIdCondition(id, module))
        .update(Collections.singletonMap(fieldName, System.currentTimeMillis()))
        ;
	}

	public static PublishData getPublishData(long id, boolean fetchMessages) throws Exception {
		FacilioModule module = ModuleFactory.getPublishDataModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getPublishDataFields())
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		;
		Map<String, Object> prop = builder.fetchFirst();
		PublishData data = FieldUtil.getAsBeanFromMap(prop, PublishData.class);
		if (fetchMessages) {
			List<PublishMessage> messages = getPublishMessages(id);
			data.setMessages(messages);
		}
		return data;

	}

	public static List<PublishMessage> getPublishMessages(long parentId) throws Exception {
		FacilioModule module = ModuleFactory.getPublishMessageModule();
		List<FacilioField> fields = FieldFactory.getPublishMessageFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, PublishMessage.class);
		}
		return null;
	}

	private static void handlePublishDataOnMessageAck(long messageId, IotCommandType command) throws Exception {
		FacilioModule module = ModuleFactory.getPublishMessageModule();
		List<FacilioField> fields = FieldFactory.getPublishMessageFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(messageId, module));
		Map<String, Object> prop = builder.fetchFirst();
		if (prop != null && !prop.isEmpty()) {
			PublishMessage message = FieldUtil.getAsBeanFromMap(prop, PublishMessage.class);
			handlePublishMessageSuccess(command, message);


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
				acknowdledgeData(parentId, "responseAckTime");
				PublishData data = getPublishData(parentId, false);
				try {
					if (data.getCommandEnum() != null && data.getCommandEnum() == IotCommandType.SET) {
						publishGetData(data.getControllerId(), message);
					}
				} catch(Exception e) {
					LOGGER.error("Exception while publishing get ", e);
				}

				sendPublishNotification(data, null);
			}
		}
	}

	// Get called after setting
	private static void publishGetData(long controllerId, PublishMessage message) throws Exception {
		JSONObject msg = message.getData();
		JSONArray points = (JSONArray) msg.get("points");
		JSONObject pointInstance = (JSONObject) points.get(0);

		Map<String, Object> instance = new HashMap<>(pointInstance);
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

	private static void addPublishMessage(long parentId, List<PublishMessage> messages) throws Exception {
		GenericInsertRecordBuilder msgBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPublishMessageModule().getTableName())
				.fields(FieldFactory.getPublishMessageFields());
		List<Map<String, Object>> records = new ArrayList<>();
		for (PublishMessage msg : messages) {
			msg.setOrgId(AccountUtil.getCurrentOrg().getId());
			msg.setSentTime(System.currentTimeMillis());
			msg.setParentId(parentId);

			Map<String, Object> record = FieldUtil.getAsProperties(msg);
			records.add(record);

		}
		msgBuilder.addRecords(records);
		msgBuilder.save();

		for(int i = 0; i < records.size(); i++) {
			messages.get(i).setId((long)records.get(0).get("id"));
		}
	}

	public static PublishData publishIotMessage(List<Map<String, Object>> instances, IotCommandType command) throws Exception {
		PublishData data = constructIotMessage(instances, command);
		return addAndPublishData(data);
	}

	public static PublishData publishIotMessage(long controllerId, IotCommandType command) throws Exception {
		PublishData data = constructIotMessage(controllerId, command);
		return addAndPublishData(data);
	}

	public static PublishData publishIotMessage(long controllerId, JSONObject property) throws Exception {
		PublishData data = constructIotMessage(controllerId, property);
		return addAndPublishData(data);
	}


	private static PublishData addAndPublishData(PublishData data) throws Exception {

		addPublishData(data);
		addPublishMessage(data.getId(), data.getMessages());

		if (data.getCommandEnum() != IotCommandType.GET) {
			// Pinging device to check if it is active
			PublishData pingData = constructIotMessage(data.getControllerId(), IotCommandType.PING);
			pingData.setId(data.getId());
			publishIotMessage(pingData, data.getId());
		} else {
			publishIotMessage(data, -1);
		}

		return data;
	}

	private static void publishIotMessage(PublishData data, long msgId) throws Exception {
		for(PublishMessage message : data.getMessages()) {
			message.getData().put("msgid", msgId != -1 ? msgId : message.getId());
			publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), message.getData());

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PUBLISH_DATA, data);
			FacilioTimer.scheduleInstantJob("PublishedDataChecker", context);
		}
	}

	public static void publishMessagesDirectly (Collection<PublishMessage> collection) throws Exception {
		for (PublishMessage msg : collection) {
			msg.getData().put("msgid", msg.getId());
			publishIotMessage(AccountUtil.getCurrentOrg().getDomain(), msg.getData());
		}
	}

	private static void publishIotMessage(String client, JSONObject object) throws Exception {
		if (!FacilioProperties.isProduction()) {
			return;
		}

		Long agentId = (Long) object.remove(AgentKeys.AGENT_ID);
		if( FacilioProperties.isOnpremise()) {
			publishToRabbitMQ(client, object);
		} else {
			publishToAwsIot(client, object);
		}
		if(agentId != null && agentId > 0) {
			AgentUtil.putLog(object, AccountUtil.getCurrentOrg().getOrgId(), agentId, true);
		}

	}


	private static void publishToRabbitMQ(String client, JSONObject object) throws Exception {

		String topic = client+".msgs";
		LOGGER.info(FacilioProperties.getIotEndPoint() +" " + client+"-facilio" + " " + topic + " " + object);
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


	private static void publishToAwsIot(String client, JSONObject object) throws Exception {
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

	private static void handlePublishMessageSuccess(IotCommandType command, PublishMessage message) throws Exception {
		if (command == null) {
			return;
		}

		List<Long> ids = getPointIdsFromMessage(message);
		if (ids.isEmpty()) {
			return;
		}

		switch(command) {
			case CONFIGURE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("configureStatus", PointEnum.ConfigureStatus.CONFIGURED.getIndex()));
				break;

			case UNCONFIGURE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("configureStatus", PointEnum.ConfigureStatus.UNCONFIGURED.getIndex()));
				break;

			case SUBSCRIBE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("subscribeStatus", PointEnum.SubscribeStatus.SUBSCRIBED.getIndex()));
				break;

			case UNSUBSCRIBE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("subscribeStatus", PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex()));
				break;
		}
	}

	public static void handlePublishMessageFailure(PublishData data) throws Exception {
		for(PublishMessage message : data.getMessages()) {
			handlePublishMessageFailure(data.getCommandEnum(), message);
		}
		sendFailureNotification(data);
	}

	private static void handlePublishMessageFailure(IotCommandType command, PublishMessage message) throws Exception {
		if (command == null) {
			return;
		}

		List<Long> ids = getPointIdsFromMessage(message);
		if (ids.isEmpty()) {
			return;
		}

		switch(command) {
			case CONFIGURE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("configureStatus", PointEnum.ConfigureStatus.UNCONFIGURED.getIndex()));
				break;

			case UNCONFIGURE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("configureStatus", PointEnum.ConfigureStatus.CONFIGURED.getIndex()));
				break;

			case SUBSCRIBE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("subscribeStatus", PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex()));
				break;

			case UNSUBSCRIBE:
				TimeSeriesAPI.updateInstances(ids, Collections.singletonMap("subscribeStatus", PointEnum.SubscribeStatus.SUBSCRIBED.getIndex()));
				break;
		}
	}

	private static List<Long> getPointIdsFromMessage(PublishMessage message) {
		List<Long> ids = new ArrayList<>();
		JSONObject obj = message.getData();
		if (obj.get("points") != null) {
			JSONArray points = (JSONArray) obj.get("points");
			for (int i = 0; i < points.size(); i++) {
				JSONObject point = (JSONObject) points.get(i);
				if (point.containsKey("pointId")) {
					Long id = (Long) point.get("pointId");
					if (id != null && id > 0) {
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}

	public static void sendPublishNotification(PublishData publishData, JSONObject info) {

		try {
			WmsPublishResponse data = new WmsPublishResponse();
			data.publish(publishData, info);
		} catch (Exception e) {
			LOGGER.error("Error occurred while sending publish notification", e);
		}
	}

	public static void sendFailureNotification(PublishData publishData) {

		try {
			WmsPublishResponse data = new WmsPublishResponse();
			data.publishFailure(publishData);
		} catch (Exception e) {
			LOGGER.error("Error occurred while sending publish notification", e);
		}
	}

	public static PublishData setReadingValue(long resourceId, long fieldId, Object value) throws Exception {
		if(AccountUtil.getCurrentOrg()!= null){
			Criteria criteria = new Criteria();
			FacilioModule pointModule = ModuleFactory.getPointModule();
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getPointFieldIdField(pointModule), String.valueOf(fieldId),NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getPointResourceIdField(pointModule), String.valueOf(resourceId), NumberOperators.EQUALS));
			GetPointRequest getPointRequest = new GetPointRequest()
					.withCriteria(criteria);
			List<Point> points = getPointRequest.getPoints();
			if ((points != null) && ( ! points.isEmpty())){
				Point point = points.get(0);
				point.setValue(value);
				ControllerMessenger.setValue(point);
			}else {
				Map<String, Object> instance = TimeSeriesAPI.getMappedInstance(resourceId,fieldId);
				if (instance != null) {
					instance.put("value", value);
					instance.put("fieldId", fieldId);
					return IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IotCommandType.SET);
				}else {
					LOGGER.info("No point for assetid "+resourceId+" and fieldId "+fieldId+" for set vlaue "+value);
				}
			}
		}else {
			LOGGER.info("Exception occurred current org is null");
		}
		return null;

	}

	public static void setReadingValue(List<ControlActionCommandContext> commands) throws Exception {
		Set<Pair<Long, Long>> pairs = new HashSet<>();
		Map<String, ControlActionCommandContext> commandMap = new HashMap<>();
		for (ControlActionCommandContext command: commands) {
			pairs.add(Pair.of(command.getResource().getId(), command.getFieldId()));
			commandMap.put(ReadingsAPI.getRDMKey(command.getResource().getId(), command.getRdm().getField()), command);
		}
		List<Point> pointData = PointsAPI.getPointData(pairs);
		Map<Long, List<Point>> pointControllerMap = new HashMap<>();
		for (Point point: pointData) {
			List<Point> pointList = pointControllerMap.get(point.getControllerId());
			if (pointList == null) {
				pointList = new ArrayList<>();
				pointControllerMap.put(point.getControllerId(), pointList);
			}
			ControlActionCommandContext command = commandMap.get(ReadingsAPI.getRDMKey(point.getResourceId(), point.getFieldId()));
			if(command.getActionName() == null  || command.getActionName().equals(AgentConstants.OVERRIDE) || command.getActionName().equals(AgentConstants.EMERGENCY_OVERRIDE))  {
				point.setValue(command.getConvertedValue() != null ? command.getConvertedValue() : command.getValue());
			}
			if(command.getOverrideTimeInMillis()!=0){
				point.setOverrideTimeInMillis(command.getOverrideTimeInMillis());
			}
			point.setControlActionId(command.getId());
			point.setCommandRetryCount(command.getRetriedCount());
			if (command.getActionName() != null) {
				point.setActionName(command.getActionName());
			}
			pointList.add(point);
		}

		for(Map.Entry<Long, List<Point>> entry: pointControllerMap.entrySet()) {
			ControllerMessenger.setValue(entry.getValue());
		}

	}


	public enum IotCommandType {
		// Maintain the index. Always add at the bottom
		CONFIGURE("configure"),    // 1
		SET("set"),
		GET("get"),
		PROPERTY("property"),    // 4
		DISCOVER("discoverPoints"),
		SUBSCRIBE("subscribe"),
		UNSUBSCRIBE("unsubscribe"),
		UNCONFIGURE("unconfigure"), // 8
		PING("ping");

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

		public static IotCommandType getCommandType(String name) {
			return COMMAND_MAP.get(name);
		}

		private static final Map<String, IotCommandType> COMMAND_MAP = Collections.unmodifiableMap(initCommandMap());

		private static Map<String, IotCommandType> initCommandMap() {
			Map<String, IotCommandType> typeMap = new HashMap<>();
			for(IotCommandType command : values()) {
				typeMap.put(command.getName(), command);
			}
			return typeMap;
		}

		public static IotCommandType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}

	public enum ThresholdOperator {
		SUM,        // 1
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION,
		GREATER_THAN,
		LESS_THAN,
		GREATER_THAN_EQUALS,
		LESS_THAN_EQUALS,
		EQUALS,
		PERCENTAGE,
		CONSTANT        // 11
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
