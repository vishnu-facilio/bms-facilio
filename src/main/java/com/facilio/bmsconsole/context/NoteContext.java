package com.facilio.bmsconsole.context;

public class NoteContext {
	
	private long noteId;
	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private Long ownerId;
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	private Long creationTime;
	public Long getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
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
	
	private String parentModuleLinkName;
	public String getParentModuleLinkName() {
		return parentModuleLinkName;
	}
	public void setParentModuleLinkName(String parentModuleLinkName) {
		this.parentModuleLinkName = parentModuleLinkName;
	}
	
	public boolean notifyRequester = false;
	public boolean getNotifyRequester() {
		return notifyRequester;
	}
	public void setNotifyRequester(boolean notifyRequester) {
		this.notifyRequester = notifyRequester;
	}
}
