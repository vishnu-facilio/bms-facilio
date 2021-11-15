package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeleteNoteCommand extends FacilioCommand implements PostTransactionCommand {
	
	private Set<Long> idsToUpdateCount;
	private String parentModuleName;
	private String moduleName;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		Long noteId = (Long) context.get(FacilioConstants.ContextNames.NOTE_ID);
		moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		parentModuleName =  (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
		if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
			if (parentModuleName == null || parentModuleName.isEmpty()) {
				throw new IllegalArgumentException("Module name for ticket notes should be specified");
			}
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module =  modBean.getModule(moduleName);
		
		if (noteId != null && noteId > 0) {
			
			if (note != null) {
				Set<Long> parentIds = new HashSet<>();
				parentIds.add(note.getParentId());
				idsToUpdateCount = parentIds;
			}
			
			List<NoteContext> noteListContext = NotesAPI.fetchNotes(noteId, moduleName);
			if (noteListContext != null && noteListContext.size() > 0) {
				if (noteListContext.get(0).getCreatedBy() != null && noteListContext.get(0).getCreatedBy().getId() != AccountUtil.getCurrentUser().getId()) {
					throw new IllegalArgumentException("No access to delete this comment");
				}
			}
			
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(noteId, module));

			JSONObject info = new JSONObject();
			info.put(FacilioConstants.ContextNames.NOTES_COMMENT, note.getBody());
			info.put("deletedBy", AccountUtil.getCurrentUser().getOuid());

			CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.DELETE_COMMENT, info, (FacilioContext) context);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
			
		}
		return false;
	}

	public boolean postExecute() throws Exception {
		NotesAPI.updateNotesCount(idsToUpdateCount, parentModuleName, moduleName);
		return false;
	}
	
}
