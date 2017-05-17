package com.facilio.wms.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.EncodeException;

import com.facilio.wms.endpoints.FacilioClientEndpoint;
import com.facilio.wms.message.Message;

public class WmsApi
{
	public static void sendMessage(Message message) throws IOException, EncodeException
	{
		try 
        {
            // open websocket
            final FacilioClientEndpoint clientEndPoint = new FacilioClientEndpoint(new URI("ws://localhost:8080/websocket/chat/100"));

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
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
	}
}