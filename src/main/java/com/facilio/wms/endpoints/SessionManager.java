package com.facilio.wms.endpoints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;

public class SessionManager {
	
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	private HashMap<String, LiveSession> liveSessions = new HashMap<>();
	private static SessionManager INSTANCE; 
	
	public static SessionManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionManager();
		}
		return INSTANCE;
	}
	
	public void addLiveSession(LiveSession liveSession) {
		
		this.liveSessions.put(liveSession.getKey(), liveSession);
		
		logger.log(Level.FINE, "Live session added => " + liveSession);
	}
	
	public LiveSession getLiveSession(String sessionId) {
		return liveSessions.get(sessionId);
	}
	
	public Collection<LiveSession> getLiveSessions() {
		return liveSessions.values();
	}
	
	public Collection<LiveSession> getLiveSessions(LiveSession.LiveSessionType liveSessionType) {
		
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
	
	public Collection<LiveSession> getLiveSessions(LiveSession.LiveSessionType liveSessionType, long id) {
		
		List<LiveSession> sessionList = new ArrayList<>();
		
		Iterator<String> itr = liveSessions.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			LiveSession liveSession = liveSessions.get(key);
			if (liveSession != null && liveSession.getLiveSessionType().equals(liveSessionType) && liveSession.getId() == id) {
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
	
	public long sendMessage(Message message) {
		long timeTaken = 0L;
		if (message.getMessageType() != null && message.getMessageType() == MessageType.BROADCAST) {
			broadcast(message);
		}
		else {
			Collection<LiveSession> sessionList = getLiveSessions(message.getSessionType(), message.getTo());
			if (sessionList != null && sessionList.size() > 0) {
				logger.log(Level.FINE, "Going to send message to ("+sessionList.size()+") user sessions. from: "+message.getFrom()+" to: "+message.getTo());
				for (LiveSession ls : sessionList) {
					try {
						timeTaken = timeTaken + ls.sendMessage(message);
					}
					catch (Exception e) {
						logger.log(Level.WARNING, "Send message failed. from: "+message.getFrom()+" to: "+message.getTo(), e);
					}
				}
			}
			else {
				logger.log(Level.FINE, "No active sessions exists for the user: "+message.getTo());
			}
			if(sessionList != null && sessionList.size() > 0) {
				logger.fine("Session size " + sessionList.size() + " " + timeTaken);
			}
		}
		return timeTaken;
	}
	
	public void broadcast(Message message) {
		
		logger.log(Level.FINE, "Send message called. from: "+message.getFrom()+" to: "+message.getTo());
		
		Collection<LiveSession> liveSessionList = liveSessions != null ? liveSessions.values() : null;
		
		if (liveSessionList != null && liveSessionList.size() > 0) {
			for (LiveSession liveSession : liveSessionList) {
				try {
					message.setTo(new Long(liveSession.getId()));
					liveSession.sendMessage(message);
				}
				catch (Exception e) {
					logger.log(Level.WARNING, "Send message failed. from: "+message.getFrom()+" to: "+message.getTo(), e);
				}
			}
		}
		else {
			logger.log(Level.INFO, "No active sessions exists for the user: "+message.getTo());
		}
	}
}
