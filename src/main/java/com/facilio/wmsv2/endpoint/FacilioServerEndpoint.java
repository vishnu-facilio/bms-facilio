package com.facilio.wmsv2.endpoint;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.bmsconsole.util.DevicesUtil;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.wms.endpoints.FacilioServerConfigurator;
import com.facilio.wmsv2.endpoint.LiveSession.LiveSessionType;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.MessageDecoder;
import com.facilio.wmsv2.message.MessageEncoder;

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
		
		if (headers != null || params != null) {
		
			LiveSessionType sessionType = LiveSessionType.APP;
			LiveSession.LiveSessionSource sessionSource = LiveSession.LiveSessionSource.WEB;
			
			if (params.containsKey(SESSION_TYPE)) {
				sessionType = LiveSessionType.valueOf(params.get(SESSION_TYPE).get(0));
			}
			
			if (params.containsKey(SESSION_SOURCE)) {
				sessionSource = LiveSession.LiveSessionSource.valueOf(params.get(SESSION_SOURCE).get(0));
			}

			/*if (params.containsKey(AUTH_TOKEN) && sessionType != null) {
				if (sessionType == LiveSessionType.DEVICE) {
					deviceToken = params.get(AUTH_TOKEN).get(0);
				}
				else {
					idToken = params.get(AUTH_TOKEN).get(0);
				}
			}*/
			
			Organization org = null;
			User user = null;
			
			if (sessionType == LiveSessionType.REMOTE_SCREEN) {
				RemoteScreenContext remoteScreen = ScreenUtil.getRemoteScreen(id);
				if (remoteScreen != null) {
					org = IAMUtil.getOrgBean().getOrgv2(remoteScreen.getOrgId());
				}
			}
			else if (sessionType == LiveSessionType.DEVICE) {
				ConnectedDeviceContext connectedDevice = DevicesUtil.getConnectedDevice(id);
				if (connectedDevice != null) {
					org = IAMUtil.getOrgBean().getOrgv2(connectedDevice.getOrgId());
				}
			}
			else {
				user = AccountUtil.getUserBean().getUserInternal(id, false);
				org = IAMUtil.getOrgBean().getOrgv2(user.getOrgId());
			}
			
			if (org != null || user != null) {
				Account currentAccount = new Account(org, user);
				LiveSession liveSession = new LiveSession().setId(session.getId()).setCurrentAccount(currentAccount).setSession(session).setCreatedTime(System.currentTimeMillis()).setLiveSessionType(sessionType);
				return liveSession;
			}
			else {
				throw new Exception("Invalid user!");
			}
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