package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class NoteContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User createdBy;
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	private String body;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private boolean notifyRequester = false;
	public boolean getNotifyRequester() {
		return notifyRequester;
	}
	public void setNotifyRequester(boolean notifyRequester) {
		this.notifyRequester = notifyRequester;
	}
}
