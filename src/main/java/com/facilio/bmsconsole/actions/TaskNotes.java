package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.opensymphony.xwork2.ActionSupport;

public class TaskNotes extends ActionSupport {
	
	public String addNote() throws Exception {
		
		if(note != null) {
			note.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			note.setOwnerId(UserInfo.getCurrentUser().getOrgUserId());
			note.setCreationTime(System.currentTimeMillis());
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.TASK_ID, getTaskId());
			context.put(FacilioConstants.ContextNames.NOTE, note);
			
			Chain addTaskNote = FacilioChainFactory.getAddTaskNoteChain();
			addTaskNote.execute(context);
		}
		return SUCCESS;
	}
	
	private NoteContext note;
	public NoteContext getNote() {
		return note;
	}
	public void setNote(NoteContext note) {
		this.note = note;
	}
	
	private long taskId;
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
}
