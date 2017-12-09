package com.facilio.wms.endpoints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import com.facilio.wms.message.Message;

public class SessionManager {
	
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	private HashMap<String, List<UserSession>> sessions = new HashMap<>();
	private static SessionManager INSTANCE; 
	
	public static SessionManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionManager();
		}
		return INSTANCE;
	}
	
	public void addUserSession(UserSession session) {
		
		if (!this.sessions.containsKey(session.getKey())) {
			this.sessions.put(session.getKey(), new ArrayList<UserSession>());
		}
		this.sessions.get(session.getKey()).add(session);
		
		logger.log(Level.INFO, "User session added. uid: "+session.getUid()+" currentOpenSessions: "+ this.sessions.get(session.getKey()).size());
	}
	
	public List<UserSession> getUserSessions(long uid) {
		return this.sessions.get(String.valueOf(uid));
	}
	
	public synchronized void removeUserSession(String sessionId) {
		
		logger.log(Level.INFO, "User session remove called. sid: "+sessionId);
		
		Iterator<String> itr = sessions.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<UserSession> sessionList = this.sessions.get(key);
			if (sessionList != null) {
				int removeIndex = -1;
				for (int i=0; i< sessionList.size(); i++) {
					UserSession us = sessionList.get(i);
					if (us.getSession().getId().equals(sessionId)) {
						removeIndex = i;
						break;
					}
				}
				if (removeIndex >= 0) {
					sessionList.remove(removeIndex);
					logger.log(Level.INFO, "User session removed. uid: "+key+" sid: "+sessionId);
				}
			}
		}
	}
	
	public void sendMessage(Message message) {
		
		logger.log(Level.INFO, "Send message called. from: "+message.getFrom()+" to: "+message.getTo());
		System.out.println("Send message called. from: "+message.getFrom()+" to: "+message.getTo());
		
		List<UserSession> sessionList = getUserSessions(message.getTo());
		if (sessionList != null) {
			logger.log(Level.INFO, "Going to send message to ("+sessionList.size()+") user sessions. from: "+message.getFrom()+" to: "+message.getTo());
			for (UserSession us : sessionList) {
				try {
					us.sendMessage(message);
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
	
	public static class UserSession {
		
		private long uid;
		private Session session;
		private long createdTime;
		
		public String getKey() {
			return String.valueOf(uid);
		}
		public long getUid() {
			return uid;
		}
		public UserSession setUid(long uid) {
			this.uid = uid;
			return this;
		}
		
		public Session getSession() {
			return session;
		}
		
		public UserSession setSession(Session session) {
			this.session = session;
			return this;
		}
		
		public long getCreatedTime() {
			return createdTime;
		}
		
		public UserSession setCreatedTime(long createdTime) {
			this.createdTime = createdTime;
			return this;
		}
		
		public synchronized void sendMessage(Message msg) {
			try {
				this.session.getBasicRemote().sendObject(msg);
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Exception while send message to user session: uid: "+uid+" sid: "+session.getId()+" createdTime: "+createdTime);
			}
		}
	}
}
