package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentMentionContext;
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

public class AddMentionsCommand extends FacilioCommand {
    List<CommentMentionContext> addCommentMentionsList = new ArrayList<>();
    @Override
    public boolean executeCommand(Context context) throws Exception {
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
            for (NoteContext noteContext : notes) {
                setupMentionData(modBean, notesModule, noteContext);
            }
            saveMentions();
        }
        return false;
    }

    private void saveMentions() throws Exception {
        if (addCommentMentionsList.size() > 0) {
            FacilioModule module = ModuleFactory.getCommentMentionsModule();
            List<FacilioField> allFields = FieldFactory.getCommentMentionsFields(module);
            InsertRecordBuilder<CommentMentionContext> commentsMentionsBuilder = new InsertRecordBuilder<CommentMentionContext>()
                    .module(module)
                    .fields(allFields);
            commentsMentionsBuilder.addRecords(addCommentMentionsList);
            commentsMentionsBuilder.save();
        }
    }

    private void setupMentionData(ModuleBean modBean, FacilioModule notesModule, NoteContext noteContext) throws Exception {
        if (noteContext.getMentions() != null && noteContext.getMentions().size() > 0) {
            List<CommentMentionContext> commentMentionsList = noteContext.getMentions();
            for (CommentMentionContext commentMentions : commentMentionsList) {
                if (commentMentions.getMentionedModuleName() != null) {
                    String mentionedModuleName = commentMentions.getMentionedModuleName();
                    FacilioModule mentionedMod = modBean.getModule(mentionedModuleName);
                    commentMentions.setMentionedModuleID(mentionedMod.getModuleId());
                }
                commentMentions.setParentID(noteContext.getId());
                commentMentions.setParentModuleID(notesModule.getModuleId());
                commentMentions.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                addCommentMentionsList.add(commentMentions);
            }
        }
    }
}
