package com.facilio.wmsv2.endpoint;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.util.TopicUtil;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LiveSession {
	
	private static final Logger logger = Logger.getLogger(LiveSession.class.getName());
	
	private String id;
	private long orgId;
	private User user;
	private Session session;
	private LiveSessionType liveSessionType;
	private LiveSessionSource liveSessionSource;
	private long createdTime;
	private long lastMsgTime;

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

	private List<String> topics = new ArrayList<>();
	public LiveSession subscribe(String topic) {
		this.topics.add(topic);
		return this;
	}
	public LiveSession unSubscribe(String topic) {
		this.topics.remove(topic);
		return this;
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
				", session=" + session.getId() +
				", liveSessionType=" + liveSessionType +
				", createdTime=" + createdTime +
				", lastMsgTime=" + lastMsgTime +
				", topics=" + topics +
				'}';
	}

	public LiveSession setCurrentAccount(Account currentAccount) {
		if (currentAccount != null) {
			if (currentAccount.getOrg() != null) {
				orgId = currentAccount.getOrg().getId();
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