package com.facilio.wms.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;

import com.facilio.wms.endpoints.FacilioClientEndpoint;
import com.facilio.wms.message.Message;

public class WmsApi
{
	private static Logger logger = Logger.getLogger(WmsApi.class.getName());
	
	private static String WEBSOCKET_URL = "ws://localhost:8080/websocket/chat";
	public static void sendMessage(int uid, Message message) throws IOException, EncodeException
	{
		try 
        {
            // open websocket
            final FacilioClientEndpoint clientEndPoint = new FacilioClientEndpoint(new URI(WEBSOCKET_URL + "/" + uid));

            // add listener
            clientEndPoint.addMessageHandler(new FacilioClientEndpoint.MessageHandler() 
            {
                public void handleMessage(String message) {
                    //TODO
                }
            });
            // send message to websocket
            clientEndPoint.sendMessage(message);
        } 
        catch (URISyntaxException ex) 
        {
        	logger.log(Level.SEVERE, "URISyntaxException exception: " + ex.getMessage(), ex);
        }
	}
}