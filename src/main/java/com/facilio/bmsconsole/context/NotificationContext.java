package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.util.NotificationTemplate;
import com.facilio.bmsconsole.workflow.ActivityType;

public class NotificationContext {

	private long id;
	private long orgId;
	private long userId;
	private ActivityType notificationType;
	private long actorId;
	private String info;
	private boolean isRead;
	private long readAt;
	private boolean isSeen;
	private long seenAt;
	private long createdTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public ActivityType getNotificationType() {
		return notificationType;
	}
	public int getNotificationTypeVal() {
		if (this.notificationType != null) {
			return notificationType.getValue();
		}
		return -1;
	}
	public void setNotificationTypeVal(int notificationTypeVal) {
		this.notificationType = ActivityType.valueOf(notificationTypeVal);
	}
	public void setNotificationType(ActivityType notificationType) {
		this.notificationType = notificationType;
	}
	public void setNotificationType(int notificationType) {
		this.notificationType = ActivityType.valueOf(notificationType);
	}
	public long getActorId() {
		return actorId;
	}
	public void setActorId(long actorId) {
		this.actorId = actorId;
	}
	public String getInfo() {
		return info;
	}
	public JSONObject getInfoJson() throws Exception {
		if (info != null) {
			return (JSONObject) new JSONParser().parse(info);
		}
		return null;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public long getReadAt() {
		return readAt;
	}
	public void setReadAt(long readAt) {
		this.readAt = readAt;
	}
	public boolean getIsSeen() {
		return isSeen;
	}
	public void setIsSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}
	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}
	public long getSeenAt() {
		return seenAt;
	}
	public void setSeenAt(long seenAt) {
		this.seenAt = seenAt;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public JSONObject getMessage() throws Exception {
		return NotificationTemplate.getFormattedMessage(this);
	}
}
