package com.facilio.wms.endpoints;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.iam.accounts.impl.IAMUserBeanImpl;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.service.FacilioService;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageDecoder;
import com.facilio.wms.message.MessageEncoder;

/**
 *
 * @author Shivaraj
 */
@ServerEndpoint(
        value="/websocket/{id}",
        configurator = FacilioServerConfigurator.class,
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class FacilioServerEndpoint 
{
    private final Logger log = Logger.getLogger(getClass().getName());
    
    private static final String HANDSHAKE_REQUEST = "handshakereq";
    private static final String SESSION_TYPE = "type";
    private static final String SESSION_SOURCE = "source";
    private static final String AUTH_TOKEN = "token";

    private Session session;
    private static final Set<FacilioServerEndpoint> endpoints = new CopyOnWriteArraySet<FacilioServerEndpoint>();

    @OnOpen
    public void onOpen(Session session, @PathParam("id") long id) throws Exception 
    {
    	LiveSession liveSession = validateSession(id, session);
    	if (liveSession != null) {
    		
    		log.log(Level.INFO, "Session started => " + liveSession);
    		
    		this.session = session;
	        endpoints.add(this);
	        
	        SessionManager.getInstance().addLiveSession(liveSession);
    	}
    	else {
    		throw new Exception("Invalid user!");
    	}
    }
    
    private LiveSession validateSession(long id, Session session) throws Exception {
    	
    	HandshakeRequest hreq = (HandshakeRequest) session.getUserProperties().get(HANDSHAKE_REQUEST);
		
		Map<String, List<String>> headers = hreq.getHeaders();
		Map<String, List<String>> params = hreq.getParameterMap();
		
		if (headers != null || params != null) {
		
			String idToken = null;
			String deviceToken = null;
			LiveSession.LiveSessionType sessionType = LiveSession.LiveSessionType.APP;
			LiveSession.LiveSessionSource sessionSource = LiveSession.LiveSessionSource.WEB;
			
			List<String> cookieValues = headers.get("cookie");
			if (cookieValues != null && cookieValues.size() > 0) {
				for (String cookie : cookieValues.get(0).split(";")) {
					if (cookie.split("=").length > 1) {
						String key = cookie.split("=")[0].trim();
						String value = cookie.split("=")[1].trim();
						
						if ("fc.idToken.facilio".equals(key)) {
							idToken = value;
						}
						else if ("fc.deviceTokenNew".equals(key)) {
							deviceToken = value;
						}
					}
				}
			}
			
			List<String> authHeader = headers.get("Authorization");
			if (authHeader != null && authHeader.size() > 0) {
				if (authHeader.get(0).startsWith("Bearer facilio ")) {
					idToken = authHeader.get(0).replace("Bearer facilio ", "");
				}
				else if (authHeader.get(0).startsWith("Bearer device ")) {
					deviceToken = authHeader.get(0).replace("Bearer device ", "");
				}
			}
			
			if (params.containsKey(SESSION_TYPE)) {
				sessionType = LiveSession.LiveSessionType.valueOf(params.get(SESSION_TYPE).get(0));
			}
			
			if (params.containsKey(SESSION_SOURCE)) {
				sessionSource = LiveSession.LiveSessionSource.valueOf(params.get(SESSION_SOURCE).get(0));
			}
			
			if (params.containsKey(AUTH_TOKEN) && sessionType != null) {
				if (sessionType == LiveSessionType.DEVICE) {
					deviceToken = params.get(AUTH_TOKEN).get(0);
				}
				else {
					idToken = params.get(AUTH_TOKEN).get(0);
				}
			}
			
			if (idToken != null) {
				IAMAccount iamAccount = IAMUserUtil.verifiyFacilioToken(idToken, false, null, "app", null, null);
				if (iamAccount == null) {
					throw new Exception("Invalid auth!");
				}
			}
			else if (deviceToken != null) {
				DecodedJWT verifiedJwt = IAMUserBeanImpl.validateJWT(deviceToken, "auth0");
				if (verifiedJwt == null) {
					throw new Exception("Invalid auth!");
				}
			}
			
			LiveSession liveSession = new LiveSession().setId(id).setSession(session).setCreatedTime(System.currentTimeMillis()).setLiveSessionType(sessionType).setLiveSessionSource(sessionSource);
			return liveSession;
		}
		else {
			throw new Exception("Invalid params!");
		}
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException 
    {
    	System.out.println("Session started message to ::" +session.getPathParameters()+ ":::sessionid" + session.getId()+"  msg: "+message.getContent()+"  cc: "+message);
    	
    	LiveSession ls = SessionManager.getInstance().getLiveSession(session.getId());
    	
    	long id = Long.parseLong(session.getPathParameters().get("id"));
    	message.setTo(id);
    	message.setSessionType(ls.getLiveSessionType());
    	if (ls.getLiveSessionType() == LiveSessionType.REMOTE_SCREEN) {
    		try {
				RemoteScreenContext remoteScreen = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getRemoteScreen(id));
				message.setOrgId(remoteScreen.getOrgId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
//    	else if (ls.getLiveSessionType() == LiveSessionType.DEVICE) {
//    		try {
//    			ConnectedDeviceContext deviceContext = FacilioService.runAsServiceWihReturn(() ->  DevicesUtil.getConnectedDevice(id));
//				message.setOrgId(deviceContext.getOrgId());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
    	if ("subscribe".equalsIgnoreCase(message.getAction())) {
    		PubSubManager.getInstance().subscribe(message);
    	}
    	else if ("unsubscribe".equalsIgnoreCase(message.getAction())) {
    		PubSubManager.getInstance().unsubscribe(message);
    	}
    	else if (message.getContent() != null && message.getContent().get("ping") != null) {
    		message.getContent().put("ping", "pong");
    		session.getBasicRemote().sendObject(message);
    		
    		SessionManager.getInstance().getLiveSession(session.getId()).setLastMsgTime(System.currentTimeMillis());
    	}
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
    	System.out.println("Session started closed" +session.getId());
    	endpoints.remove(this);
    	SessionManager.getInstance().removeLiveSession(session.getId());
    	session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
    	System.out.println("Session started Error" +throwable.getMessage());
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
}