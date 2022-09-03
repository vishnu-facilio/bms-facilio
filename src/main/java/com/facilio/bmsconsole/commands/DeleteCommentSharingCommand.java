package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DeleteCommentSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (note != null && note.getId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(moduleName);
            FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();

            List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
            Map<String, FacilioField> sharingFieldMap = FieldFactory.getAsMap(allFields);

            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(commentsSharingModule.getTableName())
                    .andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentId"), String.valueOf(note.getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(sharingFieldMap.get("parentModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            builder.delete();
        }
        return false;
    }
}
