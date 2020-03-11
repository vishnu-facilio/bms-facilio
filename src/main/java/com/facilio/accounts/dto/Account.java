package com.facilio.accounts.dto;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Stack;

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
	private int publicSelectQueries = 0;
	private int publicInsertQueries = 0;
	private int publicUpdateQueries = 0;
	private int publicDeleteQueries = 0;
	private int publicRedisQueries = 0;
	private int publicRedisGetCount = 0;
	private int publicRedisPutCount = 0;
	private int publicRedisDeleteCount = 0;
	private long publicSelectQueriesTime = 0L;
	private long publicInsertQueriesTime = 0L;
	private long publicUpdateQueriesTime = 0L;
	private long publicDeleteQueriesTime = 0L;
	private long publicRedisTime = 0L;
	private long publicRedisGetTime = 0L;
	private long publicRedisPutTime = 0L;
	private long publicRedisDeleteTime = 0L;
	private Deque<Boolean> publicAccess = new ArrayDeque<>();


	private RemoteScreenContext remoteScreen;

	@Override
	public Organization getOrg() {
		if (publicAccess.isEmpty()) {
			return this.org;
		}
		return null;
	}

	public void setOrg(Organization org) {
		this.org = org;
		if (org != null && org.getLoggerLevel() >= 0) {
			this.setLoggerLevel(org.getLoggerLevel());
		}
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
		if (publicAccess.isEmpty()) {
			return this.user;
		}
		return null;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getCurrentSiteId() {
		if (publicAccess.isEmpty()) {
			return this.siteId;
		}
		return -1;
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

	public int getPublicSelectQueries() {
		return publicSelectQueries;
	}

	public void incrementSelectQueryCount(int selectQueries) {
		if (publicAccess.isEmpty()) {
			this.selectQueries += selectQueries;
		}
		else {
			this.publicSelectQueries += selectQueries;
		}
	}

	public int getInsertQueries() {
		return insertQueries;
	}

	public int getPublicInsertQueries() {
		return publicInsertQueries;
	}

	public void incrementInsertQueryCount(int insertQueries) {
		if (publicAccess.isEmpty()) {
			this.insertQueries += insertQueries;
		}
		else {
			this.publicInsertQueries += insertQueries;
		}
	}

	public int getUpdateQueries() {
		return updateQueries;
	}

	public int getPublicUpdateQueries() {
		return publicUpdateQueries;
	}

	public void incrementUpdateQueryCount(int updateQueries) {
		if (publicAccess.isEmpty()) {
			this.updateQueries += updateQueries;
		}
		else {
			this.publicUpdateQueries += updateQueries;
		}
	}

	public int getDeleteQueries() {
		return deleteQueries;
	}

	public int getPublicDeleteQueries() {
		return publicDeleteQueries;
	}

	public void incrementDeleteQueryCount(int deleteQueries) {
		if (publicAccess.isEmpty()) {
			this.deleteQueries += deleteQueries;
		}
		else {
			this.publicDeleteQueries += deleteQueries;
		}
	}

	public int getRedisQueries() {
		return redisQueries;
	}

	public int getPublicRedisQueries() {
		return publicRedisQueries;
	}

	public void incrementRedisQueryCount(int redisQueries) {
		if (publicAccess.isEmpty()) {
			this.redisQueries += redisQueries;
		}
		else {
			this.publicRedisQueries += redisQueries;
		}
	}

	public long getSelectQueriesTime() {
		return selectQueriesTime;
	}

	public long getPublicSelectQueriesTime() {
		return publicSelectQueriesTime;
	}

	public void incrementSelectQueryTime(long selectQueriesTime) {
		if (publicAccess.isEmpty()) {
			this.selectQueriesTime += selectQueriesTime;
		}
		else {
			this.publicSelectQueriesTime += selectQueriesTime;
		}
	}

	public long getInsertQueriesTime() {
		return insertQueriesTime;
	}

	public long getPublicInsertQueriesTime() {
		return publicInsertQueriesTime;
	}

	public void incrementInsertQueryTime(long insertQueriesTime) {
		if (publicAccess.isEmpty()) {
			this.insertQueriesTime += insertQueriesTime;
		}
		else {
			this.publicInsertQueriesTime += insertQueriesTime;
		}
	}

	public long getUpdateQueriesTime() {
		return updateQueriesTime;
	}

	public long getPublicUpdateQueriesTime() {
		return publicUpdateQueriesTime;
	}

	public void incrementUpdateQueryTime(long updateQueriesTime) {
		if (publicAccess.isEmpty()) {
			this.updateQueriesTime += updateQueriesTime;
		}
		else {
			this.publicUpdateQueriesTime += updateQueriesTime;
		}
	}

	public long getDeleteQueriesTime() {
		return deleteQueriesTime;
	}

	public long getPublicDeleteQueriesTime() {
		return publicDeleteQueriesTime;
	}

	public void incrementDeleteQueryTime(long deleteQueriesTime) {
		if (publicAccess.isEmpty()) {
			this.deleteQueriesTime += deleteQueriesTime;
		}
		else {
			this.publicDeleteQueriesTime += deleteQueriesTime;
		}
	}

	public long getRedisTime() {
		return redisTime;
	}

	public long getPublicRedisTime() {
		return publicRedisTime;
	}

	public void incrementRedisTime(long redisTime) {
		if (publicAccess.isEmpty()) {
			this.redisTime += redisTime;
		}
		else {
			this.publicRedisTime += redisTime;
		}
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

	public int getPublicRedisGetCount() {
		return publicRedisGetCount;
	}

	public int getPublicRedisPutCount() {
		return publicRedisPutCount;
	}

	public int getPublicRedisDeleteCount() {
		return publicRedisDeleteCount;
	}

	public long getPublicRedisGetTime() {
		return publicRedisGetTime;
	}

	public long getPublicRedisPutTime() {
		return publicRedisPutTime;
	}

	public long getPublicRedisDeleteTime() {
		return publicRedisDeleteTime;
	}

	public void incrementRedisGetTime(long redisTime) {
		if (publicAccess.isEmpty()) {
			this.redisGetTime += redisTime;
		}
		else {
			this.publicRedisGetTime += redisTime;
		}
	}

	public void incrementRedisPutTime(long redisTime) {
		if (publicAccess.isEmpty()) {
			this.redisPutTime += redisTime;
		}
		else {
			this.publicRedisPutTime += redisTime;
		}
	}

	public void incrementRedisDeleteTime(long redisTime) {
		if (publicAccess.isEmpty()) {
			this.redisDeleteTime += redisTime;
		}
		else {
			this.publicRedisDeleteTime += redisTime;
		}
	}

	public void incrementRedisGetCount(int redisQueries) {
		if (publicAccess.isEmpty()) {
			this.redisGetCount += redisQueries;
		}
		else {
			this.publicRedisGetCount += redisQueries;
		}
	}

	public void incrementRedisPutCount(int redisQueries) {
		if (publicAccess.isEmpty()) {
			this.redisPutCount += redisQueries;
		}
		else {
			this.publicRedisPutCount += redisQueries;
		}
	}

	public void incrementRedisDeleteCount(int redisQueries) {
		if (publicAccess.isEmpty()) {
			this.redisDeleteCount += redisQueries;
		}
		else {
			this.publicRedisDeleteCount += redisQueries;
		}
	}

	public long getTotalQueries() {
		return getSelectQueries() + getDeleteQueries() + getInsertQueries() + getUpdateQueries() +
				getPublicSelectQueries() + getPublicDeleteQueries() + getPublicInsertQueries() + getPublicUpdateQueries()
				;
	}

	public long getTotalQueryTime() {
		return getSelectQueriesTime() + getDeleteQueriesTime() + getInsertQueriesTime() + getUpdateQueriesTime() +
				getPublicSelectQueriesTime() + getPublicDeleteQueriesTime() + getPublicInsertQueriesTime() + getPublicUpdateQueriesTime()
				;
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

	public void clearStateVariables() {
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

		publicSelectQueries = 0;
		publicInsertQueries = 0;
		publicUpdateQueries = 0;
		publicDeleteQueries = 0;
		publicRedisQueries = 0;
		publicRedisGetCount = 0;
		publicRedisPutCount = 0;
		publicRedisDeleteCount = 0;
		publicSelectQueriesTime = 0L;
		publicInsertQueriesTime = 0L;
		publicUpdateQueriesTime = 0L;
		publicDeleteQueriesTime = 0L;
		publicRedisTime = 0L;
		publicRedisGetTime = 0L;
		publicRedisPutTime = 0L;
		publicRedisDeleteTime = 0L;

		if (redisLocalCache != null) {//Better for gc I guess
			redisLocalCache.clear();
		}
	}

	private HashMap<String, Long> redisLocalCache;
	public Long getFromRedisLocalCache (String key) {
		return redisLocalCache == null ? null : redisLocalCache.get(key);
	}
	public void addToRedisLocalCache  (String key, Long value) {
		if (redisLocalCache == null) {
			redisLocalCache = new HashMap<>();
		}
		redisLocalCache.put(key, value);
	}
	public Long removeFromRedisLocalCache (String key) {
		if (redisLocalCache != null) {
			return redisLocalCache.remove(key);
		}
		return null;
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

	@Override
	public void addPublicAccess() {
		publicAccess.push(Boolean.TRUE);
	}

	@Override
	public void removePublicAccess() {
		publicAccess.pop();
	}

	public String getCurrentDataSource() {
		if (getOrg() != null && publicAccess.isEmpty()) {
			return getOrg().getDataSource();
		}
		return null;
	}

	public String getCurrentDBName() {
		if (getOrg() != null && publicAccess.isEmpty()) {
			return getOrg().getDbName();
		}
		return null;
	}

	public void setUserSessionId(long userSessionId) {
		this.userSessionId = userSessionId;
	}
}

