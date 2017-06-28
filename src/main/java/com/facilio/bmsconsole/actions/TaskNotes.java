package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.AddTaskNoteCommand;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.opensymphony.xwork2.ActionSupport;

public class TaskNotes extends ActionSupport {
	
	public String addNote() throws Exception {
		
		System.out.println(body);
		
		NoteContext context = new NoteContext();
		
		context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.setOwnerId(UserInfo.getCurrentUser().getOrgUserId());
		context.setCreationTime(System.currentTimeMillis()/1000);
		context.setTitle(getTitle());
		context.setBody(getBody());
		context.put(AddTaskNoteCommand.TASK_ID, getTaskId());
		
		Chain addTaskNote = FacilioChainFactory.getAddTaskNoteChain();
		addTaskNote.execute(context);
		setNoteId(context.getNoteId());
		
		return SUCCESS;
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
	
	private long noteId;
	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
}
