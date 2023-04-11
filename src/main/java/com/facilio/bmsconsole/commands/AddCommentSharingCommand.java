package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddCommentSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        if (notes == null && note != null) {
            notes = new ArrayList<>();
            notes.add(note);
        }

        String moduleName = (String)context.get("moduleName");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule notesModule = modBean.getModule(moduleName);

        if (notes != null && !notes.isEmpty()) {

            List<CommentSharingContext> addCommentSharingList = new ArrayList<>();
            for (NoteContext noteContext : notes) {
                if (noteContext.getCommentSharing() != null && noteContext.getCommentSharing().size() > 0) {
                    List<CommentSharingContext> commentSharingList = noteContext.getCommentSharing();
                    for (CommentSharingContext commentSharing : commentSharingList) {
                        commentSharing.setParentId(noteContext.getId());
                        commentSharing.setParentModuleId(notesModule.getModuleId());
                        commentSharing.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                        addCommentSharingList.add(commentSharing);
                    }
                }
            }
            if (addCommentSharingList.size() > 0) {
                FacilioModule module = ModuleFactory.getCommentsSharingModule();
                List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(module);
                InsertRecordBuilder<CommentSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentSharingContext>()
                        .module(module)
                        .fields(allFields);
                commentsSharingBuilder.addRecords(addCommentSharingList);
                commentsSharingBuilder.save();
            }
        }
        return false;
    }
}
