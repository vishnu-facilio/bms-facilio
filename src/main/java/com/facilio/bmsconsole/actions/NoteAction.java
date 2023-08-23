package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
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
		return saveNote(FacilioConstants.ContextNames.TICKET_NOTES, false);
	}
	
	public String addSpaceNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.BASE_SPACE_NOTES, false);
	}
	
	public String addAssetNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.ASSET_NOTES, false);
	}
	
	public String addItemTypeNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.ITEM_TYPES_NOTES, false);
	}
	
	public String addToolTypeNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.TOOL_TYPES_NOTES, false);
	}
	
	public String addItemNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.ITEM_NOTES, false);
	}
	
	public String addToolNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.TOOL_NOTES, false);
	}
	
	public String addStoreRoomNote() throws Exception {
		return saveNote(FacilioConstants.ContextNames.STORE_ROOM_NOTES, false);
	}
	
	public String addNote() throws Exception {
		return saveNote(module, false);
	}

	public String deleteNote() throws Exception {
		FacilioContext context = new FacilioContext();
		
		String parentModuleName = this.parentModuleName;
		if (parentModuleName == null) {
			parentModuleName = ticketModuleName;
		}
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		//context.put(FacilioConstants.ContextNames.NOTE, note);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module);
		context.put(FacilioConstants.ContextNames.NOTE_ID, noteId);
        if (this.module.equals("cmdnotes")) {
			String customModuleNotes = CommonCommandUtil.getModuleTypeModuleName(parentModuleName, FacilioModule.ModuleType.NOTES);
			if (customModuleNotes != null) {
				context.put(FacilioConstants.ContextNames.MODULE_NAME, customModuleNotes);
			}
		}
		updateCurrentActivity(context, module);

		FacilioChain deleteNote = FacilioChainFactory.deleteNotesChain();
		deleteNote.execute(context);
		setResult("rowsUpdated", context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		return SUCCESS;
	}
	
	private String saveNote(String moduleName, Boolean isEdit) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		ModuleBaseWithCustomFields parent = new ModuleBaseWithCustomFields();
		parent.setId(note.getParentId());
		note.setParent(parent);

		String parentModuleName = this.parentModuleName;
		if (parentModuleName == null) {
			parentModuleName = ticketModuleName;
		}
		if (this.module.equals("cmdnotes")) {
			String customModuleNotes = CommonCommandUtil.getModuleTypeModuleName(parentModuleName, FacilioModule.ModuleType.NOTES);
			if (customModuleNotes != null) {
				context.put(FacilioConstants.ContextNames.MODULE_NAME, customModuleNotes);
			}
		}
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		context.put(FacilioConstants.ContextNames.NOTE, note);
		updateCurrentActivity(context, moduleName);
		FacilioChain saveNote;
		if (isEdit) {
			saveNote = TransactionChainFactory.getUpdateNotesChain();
		} else {
			saveNote = TransactionChainFactory.getAddNotesChain();
		}

		saveNote.execute(context);
		setResult("note", note);
		setResult("noteId", note.getId());
		setNoteId(note.getId());

		return SUCCESS;
	}


	private void updateCurrentActivity(FacilioContext context, String moduleName) throws Exception {
		if (moduleName.equals(FacilioConstants.ContextNames.ITEM_TYPES_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.STORE_ROOM_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.TOOL_TYPES_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.RECEIVABLE_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.CONTRACT_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ASSET_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.DELIVERY_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.BASE_ALARM_NOTES)) {
			context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE_ID, alarmOccurrenceId);
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ALARM_ACTIVITY);
		} else if (moduleName.equals(FacilioConstants.ContextNames.QUOTE_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.QUOTE_ACTIVITY);
		}  else if (moduleName.equals(FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_ACTIVITY);
		} else if (moduleName.equals(FacilioConstants.ContextNames.VENDOR_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.VENDOR_ACTIVITY);
		} else if (moduleName.equals(FacilioConstants.ContextNames.TENANT_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.TENANT_ACTIVITY);
		}  else if (moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE_NOTES)) {
			if (parentModuleName.equals(FacilioConstants.ContextNames.SITE)) {
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SITE_ACTIVITY);
			} else if (parentModuleName.equals(FacilioConstants.ContextNames.BUILDING)) {
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.BUILDING_ACTIVITY);
			} else if (parentModuleName.equals(FacilioConstants.ContextNames.FLOOR)) {
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.FLOOR_ACTIVITY);
			} else if (parentModuleName.equals(FacilioConstants.ContextNames.SPACE)) {
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SPACE_ACTIVITY);
			}
		} else if (moduleName.equals(FacilioConstants.ContextNames.SERVICE_REQUEST_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.PURCHASE_REQUEST_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.PURCHASE_REQUEST_ACTIVITY);
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.PURCHASE_ORDER_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.PURCHASE_ORDER_ACTIVITY);
		}
		else if (moduleName.equals(FacilioConstants.Meter.METER_NOTES)) {
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.Meter.METER_ACTIVITY);
		}
		else {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule parentModule = modBean.getModule(parentModuleName);
			if (parentModule.isCustom()) {
				context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
			}
		}
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

	public long parentNoteId = -1;

	public long getParentNoteId() {
		return parentNoteId;
	}

	public void setParentNoteId(long parentNoteId) {
		this.parentNoteId = parentNoteId;
	}

	public Boolean onlyFetchParentNotes = false;

	public Boolean getOnlyFetchParentNotes() {
		return onlyFetchParentNotes;
	}

	public void setOnlyFetchParentNotes(Boolean onlyFetchParentNotes) {
		this.onlyFetchParentNotes = onlyFetchParentNotes;
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
		context.put(FacilioConstants.ContextNames.PARENT_NOTE_ID,parentNoteId);
		context.put("onlyFetchParentNotes",onlyFetchParentNotes);

		if (this.module.equals("cmdnotes")) {
			String customModuleNotes = CommonCommandUtil.getModuleTypeModuleName(parentModuleName, FacilioModule.ModuleType.NOTES);
			if (customModuleNotes != null) {
				context.put(FacilioConstants.ContextNames.MODULE_NAME, customModuleNotes);
			}
		}
		context.put(FacilioConstants.ContextNames.PARENT_ID, this.parentId);

		FacilioChain getRelatedNoteChain = FacilioChainFactory.getNotesChain();
		getRelatedNoteChain.execute(context);
		if (notesIds != null && !notesIds.isEmpty()) {
			setNotesLists((Map<Long, List<NoteContext>>) context.get(FacilioConstants.ContextNames.NOTE_MAP));

		} else {
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

	public String v2updateNote() throws Exception {
		saveNote(module, true);
		setResult(FacilioConstants.ContextNames.NOTE, noteId);
		setResult("Notes", note);
		return SUCCESS;
	}

	public String v2SharingUpdate() throws Exception {

		FacilioContext context = new FacilioContext();

		String parentModuleName = this.parentModuleName;
		if (parentModuleName == null) {
			parentModuleName = ticketModuleName;
		}
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
		context.put(FacilioConstants.ContextNames.NOTE, note);

		context.put(FacilioConstants.ContextNames.MODULE_NAME, module);
		if (this.module.equals("cmdnotes")) {
			String customModuleNotes = CommonCommandUtil.getModuleTypeModuleName(parentModuleName, FacilioModule.ModuleType.NOTES);
			if (customModuleNotes != null) {
				context.put(FacilioConstants.ContextNames.MODULE_NAME, customModuleNotes);
			}
		}

		FacilioChain updateNotesSharingChain = TransactionChainFactory.updateNotesSharing();
		updateNotesSharingChain.execute(context);

		setResult(FacilioConstants.ContextNames.NOTE, note);

		return SUCCESS;
	}
 }

