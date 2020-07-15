package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

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
	
	public String addItemTypeNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.ITEM_TYPES_NOTES);
	}
	
	public String addToolTypeNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.TOOL_TYPES_NOTES);
	}
	
	public String addItemNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.ITEM_NOTES);
	}
	
	public String addToolNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.TOOL_NOTES);
	}
	
	public String addStoreRoomNote() throws Exception {
		return addNote(FacilioConstants.ContextNames.STORE_ROOM_NOTES);
	}
	
	public String addNote() throws Exception {
		return addNote(module);
	}

	public String deleteNote() throws Exception {
		FacilioContext context = new FacilioContext();
		
		String parentModuleName = this.parentModuleName;
		if (parentModuleName == null) {
			parentModuleName = ticketModuleName;
		}
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		context.put(FacilioConstants.ContextNames.NOTE, note);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module);
		context.put(FacilioConstants.ContextNames.NOTE_ID, noteId);
		FacilioChain deleteNote = FacilioChainFactory.deleteNotesChain();
		deleteNote.execute(context);
		setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;
	}
	
	private String addNote(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		String parentModuleName = this.parentModuleName;
		if (parentModuleName == null) {
			parentModuleName = ticketModuleName;
		}
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		context.put(FacilioConstants.ContextNames.NOTE, note);
		if (moduleName.equals(FacilioConstants.ContextNames.ITEM_TYPES_NOTES)) {
		   context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
			}

		else if (moduleName.equals(FacilioConstants.ContextNames.ASSET_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
			}
		else if (moduleName.equals(FacilioConstants.ContextNames.BASE_ALARM_NOTES)) {
			context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE_ID, alarmOccurrenceId);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ALARM_ACTIVITY);
		} else if (moduleName.equals(FacilioConstants.ContextNames.QUOTE_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.QUOTE_ACTIVITY);
		}  else if (moduleName.equals(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY);
		}
		FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
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
	
	private String parentModuleName;
	public String getParentModuleName() {
		return parentModuleName;
	}
	public void setParentModuleName(String parentModuleName) {
		this.parentModuleName = parentModuleName;
	}

	private NoteContext note;
	public NoteContext getNote() {
		return note;
	}
	public void setNote(NoteContext note) {
		this.note = note;
	}
	
	private Map<Long, List<NoteContext>> notesLists ;
	


	public Map<Long, List<NoteContext>> getNotesLists() {
		return notesLists;
	}

	public void setNotesLists(Map<Long, List<NoteContext>> notesLists) {
		this.notesLists = notesLists;
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

	public long getAlarmOccurrenceId() {
		return alarmOccurrenceId;
	}

	public void setAlarmOccurrenceId(long alarmOccurrenceId) {
		this.alarmOccurrenceId = alarmOccurrenceId;
	}

	private long alarmOccurrenceId = -1;
	public String getSpaceNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.BASE_SPACE_NOTES);
	}
	
	public String getAssetNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.ASSET_NOTES);
	}
	
	public String getItemTypeNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.ITEM_TYPES_NOTES);
	}
	
	public String getToolTypeNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.TOOL_TYPES_NOTES);
	}
	
	public String getItemNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.ITEM_NOTES);
	}
	
	public String getToolNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.TOOL_NOTES);
	}
	
	public String getStoreRoomNotes() throws Exception {
		return getNotesList(FacilioConstants.ContextNames.STORE_ROOM_NOTES);
	}

	private List<Long> notesIds;

	public List<Long> getNotesIds() {
		return notesIds;
	}

	public void setNotesIds(List<Long> notesIds) {
		this.notesIds = notesIds;
	}

	public String getNotesList() throws Exception {
		return getNotesList(module);
	}
	
	public String v2getNotesList() throws Exception {
		getNotesList();
		setResult("notes", notes);
		
		return SUCCESS;
	}
	
	private String getNotesList(String moduleName) throws Exception {
		
		FacilioContext context = new FacilioContext();
		if (notesIds != null && !notesIds.isEmpty()) {			
			context.put(FacilioConstants.ContextNames.NOTE_IDS, notesIds);
			}
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.PARENT_ID, this.parentId);

		FacilioChain getRelatedNoteChain = FacilioChainFactory.getNotesChain();
		getRelatedNoteChain.execute(context);
		if (notesIds != null && !notesIds.isEmpty()) {
			setNotesLists((Map<Long, List<NoteContext>>) context.get(FacilioConstants.ContextNames.NOTE_LIST));
			
		}
		else {
			setNotes((List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST));
		}
		
		return SUCCESS;
	}
	
	/******************      V2 Api    ******************/

	public String v2noteList() throws Exception {
		getNotesList();
		setResult("notesList", notesLists);
		setResult(FacilioConstants.ContextNames.NOTE_LIST, notes);
		
		return SUCCESS;
	}
	
	public String v2addNote() throws Exception {
		addNote();
		setResult(FacilioConstants.ContextNames.NOTE, noteId);
		setResult("Notes", note);
		return SUCCESS;
	}
 }

