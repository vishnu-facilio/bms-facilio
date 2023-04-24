package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
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
                    commentMentions.setMentionedModuleId(mentionedMod.getModuleId());
                }
                commentMentions.setParentId(noteContext.getId());
                commentMentions.setParentModuleId(notesModule.getModuleId());
                commentMentions.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                if(commentMentions.getMentionTypeEnum() == CommentMentionContext.MentionType.PEOPLE){
                    setPeopleRecordForMention(commentMentions);
                }
                addCommentMentionsList.add(commentMentions);
            }
        }
    }

    private static void setPeopleRecordForMention(CommentMentionContext commentMentions) throws Exception {
        if(commentMentions.getMentionedRecordId() > 0){
            long pplId = commentMentions.getMentionedRecordId();
            V3PeopleContext pplRecord = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, pplId, V3PeopleContext.class);
            commentMentions.setRecordObj(pplRecord);
        }
    }
}
