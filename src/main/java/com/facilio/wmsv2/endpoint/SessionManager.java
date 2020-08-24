package com.facilio.wmsv2.endpoint;

import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Message;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionManager {
	
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	private HashMap<String, LiveSession> liveSessions = new HashMap<>();
	private static SessionManager INSTANCE;
	private DefaultBroadcaster broadcaster;

	public static SessionManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionManager();
		}
		return INSTANCE;
	}
	
	public void addLiveSession(LiveSession liveSession) {
		this.liveSessions.put(liveSession.getId(), liveSession);
		logger.log(Level.FINE, "Live session added => " + liveSession);
	}
	
	public LiveSession getLiveSession(String sessionId) {
		return liveSessions.get(sessionId);
	}
	
	public Collection<LiveSession> getLiveSessions() {
		return liveSessions.values();
	}
	
	public Collection<LiveSession> getLiveSessions(com.facilio.wms.endpoints.LiveSession.LiveSessionType liveSessionType) {
		
		List<LiveSession> sessionList = new ArrayList<>();
		
		Iterator<String> itr = liveSessions.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			LiveSession liveSession = liveSessions.get(key);
			if (liveSession != null && liveSession.getLiveSessionType().equals(liveSessionType)) {
				sessionList.add(liveSession);
			}
		}
		
		return sessionList;
	}

	public Collection<LiveSession> getLiveSessions(String topic, long orgId) {
		List<LiveSession> sessionList = new ArrayList<>();
		for (LiveSession session : liveSessions.values()) {
			if (session.getOrgId() == orgId && session.matchTopic(topic)) {
				sessionList.add(session);
			}
		}
		return sessionList;
	}
	
	public Collection<LiveSession> getLiveSessions(com.facilio.wms.endpoints.LiveSession.LiveSessionType liveSessionType, long id) {
		
		List<LiveSession> sessionList = new ArrayList<>();
		
		Iterator<String> itr = liveSessions.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			LiveSession liveSession = liveSessions.get(key);
			if (liveSession != null && liveSession.getLiveSessionType().equals(liveSessionType) && liveSession.getOuid() == id) {
				sessionList.add(liveSession);
			}
		}
		
		return sessionList;
	}
	
	public synchronized void removeLiveSession(String sessionId) {
		
		logger.log(Level.FINE, "Live session remove called. sid: "+ sessionId);
		
		if (liveSessions.containsKey(sessionId)) {
			liveSessions.remove(sessionId);
		}
	}
	
	public void sendMessage(Message message) {
		try {
			broadcaster.broadcast(message);
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Send message failed: " + message.toString(), ex);
		}
	}

	public void setBroadcaster(DefaultBroadcaster broadcaster) {
		if (!Objects.equals(broadcaster, this.broadcaster)) {
			this.broadcaster = broadcaster;
		}
	}
}
