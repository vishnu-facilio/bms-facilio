package com.facilio.wmsv2.endpoint;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.wms.endpoints.FacilioServerConfigurator;
import com.facilio.wmsv2.endpoint.LiveSession.LiveSessionType;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.MessageDecoder;
import com.facilio.wmsv2.message.MessageEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
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
        value="/websocket/connect/{id}",
        configurator = FacilioServerConfigurator.class,
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class FacilioServerEndpoint 
{
    private static final Logger log = Logger.getLogger(FacilioServerEndpoint.class.getName());
    
    private static final String HANDSHAKE_REQUEST = "handshakereq";
    private static final String SESSION_TYPE = "type";
    private static final String SESSION_SOURCE = "source";
    private static final String AUTH_TOKEN = "token";

    private DefaultBroadcaster broadcaster = null;
    private Processor processor = Processor.getInstance();

//    private Session session;
//    private static final Set<FacilioServerEndpoint> endpoints = new CopyOnWriteArraySet<FacilioServerEndpoint>();

	public FacilioServerEndpoint() {
		System.out.println("Initializing");
	}
	
	private DefaultBroadcaster getBroadcaster() {
		if (this.broadcaster == null) {
			this.broadcaster = DefaultBroadcaster.getDefaultBroadcaster();
		}
		return this.broadcaster;
	}

    @OnOpen
    public void onOpen(Session session, @PathParam("id") long id) throws Exception
    {
    	LiveSession liveSession = validateSession(id, session);
    	if (liveSession != null) {
    		log.log(Level.INFO, "Session started => " + liveSession);

			SessionManager.getInstance().setBroadcaster(getBroadcaster());
	        SessionManager.getInstance().addLiveSession(liveSession);
    	}
    	else {
    		throw new Exception("Invalid user!");
    	}
    }
    
    private LiveSession validateSession(Long id, Session session) throws Exception {
    	
    	HandshakeRequest hreq = (HandshakeRequest) session.getUserProperties().get(HANDSHAKE_REQUEST);
		
		Map<String, List<String>> headers = hreq.getHeaders();
		Map<String, List<String>> params = hreq.getParameterMap();

		String facilioToken = getUserCookie(hreq, "fc.idToken.facilio");;
		if(facilioToken == null) {
			facilioToken = getUserCookie(hreq, "fc.idToken.facilioportal");
		}
		String headerToken = headers.containsKey("Authorization") ? headers.get("Authorization").get(0) : null;

		if (facilioToken != null || headerToken != null) {

			if (headerToken != null && headerToken.trim().length() > 0) {
				if (headerToken.startsWith("Bearer facilio ")) {
					facilioToken = headerToken.replace("Bearer facilio ", "");
				} else if (headerToken.startsWith("Bearer Facilio ")) { // added this check for altayer emsol data // Todo remove this later
					facilioToken = headerToken.replace("Bearer Facilio ", "");
				} else {
					facilioToken = headerToken.replace("Bearer ", "");
				}
			}

			String overrideSessionCookie = getUserCookie(hreq, "fc.overrideSession");
			boolean overrideSessionCheck = false;
			if(overrideSessionCookie != null) {
				if("true".equalsIgnoreCase(overrideSessionCookie)) {
					overrideSessionCheck = true;
				}
			}

			String userType = "web";
			String deviceType = headers.containsKey("X-Device-Type") ? headers.get("X-Device-Type").get(0) : null;
			if (!StringUtils.isEmpty(deviceType)
					&& ("android".equalsIgnoreCase(deviceType) || "ios".equalsIgnoreCase(deviceType))) {
				userType = "mobile";
			}

			LiveSessionType sessionType = LiveSessionType.APP;
			LiveSession.LiveSessionSource sessionSource = LiveSession.LiveSessionSource.WEB;

			if (params.containsKey(SESSION_TYPE)) {
				sessionType = LiveSessionType.valueOf(params.get(SESSION_TYPE).get(0));
			}

			if (params.containsKey(SESSION_SOURCE)) {
				sessionSource = LiveSession.LiveSessionSource.valueOf(params.get(SESSION_SOURCE).get(0));
			}

			IAMAccount iamAccount = IAMUserUtil.verifiyFacilioTokenv3(facilioToken, overrideSessionCheck, userType);
			if (iamAccount != null) {
				Account currentAccount = new Account(iamAccount.getOrg(), new User(iamAccount.getUser()));
				if (params.containsKey("app")) {
					List<String> apps = params.get("app");
					if (CollectionUtils.isNotEmpty(apps)) {
						String appName = apps.get(0);
						ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", org.getOrgId());
						ApplicationContext applicationContext = moduleCRUD.getApplicationForLinkName(appName);
						currentAccount.setApp(applicationContext);
					}
				}
				LiveSession liveSession = new LiveSession().setId(session.getId()).setCurrentAccount(currentAccount).setSession(session).setCreatedTime(System.currentTimeMillis()).setLiveSessionType(sessionType);
				return liveSession;
			}
		}
		throw new Exception("Invalid user!");
    }

	public String getUserCookie(HandshakeRequest handshakeRequest, String cookieName) {
		Map<String, List<String>> headers = handshakeRequest.getHeaders();
		List<String> cookieValues = headers.get("cookie");
		if (cookieValues != null && cookieValues.size() > 0) {
			for (String cookie : cookieValues.get(0).split(";")) {
				if (cookie.split("=").length > 1) {
					String key = cookie.split("=")[0].trim();
					String value = cookie.split("=")[1].trim();
					if (key.equals(cookieName)) {
						return value;
					}
				}
			}
		}
		return null;
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