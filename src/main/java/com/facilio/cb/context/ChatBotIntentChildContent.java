package com.facilio.cb.context;

public class ChatBotIntentChildContent {
	
	long id;
	long orgId;
	long parentId;
	long childId;
	ChatBotIntent parentIntent;
	ChatBotIntent childIntent;
	
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getChildId() {
		return childId;
	}
	public void setChildId(long childId) {
		this.childId = childId;
	}
	public ChatBotIntent getParentIntent() {
		return parentIntent;
	}
	public void setParentIntent(ChatBotIntent parentIntent) {
		this.parentIntent = parentIntent;
	}
	public ChatBotIntent getChildIntent() {
		return childIntent;
	}
	public void setChildIntent(ChatBotIntent childIntent) {
		this.childIntent = childIntent;
	}

}
