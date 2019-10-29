package com.facilio.accounts.dto;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.struts2.ServletActionContext;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ConnectedDeviceContext;
import com.facilio.screen.context.RemoteScreenContext;
import com.opensymphony.xwork2.ActionContext;

public class Account implements AccountsInterface<User>, Serializable{
	
	/**
	 * 
	 * 
	 */
	private Organization org;
	private static final long serialVersionUID = 1L;
	private User user;
	private long siteId = -1; 
	
	private String deviceType;
	private String appVersion;
	private int selectQueries = 0;
	private int insertQueries = 0;
	private int updateQueries = 0;
	private int deleteQueries = 0;
	private int redisQueries = 0;
	private int redisGetCount = 0;
	private int redisPutCount = 0;
	private int redisDeleteCount = 0;
	private long selectQueriesTime = 0L;
	private long insertQueriesTime = 0L;
	private long updateQueriesTime = 0L;
	private long deleteQueriesTime = 0L;
	private long redisTime = 0L;
	private long redisGetTime = 0L;
	private long redisPutTime = 0L;
	private long redisDeleteTime = 0L;
	private long userSessionId = 0L;
	

	private RemoteScreenContext remoteScreen;
	
	@Override
	public Organization getOrg() {
		return this.org;
	}
	
	public void setOrg(Organization org) {
		this.org = org;
	}
	
	public Account(Organization org, User user) {
		setOrg(org);
		setUser(user);
		if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
			HttpServletRequest request = ServletActionContext.getRequest();
			setDeviceType(request.getHeader("X-Device-Type"));
			setAppVersion(request.getHeader("X-App-Version"));
		}
		if (org != null) {
			org.setBrand(FacilioProperties.getConfig("rebrand.brand"));
		}
	}

	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public long getCurrentSiteId() {
		return this.siteId;
	}
	
	public void setCurrentSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	public Boolean isFromMobile() {
		return isFromAndroid() || isFromIos();
	}

	public Boolean isFromIos() {
		return deviceType != null && deviceType.equalsIgnoreCase("ios");
	}

	public Boolean isFromAndroid() {
		return deviceType != null && deviceType.equalsIgnoreCase("android");
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public RemoteScreenContext getRemoteScreen() {
		return this.remoteScreen;
	}

	private ConnectedDeviceContext connectedDevice;
	public ConnectedDeviceContext getConnectedDevice() {
		return connectedDevice;
	}

	public void setConnectedDevice(ConnectedDeviceContext connectedDevice) {
		this.connectedDevice = connectedDevice;
	}

	public void setRemoteScreen(RemoteScreenContext remoteScreen) {
		this.remoteScreen = remoteScreen;
	}

	public int getSelectQueries() {
		return selectQueries;
	}

	public void incrementSelectQueryCount(int selectQueries) {
		this.selectQueries = this.selectQueries + selectQueries;
	}

	public int getInsertQueries() {
		return insertQueries;
	}

	public void incrementInsertQueryCount(int insertQueries) {
		this.insertQueries = this.insertQueries + insertQueries;
	}

	public int getUpdateQueries() {
		return updateQueries;
	}

	public void incrementUpdateQueryCount(int updateQueries) {
		this.updateQueries = this.updateQueries + updateQueries;
	}

	public int getDeleteQueries() {
		return deleteQueries;
	}

	public void incrementDeleteQueryCount(int deleteQueries) {
		this.deleteQueries = this.deleteQueries + deleteQueries;
	}

	public int getRedisQueries() {
		return redisQueries;
	}

	public void incrementRedisQueryCount(int redisQueries) {
		this.redisQueries = this.redisQueries + redisQueries;
	}

	public long getSelectQueriesTime() {
		return selectQueriesTime;
	}

	public void incrementSelectQueryTime(long selectQueriesTime) {
		this.selectQueriesTime = this.selectQueriesTime + selectQueriesTime;
	}

	public long getInsertQueriesTime() {
		return insertQueriesTime;
	}

	public void incrementInsertQueryTime(long insertQueriesTime) {
		this.insertQueriesTime = this.insertQueriesTime + insertQueriesTime;
	}

	public long getUpdateQueriesTime() {
		return updateQueriesTime;
	}

	public void incrementUpdateQueryTime(long updateQueriesTime) {
		this.updateQueriesTime = this.updateQueriesTime + updateQueriesTime;
	}

	public long getDeleteQueriesTime() {
		return deleteQueriesTime;
	}

	public void incrementDeleteQueryTime(long deleteQueriesTime) {
		this.deleteQueriesTime = this.deleteQueriesTime + deleteQueriesTime;
	}

	public long getRedisTime() {
		return redisTime;
	}

	public void incrementRedisTime(long redisTime) {
		this.redisTime = this.redisTime + redisTime;
	}

	public int getRedisGetCount() {
		return redisGetCount;
	}

	public int getRedisPutCount() {
		return redisPutCount;
	}

	public int getRedisDeleteCount() {
		return redisDeleteCount;
	}

	public long getRedisGetTime() {
		return redisGetTime;
	}

	public long getRedisPutTime() {
		return redisPutTime;
	}

	public long getRedisDeleteTime() {
		return redisDeleteTime;
	}

	public void incrementRedisGetTime(long redisTime) {
		this.redisGetTime = this.redisGetTime + redisTime;
	}

	public void incrementRedisPutTime(long redisTime) {
		this.redisPutTime = this.redisPutTime + redisTime;
	}

	public void incrementRedisDeleteTime(long redisTime) {
		this.redisDeleteTime = this.redisDeleteTime + redisTime;
	}

	public void incrementRedisGetCount(int redisQueries) {
		this.redisGetCount = this.redisGetCount + redisQueries;
	}

	public void incrementRedisPutCount(int redisQueries) {
		this.redisPutCount = this.redisPutCount + redisQueries;
	}

	public void incrementRedisDeleteCount(int redisQueries) {
		this.redisDeleteCount = this.redisDeleteCount + redisQueries;
	}

	public long getTotalQueries() {
		return getSelectQueries() + getDeleteQueries()+ getInsertQueries()+ getUpdateQueries();
	}

	public long getTotalQueryTime() {
		return getSelectQueriesTime() + getDeleteQueriesTime()+ getInsertQueriesTime()+ getUpdateQueriesTime();
	}

	private String requestUri;
    public void setRequestUri(String requestUri) {
    	this.requestUri = requestUri;
    }

	public String getRequestUri() {
    	return requestUri;
	}

	private String requestParams;
	public String getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	private String timeZone;
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;	
	}
	public String getTimeZone() {
		if(StringUtils.isEmpty(timeZone) && getOrg() != null) {
			return getOrg().getTimezone();
		}
    	return timeZone;
	}

	public void clearCounters() {
		selectQueries = 0;
		insertQueries = 0;
		updateQueries = 0;
		deleteQueries = 0;
		redisQueries = 0;
		redisGetCount = 0;
		redisPutCount = 0;
		redisDeleteCount = 0;
		selectQueriesTime = 0L;
		insertQueriesTime = 0L;
		updateQueriesTime = 0L;
		deleteQueriesTime = 0L;
		redisTime = 0L;
		redisGetTime = 0L;
		redisPutTime = 0L;
		redisDeleteTime = 0L;
	}

	private Level level = null;
    public Level getLevel() {
    	if (level == null) {
    		return Level.INFO;
		}
		return level;
    }
	public void setLevel(Level level) {
    	if (this.level == null || level.toInt() < this.level.toInt()) { //Setting only if incoming level is greater. Useful when we enable logger level org/ user wise and all
			this.level = level;
		}
	}
	public void setLoggerLevel(int loggerLevel) {
		Level level = null;
		switch (loggerLevel) {
			case 0:
				level = Level.ALL;
				break;
			case 1:
				level = Level.TRACE;
				break;
			case 2:
				level = Level.DEBUG;
				break;
			case 3:
				level = Level.INFO;
				break;
			case 4:
				level = Level.WARN;
				break;
			case 5:
				level = Level.ERROR;
				break;
			case 6:
				level = Level.FATAL;
				break;
			case 7:
				level = Level.OFF;
				break;
			default:
				break;
		}
		if (level != null) {
			setLevel(level);
		}
	}

	@Override
	public Long getUserSessionId() {
		// TODO Auto-generated method stub
		return this.userSessionId;
	}
	
	public void setUserSessionId(long userSessionId) {
		this.userSessionId = userSessionId;
	}
}

