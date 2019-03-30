package com.facilio.wms.endpoints;

import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageDecoder;
import com.facilio.wms.message.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

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

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") long uid) throws IOException, EncodeException 
    {
//    	System.out.println("Session started uid ::" +uid + ":::sessionid" + session.getId());
        this.session = session;
        endpoints.add(this);
        
        SessionManager.UserSession us = new SessionManager.UserSession().setUid(uid).setSession(session).setCreatedTime(System.currentTimeMillis());
        SessionManager.getInstance().addUserSession(us);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException 
    {
//    	System.out.println("Session started message to ::" +session.getPathParameters()+ ":::sessionid" + session.getId()+"  msg: "+message.getContent()+"  cc: "+message);
    	
    	long uid = Long.parseLong(session.getPathParameters().get("uid"));
    	message.setTo(uid);
    	if ("subscribe".equalsIgnoreCase(message.getAction())) {
    		PubSubManager.getInstance().subscribe(message);
    	}
    	else if ("unsubscribe".equalsIgnoreCase(message.getAction())) {
    		PubSubManager.getInstance().unsubscribe(message);
    	}
    	else if (message.getContent() != null && message.getContent().get("ping") != null) {
    		message.getContent().put("ping", "pong");
    		session.getBasicRemote().sendObject(message);
    	}
    }
    
//    @OnMessage
//    public void onMessage(Session session, String message) throws IOException {
//    	System.out.println("Session started message: ::sessionid" + session.getId()+"  msg: "+message);
//    	if ("ping".equalsIgnoreCase(message)) {
//    		session.getBasicRemote().sendText("pong");
//    	}
//    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
//    	System.out.println("Session started closed" +session.getId());
    	endpoints.remove(this);
    	SessionManager.getInstance().removeUserSession(session.getId());
    	session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
//    	System.out.println("Session started Error" +throwable.getMessage());
//        log.warning(throwable.toString());
//        throwable.printStackTrace();
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

//    public static void sendMessage(Message message) throws IOException, EncodeException 
//    {
//    	System.out.println("Sending message TO:: "+message.getTo()+"  MSG:: "+message.getContent());
//    	List<FacilioServerEndpoint> sessions = getSessions(message.getTo());
//    	if (sessions != null) {
//    		for (FacilioServerEndpoint endpoint : sessions) 
//        	{
//        		synchronized(endpoint) {
//        			endpoint.session.getBasicRemote().sendObject(message);
//        		}
//        	}
//    	}
//    	else {
//    		System.out.println("SESSIONS IS NULL :: "+message.getTo()+"  MSG:: "+message.getContent());
//    	}
////        for (FacilioServerEndpoint endpoint : endpoints) 
////        {
////            synchronized(endpoint) 
////            {
////            	List<FacilioServerEndpoint> sessions = getSessions(message.getTo());
////            	System.out.println("Sending message2 :: " + endpoint.session.getId());
////            	System.out.println("Sending message3 ::" + sessions);
////                if (sessions.contains(endpoint.session.getId())) {
////                    endpoint.session.getBasicRemote().sendObject(message);
////                }
////            }
////        }
//    }
}