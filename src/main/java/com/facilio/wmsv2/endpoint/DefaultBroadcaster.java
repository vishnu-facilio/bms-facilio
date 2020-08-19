package com.facilio.wmsv2.endpoint;

import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections.CollectionUtils;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultBroadcaster {

    private static final DefaultBroadcaster broadcaster = new DefaultBroadcaster();
    private final List<LiveSession> sessions = new ArrayList<>();

    static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    protected void push(LiveSession session, Message message) throws IOException {
        try {
            if (session.matchTopic(message.getTopic())) {
                session.getSession().getBasicRemote().sendObject(message);
            }
        } catch (EncodeException ex) {
            ex.printStackTrace();
        }
    }

    public void addSession(LiveSession session) {
        sessions.add(session);
    }

    public void removeSession(LiveSession session) {
        sessions.remove(session);
    }

    public void broadcast(Message data) throws IOException {
        for (LiveSession session : sessions) {
            push(session, data);
        }
    }

    public void broadcast(LiveSession session, Message data) throws IOException {
        push(session, data);
    }

    public void broadcast(Collection<LiveSession> sessions, Message data) throws IOException {
        if (CollectionUtils.isNotEmpty(sessions)) {
            for (LiveSession session : sessions) {
                push(session, data);
            }
        }
    }
}
