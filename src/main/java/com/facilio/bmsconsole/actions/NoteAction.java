package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;

public class NoteAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(NoteAction.class.getName());
	
	private long noteId;
	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}
	
	public String addTicketNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.TICKET_NOTES);
	}
	
	public String addSpaceNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.BASE_SPACE_NOTES);
	}
	
	public String addAssetNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.ASSET_NOTES);
	}
	
	public String addInventoryNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.INVENTORY_NOTES);
	}
	
	public String addNote() throws Exception {
		return addNote(module);
	}
	
	private String addNote(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.TICKET_MODULE, ticketModuleName);
		context.put(FacilioConstants.ContextNames.NOTE, note);
		Chain addNote = TransactionChainFactory.getAddNotesChain();
		addNote.execute(context);
		
		setNoteId(note.getId());
		
		return SUCCESS;
	} 
	
	private String module;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	private String ticketModuleName;
	public String getTicketModuleName() {
		return ticketModuleName;
	}
	public void setTicketModuleName(String ticketModuleName) {
		this.ticketModuleName = ticketModuleName;
	}

	private NoteContext note;
	public NoteContext getNote() {
		return note;
	}
	public void setNote(NoteContext note) {
		this.note = note;
	}
	
	private List<NoteContext> notes;
	public List<NoteContext> getNotes() {
		return notes;
	}
	public void setNotes(List<NoteContext> notes) {
		this.notes = notes;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getTicketNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.TICKET_NOTES);
	}
	
	public String getSpaceNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.BASE_SPACE_NOTES);
	}
	
	public String getAssetNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.ASSET_NOTES);
	}
	
	public String getInventoryNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.INVENTORY_NOTES);
	}
	public String getNotesList() throws Exception {
		return getNotesList(module);
	}
	
	private String getNotesList(String moduleName) throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, this.parentId);

		Chain getRelatedNoteChain = FacilioChainFactory.getNotesChain();
		getRelatedNoteChain.execute(context);

		setNotes((List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST));
		return SUCCESS;
	}
	
	/******************      V2 Api    ******************/

	public String v2noteList() throws Exception {
		getNotesList();
		setResult(FacilioConstants.ContextNames.NOTE_LIST, notes);
		return SUCCESS;
	}
	
	public String v2addNote() throws Exception {
		addNote();
		setResult(FacilioConstants.ContextNames.NOTE, noteId);
		return SUCCESS;
	}
 }

