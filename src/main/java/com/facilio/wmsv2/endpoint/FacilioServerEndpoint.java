package com.facilio.wmsv2.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.User;
import com.facilio.iam.accounts.impl.IAMUserBeanImpl;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.wms.endpoints.FacilioServerConfigurator;
import com.facilio.wms.endpoints.LiveSession.LiveSessionType;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.MessageDecoder;
import com.facilio.wmsv2.message.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shivaraj
 */
@ServerEndpoint(
        value="/websocket/connect",
        configurator = FacilioServerConfigurator.class,
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class FacilioServerEndpoint 
{
    private static final Logger log = Logger.getLogger(FacilioServerEndpoint.class.getName());
    
    private static final String HANDSHAKE_REQUEST = "handshakereq";
    private static final String SESSION_TYPE = "type";
//    private static final String SESSION_SOURCE = "source";
    private static final String AUTH_TOKEN = "token";

    private DefaultBroadcaster broadcaster = DefaultBroadcaster.getBroadcaster();
    private Processor processor = Processor.getInstance();

//    private Session session;
//    private static final Set<FacilioServerEndpoint> endpoints = new CopyOnWriteArraySet<FacilioServerEndpoint>();

	public FacilioServerEndpoint() {
		System.out.println("Initializing");
	}

    @OnOpen
    public void onOpen(Session session) throws Exception
    {
    	LiveSession liveSession = validateSession(session);
    	if (liveSession != null) {
    		log.log(Level.INFO, "Session started => " + liveSession);

			SessionManager.getInstance().setBroadcaster(broadcaster);
	        SessionManager.getInstance().addLiveSession(liveSession);
    	}
    	else {
    		throw new Exception("Invalid user!");
    	}
    }
    
    private LiveSession validateSession(Session session) throws Exception {
    	
    	HandshakeRequest hreq = (HandshakeRequest) session.getUserProperties().get(HANDSHAKE_REQUEST);
		
		Map<String, List<String>> headers = hreq.getHeaders();
		Map<String, List<String>> params = hreq.getParameterMap();
		
		if (headers != null || params != null) {
		
			String idToken = null;
			String deviceToken = null;
			LiveSessionType sessionType = LiveSessionType.APP;
//			LiveSession.LiveSessionSource sessionSource = LiveSession.LiveSessionSource.WEB;
			
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
				sessionType = LiveSessionType.valueOf(params.get(SESSION_TYPE).get(0));
			}

			if (params.containsKey(AUTH_TOKEN) && sessionType != null) {
				if (sessionType == LiveSessionType.DEVICE) {
					deviceToken = params.get(AUTH_TOKEN).get(0);
				}
				else {
					idToken = params.get(AUTH_TOKEN).get(0);
				}
			}
			
			Account currentAccount = null;
			System.out.println("idToken present: "+ (idToken != null ? true : false));
			if (idToken != null) {
				IAMAccount iamAccount = IAMUserUtil.verifiyFacilioTokenv3(idToken, false, "web");
				if (iamAccount == null) {
					throw new Exception("Invalid auth!");
				}
				currentAccount = new Account(iamAccount.getOrg(), new User(iamAccount.getUser()));
			}
			else if (deviceToken != null) {
				DecodedJWT verifiedJwt = IAMUserBeanImpl.validateJWT(deviceToken, "auth0");
				if (verifiedJwt == null) {
					throw new Exception("Invalid auth!");
				}
			}
			
			LiveSession liveSession = new LiveSession().setId(session.getId()).setCurrentAccount(currentAccount).setSession(session).setCreatedTime(System.currentTimeMillis()).setLiveSessionType(sessionType);
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

		message.setLiveSession(ls);
		message = processor.filterIncomingMessage(message);
		if (message != null) {
			BaseHandler handler = processor.getHandler(message.getTopic());
			if (handler != null) {
				handler.processIncomingMessage(message);
			}
		}
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException 
    {
    	log.info("Session started closed" +session.getId());
//    	endpoints.remove(this);
    	SessionManager.getInstance().removeLiveSession(session.getId());
    	session.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) 
    {
    	log.info("Session started Error" +throwable.getMessage());
    }
}