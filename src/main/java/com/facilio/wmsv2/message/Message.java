package com.facilio.wmsv2.message;

import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.endpoint.LiveSession.LiveSessionType;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Created by Shivaraj on 16/05/2017.
 */
public class Message 
{
	private LiveSessionType sessionType = null;
	private String topic;
	private Long from;
	private Long to;
	private String action;
	private JSONObject content = new JSONObject();
	private Long timestamp;
	private Long orgId = -1l;

	@JsonIgnore
	private LiveSession liveSession;

	private static final Logger LOGGER = LogManager.getLogger(Message.class.getName());

	public Message() {
	}
	@Override
	public String toString() {
		return toJson().toString();
	}

	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public LiveSessionType getSessionType() {
		return this.sessionType;
	}

	public Message setSessionType(LiveSessionType sessionType) {
		this.sessionType = sessionType;
		return this;
	}
	
	public Long getFrom() {
		return this.from;
	}

	public Message setFrom(Long from) {
		this.from = from;
		return this;
	}
	
	public Long getOrgId() {
		return this.orgId;
	}

	public Message setOrgId(Long orgId) {
		this.orgId = orgId;
		return this;
	}
	
	public Long getTimestamp() {
		return this.timestamp;
	}

	public Message setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	public Long getTo() {
		return this.to;
	}

	public Message setTo(Long to) {
		this.to = to;
		return this;
	}

	public String getAction() {
		return action;
	}

	public Message setAction(String action) {
		this.action = action;
		return this;
	}

	public String getLiveSessionId() {
		if (liveSession != null) {
			return liveSession.getId();
		}
		return null;
	}
	public void setLiveSessionId(String uuid) {
		if (StringUtils.isNotEmpty(uuid)) {
			this.liveSession = SessionManager.getInstance().getLiveSession(uuid);
		}
	}

	public LiveSession getLiveSession() {
		return liveSession;
	}

	public void setLiveSession(LiveSession liveSession) {
		this.liveSession = liveSession;
	}

	public JSONObject getContent() {
		return content;
	}

	public Message setContent(JSONObject content) {
		this.content = content;
		return this;
	}
	
	public Message addData(String key, Object value) {
		this.content.put(key, value);
		return this;
	}
	
	public Object getData(String key) {
		return this.content.get(key);
	}

	public JSONObject toJson() {
		try {
			return FieldUtil.getAsJSON(this);
		} catch (Exception ex) {
			return new JSONObject();
		}
	}

	public static Message getMessage(JSONObject object) throws Exception {
		return FieldUtil.getAsBeanFromJson(object, Message.class);
	}

}
