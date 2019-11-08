package com.facilio.wms.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import com.facilio.wms.endpoints.FacilioClientEndpoint;
import com.facilio.wms.endpoints.SessionManager;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.WmsChatMessage;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.message.WmsNotification;
import com.facilio.wms.message.WmsPublishResponse;
import com.facilio.wms.message.WmsRemoteScreenMessage;

public class WmsApi
{
	private static final Logger LOGGER = Logger.getLogger(WmsApi.class.getName());
	private static String kinesisNotificationTopic = "notifications";
	
	private static String WEBSOCKET_URL = "ws://localhost:8080/websocket";
	private static Map<String, FacilioClientEndpoint> FACILIO_CLIENT_ENDPOINTS = new HashMap<>();
	private static FacilioProducer producer;
	
	static {
		if(!FacilioProperties.isDevelopment()) {
			String socketUrl = FacilioProperties.getConfig("app.domain");
			if (socketUrl != null) {
				WEBSOCKET_URL = "wss://"+socketUrl+"/websocket";
			}
			kinesisNotificationTopic = FacilioProperties.getConfig("environment") + "-" + kinesisNotificationTopic;
			producer = new FacilioKafkaProducer(kinesisNotificationTopic);
			LOGGER.info("Initialized Kafka Producer");
		}
	}

	private static void sendToKafka(JSONObject data) {
		JSONObject dataMap = new JSONObject();
		try {
			String partitionKey = (String) data.get("namespace");
			if (partitionKey == null) {
				partitionKey = kinesisNotificationTopic;
			}
			dataMap.put("timestamp", System.currentTimeMillis());
			dataMap.put("data", data);
			RecordMetadata future = (RecordMetadata)producer.putRecord(new FacilioRecord(partitionKey, dataMap));
		} catch (Exception e) {
			LOGGER.info(kinesisNotificationTopic + " : " + dataMap);
			LOGGER.log(Level.INFO, "Exception while producing to kafka ", e);
		}
	}

	private static Properties getKafkaProducerProperties() {
		Properties props = new Properties();
		props.put("bootstrap.servers", FacilioProperties.getKafkaProducer());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return  props;
	}
	
	
	public static String getWebsocketEndpoint(long uid) {
		return WEBSOCKET_URL + "/" + uid;
	}
	
	public static String getRemoteWebsocketEndpoint(long id) {
		return WEBSOCKET_URL + "/remote/" + id;
	}
	
	public static void sendEvent(long to, WmsEvent event) throws IOException, EncodeException
	{
		List<Long> toList = new ArrayList<>();
		toList.add(to);
		sendMessage(toList, event);
	}
	
	public static void sendRemoteMessage(long to, WmsRemoteScreenMessage remoteMessage) throws IOException, EncodeException
	{
		List<Long> toList = new ArrayList<>();
		toList.add(to);
		sendMessage(toList, remoteMessage);
	}
	
	public static void broadCastMessage( Message message) throws IOException, EncodeException
	{
		List<Long> allRecipients = new ArrayList<>();
		allRecipients.add(-1l);
		sendMessage(allRecipients, message);
		LOGGER.info("Broadcast msg sent..... ");
	}
	
	
	public static void sendEvent(List<Long> recipients, WmsEvent event) throws IOException, EncodeException
	{
		sendMessage(recipients, event);
	}
	
	public static void sendNotification(long to, WmsNotification notification) throws IOException, EncodeException
	{
		List<Long> toList = new ArrayList<>();
		toList.add(to);
		sendMessage(toList, notification);
	}
	
	public static void sendNotification(List<Long> recipients, WmsNotification notification) throws IOException, EncodeException
	{
		sendMessage(recipients, notification);
	}
	
	public static void sendChatMessage(long to, WmsChatMessage message) throws IOException, EncodeException
	{
		List<Long> toList = new ArrayList<>();
		toList.add(to);
		sendMessage(toList, message);
	}
	
	public static void sendChatMessage(List<Long> recipients, WmsChatMessage message) throws IOException, EncodeException
	{
		sendMessage(recipients, message);
	}
	
	public static void sendPubSubMessage(List<Long> recipients, Message message) throws IOException, EncodeException
	{
		sendMessage(recipients, message);
	}
	
	public static void sendPublishResponse(List<Long> recipients, WmsPublishResponse data) throws IOException, EncodeException
	{
		sendMessage(recipients, data);
	}
	
	private static void sendMessage(List<Long> recipients, Message message) throws IOException, EncodeException
	{
		for (Long to : recipients) {
			message.setTo(to);
			if (AccountUtil.getCurrentUser() != null) {
				message.setFrom(AccountUtil.getCurrentUser().getId());
			}
			if (FacilioProperties.isDevelopment()) {
				SessionManager.getInstance().sendMessage(message);
			} else {
				sendToKafka(message.toJson());
			}
		}
	}



	private static void sendToKinesis(JSONObject object) {
		try {
			String partitionKey = (String) object.get("namespace");
			if (partitionKey == null) {
				partitionKey = kinesisNotificationTopic;
			}
			PutRecordResult result = AwsUtil.getKinesisClient().putRecord(kinesisNotificationTopic, ByteBuffer.wrap(object.toJSONString().getBytes(Charset.defaultCharset())), partitionKey);
			LOGGER.fine("Sent notification message to kinesis :  " + object.toJSONString() + " , " + result.getSequenceNumber());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception while sending messages to kinesis ", e);
		}
	}

	public static String getKinesisNotificationTopic() {
		return kinesisNotificationTopic;
	}

	public static FacilioClientEndpoint getFacilioEndPoint(String path)
	{
		if(FACILIO_CLIENT_ENDPOINTS.containsKey(path))
		{
			return FACILIO_CLIENT_ENDPOINTS.get(path);
		}
		FacilioClientEndpoint clientEndPoint = null;
		try 
        {
			clientEndPoint = new FacilioClientEndpoint(new URI(path));
			clientEndPoint.addMessageHandler(new FacilioClientEndpoint.MessageHandler() 
	        {
	            public void handleMessage(String message) {
	                //TODO
	            }
	        });
			FACILIO_CLIENT_ENDPOINTS.put(path, clientEndPoint);
        }
		catch (URISyntaxException ex) 
        {
        	LOGGER.log(Level.SEVERE, "URISyntaxException exception: " + ex.getMessage(), ex);
        }
		return clientEndPoint;
	}
}