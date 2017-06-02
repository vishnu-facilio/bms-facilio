package com.facilio.wms.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;

import com.facilio.wms.endpoints.FacilioClientEndpoint;
import com.facilio.wms.message.Message;

public class WmsApi
{
	private static Logger logger = Logger.getLogger(WmsApi.class.getName());
	
	private static String WEBSOCKET_URL = "ws://localhost:8080/bms/websocket";
	private static Map<String, FacilioClientEndpoint> FACILIO_CLIENT_ENDPOINTS = new HashMap<>();
	
	public static void sendMessage(int uid, Message message) throws IOException, EncodeException
	{
		FacilioClientEndpoint clientEndPoint = getFacilioEndPoint(WEBSOCKET_URL + "/" + uid);
        clientEndPoint.sendMessage(message);
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