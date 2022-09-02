package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentsSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.PortalCommentsSharingContext;
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

import static com.facilio.accounts.dto.AppDomain.AppDomainType.FACILIO;

public class CommentsSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        String moduleName = (String)context.get("moduleName");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule notesModule = modBean.getModule(moduleName);
        if(AccountUtil.getCurrentApp().getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS)) {
            if (notes != null && !notes.isEmpty()) {
                for (NoteContext note : notes) {
                    CommentsSharingContext commentSharing = new CommentsSharingContext();
                    commentSharing.setAppId(AccountUtil.getCurrentApp().getId());
                    commentSharing.setParentId(note.getId());
                    commentSharing.setParentModuleId(notesModule.getModuleId());
                    commentSharing.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    FacilioModule module = ModuleFactory.getCommentsSharingModule();
                    List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(module);
                    InsertRecordBuilder<CommentsSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentsSharingContext>()
                            .module(module)
                            .fields(allFields);
                    commentsSharingBuilder.addRecord(commentSharing);
                    commentsSharingBuilder.save();

                }
            }
        }
        else
        {
            for (NoteContext note : notes) {
                List<PortalCommentsSharingContext> portalSharingList =note.getSelectedPortalApp();
            if(portalSharingList != null && !portalSharingList.isEmpty() && note.getSharingPublic())
            {
                List<CommentsSharingContext> commentSharingList = new ArrayList<>();
                for(PortalCommentsSharingContext portalnoteSharing : portalSharingList)
                {
                    if(portalnoteSharing.getAppSelected()==true) {
                        CommentsSharingContext commentSharing = new CommentsSharingContext();
                        commentSharing.setAppId(portalnoteSharing.getAppId());
                        commentSharing.setParentId(note.getId());
                        commentSharing.setParentModuleId(notesModule.getModuleId());
                        commentSharing.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                        commentSharingList.add(commentSharing);
                    }
                }
                if(commentSharingList.size()>0) {
                    FacilioModule module = ModuleFactory.getCommentsSharingModule();
                    List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(module);
                    InsertRecordBuilder<CommentsSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentsSharingContext>()
                            .module(module)
                            .fields(allFields);
                    commentsSharingBuilder.addRecords(commentSharingList);
                    commentsSharingBuilder.save();
                }
            }
            }
        }
        return false;
    }
}
