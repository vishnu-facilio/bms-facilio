package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.MarkDownUtil;

import java.util.List;


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
	
	private String bodyHTML;
	
	public String getBodyHTML() {
		if(body != null && bodyHTML == null)
		{
			bodyHTML=MarkDownUtil.getHTMLRender(body);
		}
		return bodyHTML;
	}
	
	private String bodyText;
	
	
	public String getBodyText() {
		if(body != null && bodyText == null)
		{
			bodyText=MarkDownUtil.getTextRender(body);
		}
		return bodyText;
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

	private ModuleBaseWithCustomFields parent;

	public ModuleBaseWithCustomFields getParent() {
		return parent;
	}
	public void setParent(ModuleBaseWithCustomFields parent) {
		this.parent = parent;
	}

	private List<CommentSharingContext> commentSharing;
	
	public List<CommentSharingContext> getCommentSharing() {
		return commentSharing;
	}

	public void setCommentSharing(List<CommentSharingContext> commentSharing) {
		this.commentSharing = commentSharing;
	}

	public boolean isEditAvailable() {
		return editAvailable;
	}

	public void setEditAvailable(boolean editAvailable) {
		this.editAvailable = editAvailable;
	}

	private boolean editAvailable = false;
}
