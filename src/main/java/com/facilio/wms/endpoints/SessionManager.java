package com.facilio.wms.endpoints;

import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;

import javax.websocket.Session;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionManager {
	
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	private HashMap<String, List<UserSession>> sessions = new HashMap<>();
	private HashMap<String, List<RemoteSession>> remoteSessions = new HashMap<>();
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
		
		logger.log(Level.FINE, "User session added. uid: "+session.getUid()+" currentOpenSessions: "+ this.sessions.get(session.getKey()).size());
	}
	
	public void addRemoteSession(RemoteSession session) {
		
		if (!this.remoteSessions.containsKey(session.getKey())) {
			this.remoteSessions.put(session.getKey(), new ArrayList<RemoteSession>());
		}
		this.remoteSessions.get(session.getKey()).add(session);
		
		logger.log(Level.FINE, "Remote screen session added. uid: "+session.getId()+" currentOpenSessions: "+ this.remoteSessions.get(session.getKey()).size());
	}
	
	public List<UserSession> getUserSessions(long uid) {
		return this.sessions.get(String.valueOf(uid));
	}
	public List<RemoteSession> getRemoteSessions(long id) {
		return this.remoteSessions.get(String.valueOf(id));
	}
	public Set<String> getActiveUsers() {
		return this.sessions.keySet();
	}
	public synchronized void removeUserSession(String sessionId) {
		
		logger.log(Level.FINE, "User session remove called. sid: "+sessionId);
		
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
					logger.log(Level.FINE, "User session removed. uid: "+key+" sid: "+sessionId);
				}
			}
		}
	}
	public synchronized void removeRemoteSession(String sessionId) {
		
		logger.log(Level.FINE, "Remote session remove called. sid: "+sessionId);
		
		Iterator<String> itr = remoteSessions.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<RemoteSession> sessionList = this.remoteSessions.get(key);
			if (sessionList != null) {
				int removeIndex = -1;
				for (int i=0; i< sessionList.size(); i++) {
					RemoteSession us = sessionList.get(i);
					if (us.getSession().getId().equals(sessionId)) {
						removeIndex = i;
						break;
					}
				}
				if (removeIndex >= 0) {
					sessionList.remove(removeIndex);
					logger.log(Level.FINE, "Remote session removed. id: "+key+" sid: "+sessionId);
				}
			}
		}
	}
	
	public long sendMessage(Message message) {
		long timeTaken = 0L;
		if (message.getMessageType() != null && message.getMessageType() == MessageType.BROADCAST) {
			broadcast(message);
		}
		else if (message.getMessageType() != null && message.getMessageType() == MessageType.REMOTE_SCREEN) {
			sendRemoteMessage(message);
		}
		else {
			timeTaken = sendUserMessage(message);
		}
		return timeTaken;
	}
	
	private long sendUserMessage(Message message) {
		logger.log(Level.FINE, "Send message called. from: "+message.getFrom()+" to: "+message.getTo());
		long timeTaken = 0L;
		List<UserSession> sessionList = getUserSessions(message.getTo());
		if (sessionList != null) {
			logger.log(Level.FINE, "Going to send message to ("+sessionList.size()+") user sessions. from: "+message.getFrom()+" to: "+message.getTo());
			for (UserSession us : sessionList) {
				try {
					timeTaken = timeTaken + us.sendMessage(message);
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
		return timeTaken;
	}
	
	public void sendRemoteMessage(Message message) {
		
		logger.log(Level.FINE, "Send remote message called. from: "+message.getFrom()+" to: "+message.getTo());

		List<RemoteSession> sessionList = getRemoteSessions(message.getTo());
		if (sessionList != null) {
			logger.log(Level.FINE, "Going to send message to ("+sessionList.size()+") remote sessions. from: "+message.getFrom()+" to: "+message.getTo());
			for (RemoteSession rs : sessionList) {
				try {
					rs.sendMessage(message);
				}
				catch (Exception e) {
					logger.log(Level.WARNING, "Send message failed. from: "+message.getFrom()+" to: "+message.getTo(), e);
				}
			}
		}
		else {
			logger.log(Level.FINE, "No active sessions exists for the remote client: "+message.getTo());
		}
	}
	
	public  void broadcast(Message message) {
		
		logger.log(Level.FINE, "Send message called. from: "+message.getFrom()+" to: "+message.getTo());
	//	System.out.println("Send message called. from: "+message.getFrom()+" to: "+message.getTo());
		
		Set<String> activeusers =  getActiveUsers() ;
		
		Iterator iter = activeusers.iterator();
		while (iter.hasNext()) {
		    String touser =(String) iter.next();
		    logger.info("Message sent to "+touser);
		    
			List<UserSession> sessionList = getUserSessions(new Long(touser));
			if (sessionList != null) {
				logger.log(Level.FINE, "Going to send message to ("+sessionList.size()+") user sessions. from: "+message.getFrom()+" to: "+touser);
				for (UserSession us : sessionList) {
					try {
						message.setTo(new Long(touser));
						us.sendMessage(message);
					}
					catch (Exception e) {
						logger.log(Level.WARNING, "Send message failed. from: "+message.getFrom()+" to: "+message.getTo(), e);
					}
				}
			}
			else {
				logger.log(Level.FINE, "No active sessions exists for the user: "+message.getTo());
			}
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
		
		public synchronized long sendMessage(Message msg) {
			long startTime = System.currentTimeMillis();
			try {
				this.session.getBasicRemote().sendObject(msg);
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Exception while send message to user session: uid: "+uid+" sid: "+session.getId()+" createdTime: "+createdTime);
			}
			return (System.currentTimeMillis() - startTime);
		}
	}
	
	public static class RemoteSession {
		
		private long id;
		private Session session;
		private long createdTime;
		
		public String getKey() {
			return String.valueOf(id);
		}
		public long getId() {
			return id;
		}
		public RemoteSession setId(long id) {
			this.id = id;
			return this;
		}
		
		public Session getSession() {
			return session;
		}
		
		public RemoteSession setSession(Session session) {
			this.session = session;
			return this;
		}
		
		public long getCreatedTime() {
			return createdTime;
		}
		
		public RemoteSession setCreatedTime(long createdTime) {
			this.createdTime = createdTime;
			return this;
		}
		
		public synchronized void sendMessage(Message msg) {
			try {
				this.session.getBasicRemote().sendObject(msg);
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Exception while send message to remote screen session session: id: "+id+" sid: "+session.getId()+" createdTime: "+createdTime);
			}
		}
	}
}
