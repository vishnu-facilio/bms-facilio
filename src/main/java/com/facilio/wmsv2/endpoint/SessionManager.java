package com.facilio.wmsv2.endpoint;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionManager {

	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());

	private HashMap<String, LiveSession> liveSessions = new HashMap<>();

	private Map<String, Set<String>> topicSessionMap = new HashMap<>();

	private static SessionManager INSTANCE;
	
	private DefaultBroadcaster broadcaster;

	public static SessionManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionManager();
		}
		return INSTANCE;
	}

	private SessionManager() {  // Making it singleton
		broadcaster = Broadcaster.getBroadcaster();
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


	public synchronized void removeLiveSession(String sessionId) {
		logger.log(Level.FINE, "Live session remove called. sid: "+ sessionId);
		if (liveSessions.containsKey(sessionId)) {
			this.unsubscribeEligibleTopics(liveSessions.get(sessionId));
			liveSessions.remove(sessionId);
		}
	}

	public Set<String> getTopicsLiveSession(String topic) {
		return topicSessionMap.getOrDefault(topic, new HashSet<>());
	}

	public void addTopicsLiveSession(String topic, String sessionId) {
		Set<String> sessionIdSet = getTopicsLiveSession(topic);
		sessionIdSet.add(sessionId);
		topicSessionMap.put(topic, sessionIdSet);
	}

	public synchronized boolean removeTopicsLiveSession(String topic, String sessionId) {
		Set<String> sessionIds = getTopicsLiveSession(topic);
		sessionIds.remove(sessionId);
		if(sessionIds.isEmpty()) {
			topicSessionMap.remove(topic);
			return true;
		}
		topicSessionMap.put(topic, sessionIds);
		return false;
	}

	public void unsubscribeEligibleTopics(LiveSession liveSession) {
		List<String> unsubscribeTopics = new ArrayList<>();
		for(String topic : liveSession.getTopics()) {
			if(removeTopicsLiveSession(topic, liveSession.getId())) {
				unsubscribeTopics.add(topic);
			}
		}
		if(!unsubscribeTopics.isEmpty()){
			this.broadcaster.unsubscribe(unsubscribeTopics.toArray(new String[unsubscribeTopics.size()]));
		}
	}

	public void setBroadcaster(DefaultBroadcaster broadcaster) {
		if (!Objects.equals(broadcaster, this.broadcaster)) {
			this.broadcaster = broadcaster;
		}
	}
}
