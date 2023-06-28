package com.facilio.wmsv2.endpoint;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.ims.util.TopicUtil;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.wmsv2.constants.Topics;
import org.json.simple.JSONObject;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LiveSession {
	
	private static final Logger logger = Logger.getLogger(LiveSession.class.getName());
	
	private String id;
	private long orgId;
	private long appId = -1;
	private User user;
	private Session session;
	private LiveSessionType liveSessionType;
	private LiveSessionSource liveSessionSource;
	private long createdTime;
	private long lastMsgTime;

	private long lastPingTime;

	private Set<String> topics = new HashSet<>();

	public String getId() {
		return id;
	}
	
	public LiveSession setId(String id) {
		this.id = id;
		return this;
	}

	public long getOrgId() {
		return orgId;
	}
	public LiveSession setOrgId(long orgId) {
		this.orgId = orgId;
		return this;
	}

	public long getAppId() {
		return appId;
	}
	public LiveSession setAppId(long appdId) {
		this.appId = appdId;
		return this;
	}

	public User getUser() {
		return user;
	}
	public LiveSession setUser(User user) {
		this.user = user;
		return this;
	}

	public long getOuid() {
		if (user != null) {
			return user.getId();
		}
		return -1;
	}
	
	public LiveSessionType getLiveSessionType() {
		return liveSessionType;
	}
	
	public LiveSession setLiveSessionType(LiveSessionType liveSessionType) {
		this.liveSessionType = liveSessionType;
		return this;
	}
	
	public LiveSessionSource getLiveSessionSource() {
		return liveSessionSource;
	}
	
	public LiveSession setLiveSessionSource(LiveSessionSource liveSessionSource) {
		this.liveSessionSource = liveSessionSource;
		return this;
	}

	public Session getSession() {
		return session;
	}
	
	public LiveSession setSession(Session session) {
		this.session = session;
		return this;
	}
	
	public long getCreatedTime() {
		return createdTime;
	}
	
	public LiveSession setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
		return this;
	}
	
	public long getLastMsgTime() {
		return lastMsgTime;
	}
	
	public LiveSession setLastMsgTime(long lastMsgTime) {
		this.lastMsgTime = lastMsgTime;
		return this;
	}

	public LiveSession setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
		return this;
	}

	public long getLastPingTime() {
		return lastPingTime;
	}

	public LiveSession subscribe(String topic) {
		this.topics.add(topic);
		return this;
	}
	public LiveSession unSubscribe(String topic) {
		this.topics.remove(topic);
		return this;
	}

	public void flushTopics() {
		this.topics.clear();
	}

	public Set<String> getTopics() {
		return this.topics;
	}

	public boolean matchTopic(String topic) {
		// default topics match
		if (TopicUtil.matchTopic(topic, Topics.defaultSubscribedTopics)) {
			return true;
		}

		if (TopicUtil.matchTopic(topic, topics.toArray(new String[topics.size()]))) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "LiveSession{" +
				"id='" + id + '\'' +
				", orgId=" + orgId +
				", user=" + user +
				", appId=" + appId +
				", session=" + session.getId() +
				", liveSessionType=" + liveSessionType +
				", createdTime=" + createdTime +
				", lastMsgTime=" + lastMsgTime +
				", lastPingTime=" + lastPingTime +
				", topics=" + topics +
				'}';
	}

	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("orgId", orgId);
		jsonObject.put("appId", appId);
		jsonObject.put("session", session.getId());
		jsonObject.put("liveSessionType", liveSessionType);
		jsonObject.put("createdTime", createdTime);
		jsonObject.put("lastMsgTime", lastMsgTime);
		jsonObject.put("lastPingTime", lastPingTime);
		jsonObject.put("topics", topics);
		return jsonObject;
	}

	public LiveSession setCurrentAccount(Account currentAccount) {
		if (currentAccount != null) {
			if (currentAccount.getOrg() != null) {
				orgId = currentAccount.getOrg().getId();
			}
			if (currentAccount.getApp() != null) {
				appId = currentAccount.getApp().getId();
			}
			user = currentAccount.getUser();
		}
		return this;
	}
	
	public static enum LiveSessionType implements FacilioIntEnum {
		APP,
		DEVICE,
		REMOTE_SCREEN,
		SERVICE_PORTAL,
		TENANT_PORTAL,
		VENDOR_PORTAL;

		public static LiveSessionType valueOf(int sessionType) {
			if (sessionType > 0 && sessionType <= values().length) {
				return values()[sessionType - 1];
			}
			return null;
		}
	}
	
	public static enum LiveSessionSource {
		WEB,
		MOBILE,
		TABLET;
	}
}