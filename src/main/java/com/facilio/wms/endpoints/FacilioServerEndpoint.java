package com.facilio.wms.endpoints;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageDecoder;
import com.facilio.wms.message.MessageEncoder;

/**
 *
 * @author Shivaraj
 */
@ServerEndpoint(
        value="/websocket/{uid}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class FacilioServerEndpoint 
{
    private final Logger log = Logger.getLogger(getClass().getName());

    private Session session;
    private static final Set<FacilioServerEndpoint> endpoints = new CopyOnWriteArraySet<FacilioServerEndpoint>();
    private static HashMap<String,Integer> users = new HashMap<String,Integer>();

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") int uid) throws IOException, EncodeException 
    {
    	System.out.println("Session started uid ::" +uid + ":::sessionid" + session.getId());
        this.session = session;
        endpoints.add(this);
        users.put(session.getId(), uid);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException 
    {
    	System.out.println("Session started message to ::" +message.getTo()+ ":::sessionid" + session.getId());
        message.setFrom(users.get(session.getId()));
        sendMessage(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
    	System.out.println("Session started closed" +session.getId());
        users.remove(session.getId());
    	endpoints.remove(this);
    	session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
    	System.out.println("Session started Error" +throwable.getMessage());
        log.warning(throwable.toString());
    }

    public static void broadcast(Message message) throws IOException, EncodeException 
    {
        for (FacilioServerEndpoint endpoint : endpoints) 
        {
            synchronized(endpoint) 
            {
                endpoint.session.getBasicRemote().sendObject(message);
            }
        }
    }

    public static void sendMessage(Message message) throws IOException, EncodeException 
    {
    	System.out.println("Sending message ::" +message.getContent());
        for (FacilioServerEndpoint endpoint : endpoints) 
        {
            synchronized(endpoint) 
            {
            	System.out.println("Sending message2 ::" +endpoint.session.getId());
            	System.out.println("Sending message3 ::" +getSessionId(message.getTo()));
                if (endpoint.session.getId().equals(getSessionId(message.getTo()))) 
                {
                    endpoint.session.getBasicRemote().sendObject(message);
                }
            }
        }
    }

    private static String getSessionId(int to) 
    {
        if (users.containsValue(to)) 
        {
            for (String sessionId: users.keySet()) 
            {
                if (users.get(sessionId).equals(to)) 
                {
                    return sessionId;
                }
            }
        }
        return null;
    }
}