package com.facilio.wmsv2.endpoint;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections.CollectionUtils;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class DefaultBroadcaster {

    private static final DefaultBroadcaster broadcaster = new DefaultBroadcaster();

    static DefaultBroadcaster getDefaultBroadcaster() {
        String wmsBroadcaster = FacilioProperties.getWmsBroadcaster();
        switch (wmsBroadcaster) {
            case "kafka":
                return KafkaBroadcaster.getBroadcaster();
            case "default":
            default:
                return DefaultBroadcaster.getBroadcaster();
        }
    }

    static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    protected DefaultBroadcaster() { // Making it singleton

    }

    protected void pushToLiveSession(Message message) {
        message = Processor.getInstance().filterOutgoingMessage(message);
        BaseHandler handler = Processor.getInstance().getHandler(message.getTopic());
        message = handler.processOutgoingMessage(message);

        if (message == null) {
            return;
        }

        switch (handler.getDeliverTo()) {
            case ALL: {
                sendObject(SessionManager.getInstance().getLiveSessions(), message);
                break;
            }

            case USER: {
                Long to = message.getTo();
                Collection<LiveSession> liveSessions = SessionManager.getInstance().getLiveSessions(message.getSessionTypeEnum(), to, message.getOrgId(), message.getAppId());
                sendObject(liveSessions, message);
                break;
            }

            case ORG: {
                Long orgId = message.getOrgId();
                Collection<LiveSession> liveSessions = SessionManager.getInstance().getLiveSessions(message.getTopic(), orgId);
                sendObject(liveSessions, message);
                break;
            }

            case APP: {
                Long appId = message.getAppId();
                Collection<LiveSession> livesessions = SessionManager.getInstance().getLiveSessionsForAppUsers(message.getTopic(), appId);
                sendObject(livesessions, message);
                break;
            }

            case SESSION: {
                sendObject(message.getLiveSession(), message);
                break;
            }
        }
    }

    private void sendObject(LiveSession session, Message message) {
        if (session == null) {
            return;
        }
        sendObject(Collections.singletonList(session), message);
    }

    private void sendObject(Collection<LiveSession> sessions, Message message) {
        if (CollectionUtils.isEmpty(sessions)) {
            return;
        }

        try {
            for (LiveSession session : sessions) {
                if (session.matchTopic(message.getTopic())) {
                    session.getSession().getBasicRemote().sendObject(message);
                }
            }
        } catch (IOException | EncodeException ex) {
            ex.printStackTrace();
        }
    }

    public void broadcast(Message data, Group group) throws Exception {
        // ignoring sendToAllWorkers in default
        pushToLiveSession(data);
    }
}
