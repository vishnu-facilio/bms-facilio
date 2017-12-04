package com.facilio.wms.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.wms.endpoints.FacilioClientEndpoint;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.WmsChatMessage;
import com.facilio.wms.message.WmsEvent;
import com.facilio.wms.message.WmsNotification;

public class WmsApi
{
	private static Logger logger = Logger.getLogger(WmsApi.class.getName());
	
	public static String WEBSOCKET_URL = "ws://localhost:8080/bms/websocket";
	private static Map<String, FacilioClientEndpoint> FACILIO_CLIENT_ENDPOINTS = new HashMap<>();
	
	static {
		String socketUrl = AwsUtil.getConfig("websocket.url");
		if (socketUrl != null) {
			WEBSOCKET_URL = socketUrl;
		}
	}
	
	public static String getWebsocketEndpoint(long uid) {
		return WEBSOCKET_URL + "/" + uid;
	}
	
	public static void sendEvent(long to, WmsEvent event) throws IOException, EncodeException
	{
		List<Long> toList = new ArrayList<>();
		toList.add(to);
		sendMessage(toList, event);
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
	
	private static void sendMessage(List<Long> recipients, Message message) throws IOException, EncodeException
	{
		for (Long to : recipients) {
			message.setTo(to);
			if (AccountUtil.getCurrentUser() != null) {
				message.setFrom(AccountUtil.getCurrentUser().getId());
			}
			
			FacilioClientEndpoint clientEndPoint = getFacilioEndPoint(WEBSOCKET_URL + "/" + to);
	        clientEndPoint.sendMessage(message);
		}
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
        	logger.log(Level.SEVERE, "URISyntaxException exception: " + ex.getMessage(), ex);
        }
		return clientEndPoint;
	}
}