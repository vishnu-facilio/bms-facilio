package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.RecordSummaryLayout;
import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class NoteAction extends ActionSupport {
	
	//New Task Props
	public String newNote() throws Exception {
		
		return SUCCESS;
	}
	
	private long noteId;
	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}
	
	public String getModuleName() {
		return "Note";
	}
	
	private ActionForm actionForm;
	public ActionForm getActionForm() {
		return actionForm;
	}
	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}
	
	public String addNote() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.NOTE, note);

		Chain addNote = FacilioChainFactory.getAddNoteChain();
		addNote.execute(context);
		
		setNoteId(note.getNoteId());
		
		return SUCCESS;
	}

	private NoteContext note;
	public NoteContext getNote() {
		return note;
	}
	public void setNote(NoteContext note) {
		this.note = note;
	}
	
	public List getFormlayout()
	{
		return FormLayout.getNewNoteLayout();
	}
	
	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.NOTE;
	}
 }

