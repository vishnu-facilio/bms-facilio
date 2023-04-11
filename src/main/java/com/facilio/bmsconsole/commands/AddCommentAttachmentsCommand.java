package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.CommentAttachmentContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddCommentAttachmentsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        NoteContext noteContext = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        if (notes == null && noteContext != null) {
            notes = new ArrayList<>();
            notes.add(noteContext);
        }
        String moduleName = (String)context.get("moduleName");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule notesModule = modBean.getModule(moduleName);
        if (notes != null && !notes.isEmpty()) {
            for (NoteContext note : notes) {
                addCommentAttachments(notesModule, note);
            }
        }
        return false;
    }

    private static void addCommentAttachments(FacilioModule notesModule, NoteContext note) throws Exception {
        List<CommentAttachmentContext> attachments = note.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            for (CommentAttachmentContext attachment : attachments) {
                attachment.setCommentModuleId(notesModule.getModuleId());
                attachment.setParent(note.getId());
                attachment.setCreatedTime(note.getCreatedTime());
            }
            String attachmentsModuleName = FacilioConstants.ContextNames.COMMENT_ATTACHMENTS;
            AttachmentsAPI.addCommentAttachments(attachments, attachmentsModuleName);
        }
    }
}
