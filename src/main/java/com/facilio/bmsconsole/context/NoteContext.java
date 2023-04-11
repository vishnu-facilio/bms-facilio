package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.MarkDownUtil;

import java.util.ArrayList;
import java.util.Arrays;
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

	public void setBodyHTML(String bodyHTML) {
		this.bodyHTML = bodyHTML;
	}

	private String bodyHTML;
	private NoteContext parentNote;

	public NoteContext getParentNote() {
		return parentNote;
	}

	public void setParentNote(NoteContext parentNote) {
		this.parentNote = parentNote;
	}


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
	private long replyCount = -1;

	public long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(long replyCount) {
		this.replyCount = replyCount;
	}

	private ArrayList<NoteContext> replies;

	public ArrayList<NoteContext> getReplies() {
		return replies;
	}

	public void addReply(NoteContext reply){
		if(this.replies != null){
			this.replies.add(reply);
		}else {
			this.replies = new ArrayList<>(Arrays.asList(reply)) ;
		}
	}
	public void setReplies(ArrayList<NoteContext> replies) {
		this.replies = replies;
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

	public List<CommentMentionContext> getMentions() {
		return mentions;
	}

	public void setMentions(List<CommentMentionContext> mentions) {
		this.mentions = mentions;
	}

	private List<CommentMentionContext> mentions;
	private List<CommentAttachmentContext> attachments;

	public List<CommentAttachmentContext> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<CommentAttachmentContext> attachments) {
		this.attachments = attachments;
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
