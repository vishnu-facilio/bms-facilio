package com.facilio.wms.endpoints;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import com.facilio.accounts.dto.Account;
import com.facilio.wms.message.Message;

public class LiveSession {
	
	private static final Logger logger = Logger.getLogger(LiveSession.class.getName());
	
	private long id;
	private Account currentAccount;
	private Session session;
	private LiveSessionType liveSessionType;
	private LiveSessionSource liveSessionSource;
	private long createdTime;
	private long lastMsgTime;
	
	public String getKey() {
//		return String.valueOf(session.getId() + "#" + id + "_" + liveSessionType + "_" + liveSessionSource + "_" + createdTime + "_" + lastMsgTime);
		return session.getId();
	}
	
	public long getId() {
		return id;
	}
	
	public LiveSession setId(long id) {
		this.id = id;
		return this;
	}
	
	public Account getCurrentAccount() {
		return currentAccount;
	}
	
	public LiveSession setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
		return this;
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
	
	public synchronized long sendMessage(Message msg) {
		long startTime = System.currentTimeMillis();
		try {
			this.session.getBasicRemote().sendObject(msg);
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Exception while send message to session: loveSessionType: "+liveSessionType+"  id: "+id+" sid: "+session.getId()+" createdTime: "+createdTime);
		}
		return (System.currentTimeMillis() - startTime);
	}
	
	public String toString() {
		return "LiveSession :: id: " + id + "orgId: "+((currentAccount != null && currentAccount .getOrg() != null) ? currentAccount.getOrg().getId() : -1)+"  sessionType: " + liveSessionType + " sessionSource: " + liveSessionSource + " sessionId: " + session.getId() + " createdTime: " + createdTime + " lastMsgTime: " + lastMsgTime;
	}
	
	public static enum LiveSessionType {
		APP,
		DEVICE,
		REMOTE_SCREEN,
		SERVICE_PORTAL,
		TENANT_PORTAL,
		VENDOR_PORTAL;
	}
	
	public static enum LiveSessionSource {
		WEB,
		MOBILE,
		TABLET;
	}
}