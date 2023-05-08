package com.facilio.wmsv2.util;

import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.SessionInfo;
import org.apache.commons.collections.CollectionUtils;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class WmsUtil {

    public static void pushToLiveSession(Message message) {

        SessionInfo sessionInfo = SessionInfo.getSessionInfo(message);
        BaseHandler handler = Processor.getInstance().getHandler(message.getTopic());

        switch (handler.getDeliverTo()) {
            case ALL: {
                sendObject(SessionManager.getInstance().getLiveSessions(), message);
                break;
            }

            case USER: {
                Long to = message.getTo();
                Collection<LiveSession> liveSessions = SessionManager.getInstance().getLiveSessions(sessionInfo.getSessionTypeEnum(), to, message.getOrgId(), message.getAppId());
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
                sendObject(sessionInfo.getLiveSession(), message);
                break;
            }
        }
    }

    private static void sendObject(LiveSession session, Message message) {
        if (session == null) {
            return;
        }
        sendObject(Collections.singletonList(session), message);
    }

    private static void sendObject(Collection<LiveSession> sessions, Message message) {
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
}
