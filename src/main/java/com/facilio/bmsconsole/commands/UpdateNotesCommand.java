package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UpdateNotesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if (notes == null && note != null) {
            notes = new ArrayList<>();
            notes.add(note);
        }
        if (notes != null && !notes.isEmpty()) {
            for (NoteContext noteContext : notes) {
                UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<NoteContext>()
                        .module(module)
                        .fields(fields)
                        .andCondition(CriteriaAPI.getIdCondition(noteContext.getId(), module));
                updateRecordBuilder.update(noteContext);
            }
        }

        return false;
    }
}
