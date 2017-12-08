package com.facilio.wms.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
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
//    private static HashMap<String,List<String>> users = new HashMap<>();
    private static HashMap<String,List<FacilioServerEndpoint>> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") long uid) throws IOException, EncodeException 
    {
    	System.out.println("Session started uid ::" +uid + ":::sessionid" + session.getId());
        this.session = session;
        endpoints.add(this);
        if (!users.containsKey(String.valueOf(uid))) {
        	users.put(String.valueOf(uid), new ArrayList<FacilioServerEndpoint>());
        }
        users.get(String.valueOf(uid)).add(this);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException 
    {
    	System.out.println("Session started message to ::" +message.getTo()+ ":::sessionid" + session.getId());
    	if (message.getContent() != null && message.getContent().get("ping") != null) {
    		message.addData("ping", "pong");
    		sendMessage(message);
    	}
//        message.setFrom(users.get(session.getId()));
//        sendMessage(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
    	System.out.println("Session started closed" +session.getId());
    	removeSession(session.getId());
    	endpoints.remove(this);
    	session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
    	System.out.println("Session started Error" +throwable.getMessage());
        log.warning(throwable.toString());
        throwable.printStackTrace();
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
    	System.out.println("Sending message TO:: "+message.getTo()+"  MSG:: "+message.getContent());
    	List<FacilioServerEndpoint> sessions = getSessions(message.getTo());
    	if (sessions != null) {
    		for (FacilioServerEndpoint endpoint : sessions) 
        	{
        		synchronized(endpoint) {
        			endpoint.session.getBasicRemote().sendObject(message);
        		}
        	}
    	}
    	else {
    		System.out.println("SESSIONS IS NULL :: "+message.getTo()+"  MSG:: "+message.getContent());
    	}
//        for (FacilioServerEndpoint endpoint : endpoints) 
//        {
//            synchronized(endpoint) 
//            {
//            	List<FacilioServerEndpoint> sessions = getSessions(message.getTo());
//            	System.out.println("Sending message2 :: " + endpoint.session.getId());
//            	System.out.println("Sending message3 ::" + sessions);
//                if (sessions.contains(endpoint.session.getId())) {
//                    endpoint.session.getBasicRemote().sendObject(message);
//                }
//            }
//        }
    }

    private static List<FacilioServerEndpoint> getSessions(long to) 
    {
    	return users.get(String.valueOf(to));
    }
    
    private static void removeSession(String sessionId) 
    {
    	Iterator<String> keys = users.keySet().iterator();
    	while (keys.hasNext()) {
    		String key = keys.next();
    		List<FacilioServerEndpoint> li = users.get(key);
    		if (li != null) {
    			for (FacilioServerEndpoint ss : li) {
    				synchronized(ss) {
    					if (ss.session.getId().equals(sessionId)) {
        					li.remove(ss);
        				}
    				}
    			}
    			users.put(key, li);
    		}
    	}
    }
}