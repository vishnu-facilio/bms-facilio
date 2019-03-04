package com.facilio.accounts.dto;

import java.io.Serializable;

import com.facilio.screen.context.RemoteScreenContext;

public class Account implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Organization org;
	private User user;
	private long siteId = -1; 
	
	private String deviceType;
	private String appVersion;
	private int selectQueries = 0;
	private int insertQueries = 0;
	private int updateQueries = 0;
	private int deleteQueries = 0;
	private int redisQueries = 0;
	private long selectQueriesTime = 0L;
	private long insertQueriesTime = 0L;
	private long updateQueriesTime = 0L;
	private long deleteQueriesTime = 0L;
	private long redisTime = 0L;
	
	private RemoteScreenContext remoteScreen;
	
	public Account(Organization org, User user) {
		this.org = org;
		this.user = user;
	}
	
	public Account(Organization org, User user, long siteId) {
		this.org = org;
		this.user = user;
		this.siteId = siteId;
	}
	
	public Organization getOrg() {
		return this.org;
	}
	
	public void setOrg(Organization org) {
		this.org = org;
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
}
