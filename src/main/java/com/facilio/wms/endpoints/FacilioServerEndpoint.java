package com.facilio.wms.endpoints;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import com.facilio.wms.util.WmsApi;

/**
 *
 * @author Shivaraj
 */
@ServerEndpoint(
        value="/chat/{uid}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class FacilioServerEndpoint 
{
    private final Logger log = Logger.getLogger(getClass().getName());

    private Session session;
    private String uid;
    private static final Set<FacilioServerEndpoint> endpoints = new CopyOnWriteArraySet<FacilioServerEndpoint>();
    private static HashMap<String,String> users = new HashMap<String,String>();

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) throws IOException, EncodeException 
    {
        this.session = session;
        this.uid = uid;
        endpoints.add(this);
        users.put(session.getId(), uid);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException 
    {
        message.setFrom(users.get(session.getId()));
        sendMessage(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
        endpoints.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
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
        for (FacilioServerEndpoint endpoint : endpoints) 
        {
            synchronized(endpoint) 
            {
                if (endpoint.session.getId().equals(getSessionId(message.getTo()))) 
                {
                    endpoint.session.getBasicRemote().sendObject(message);
                }
            }
        }
    }

    private static String getSessionId(String to) 
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