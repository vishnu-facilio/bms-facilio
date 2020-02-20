package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class DeleteNoteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		Long noteId = (Long) context.get(FacilioConstants.ContextNames.NOTE_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module =  modBean.getModule(moduleName);
		
		if (noteId != null && noteId > 0) {
			
			List<NoteContext> noteListContext = NotesAPI.fetchNotes(noteId, moduleName);
			if (noteListContext != null && noteListContext.size() > 0) {
				if (noteListContext.get(0).getCreatedBy() != null && noteListContext.get(0).getCreatedBy().getId() != AccountUtil.getCurrentUser().getId()) {
					throw new IllegalArgumentException("No access to delete this comment");
				}
			}
			
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(noteId, module));

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
			
		}
		return false;
	}

	
}
