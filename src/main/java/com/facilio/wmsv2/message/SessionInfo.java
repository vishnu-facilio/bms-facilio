package com.facilio.wmsv2.message;

import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.LiveSession.LiveSessionType;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.io.IOException;

public class SessionInfo
{
	private LiveSessionType sessionType = null;
	@JsonIgnore
	private LiveSession liveSession;

	public SessionInfo() {
	}
	@Override
	public String toString() {
		return toJson().toString();
	}

	public int getSessionType() {
		if (sessionType != null) {
			return sessionType.getIndex();
		}
		return -1;
	}

	public LiveSessionType getSessionTypeEnum() {
		return sessionType;
	}

	public SessionInfo setSessionType(LiveSessionType sessionType) {
		this.sessionType = sessionType;
		return this;
	}

	public SessionInfo setSessionType(int sessionType) {
		this.sessionType = LiveSessionType.valueOf(sessionType);
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

	public JSONObject toJson() {
		try {
			return FieldUtil.getAsJSON(this);
		} catch (Exception ex) {
			return new JSONObject();
		}
	}

	public static SessionInfo getSessionInfo(Message message) {
		JSONObject object = message.getSessionInfo();
		if(object == null || object.isEmpty()) {
			return null;
		}
		try {
			return FieldUtil.getAsBeanFromJson(object, SessionInfo.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
