package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.serializable.SerializableConsumer;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class IoTMessageAPI {
	private static final Logger LOGGER = LogManager.getLogger(IoTMessageAPI.class.getName());
	
	private static final int MAX_BUFFER = 2000000;
	private static PublishData constructIotMessage (List<Map<String, Object>> instances, String command) throws Exception {

		ControllerContext controller = ControllerAPI.getController((long) instances.get(0).get("controllerId"));

		PublishData data = new PublishData();
		data.setControllerId(controller.getId());
		
		JSONObject object = new JSONObject();
		object.put("command", command);
		object.put("deviceName", controller.getName());
		object.put("macAddress", controller.getMacAddr());
		object.put("subnetPrefix", controller.getSubnetPrefix());
		object.put("networkNumber", controller.getNetworkNumber());
		object.put("instanceNumber", controller.getInstanceNumber());
		object.put("broadcastAddress", controller.getBroadcastIp());
		object.put("clientId", controller.getId());

		JSONArray points = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(Map<String, Object> instance : instances) {
			JSONObject point = new JSONObject();
			point.put("instance", instance.get("instance"));
			point.put("instanceType", instance.get("instanceType"));
			point.put("device", instance.get("device"));
			point.put("objectInstanceNumber", instance.get("objectInstanceNumber"));
			point.put("instanceDescription", instance.get("instanceDescription"));
			if (instance.containsKey("value")) {
				point.put("newValue", instance.get("value"));
				point.put("valueType", getValueType(modBean.getField((long) instance.get("fieldId")).getDataTypeEnum()));
			}
			points.add(point);
		}
		object.put("points", points);

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
	
	public static int acknowdledgeData (long id) throws SQLException {
		Map<String, Object> prop = Collections.singletonMap("acknowledgeTime", System.currentTimeMillis());
		FacilioModule module = ModuleFactory.getPublishDataModule();
		return new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPublishDataFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.update(prop)
				;
		
	}
	
	public static int acknowdledgeMessage (long id) throws SQLException {
		Map<String, Object> prop = Collections.singletonMap("acknowledgeTime", System.currentTimeMillis());
		FacilioModule module = ModuleFactory.getPublishMessageModule();
		return new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPublishMessageFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				.update(prop)
				;
		
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
	
	public static PublishData publishIotMessage(List<Map<String, Object>> instances, String command) throws Exception {
		return publishIotMessage(instances, command, null, null);
	}
	
	public static PublishData publishIotMessage(List<Map<String, Object>> instances, String command, SerializableConsumer<PublishData> success, SerializableConsumer<PublishData> failure) throws Exception {
		PublishData data = constructIotMessage(instances, command);
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
