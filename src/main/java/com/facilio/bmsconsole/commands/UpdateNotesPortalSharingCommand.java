package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.audit.AuditData;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentsSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.PortalCommentsSharingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateNotesPortalSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String parentModuleName =  (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule notesModule = modBean.getModule(moduleName);
        List<PortalCommentsSharingContext> commentSharing = note.getSelectedPortalApp();
        FacilioModule module = ModuleFactory.getCommentsSharingModule();
        List<FacilioField> allFields = FieldFactory.getCommentsSharingFields(module);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(allFields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), Collections.singleton(note.getId()), NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();
        List<CommentsSharingContext> oldCommentsSharing = new ArrayList<>();
        List<PortalCommentsSharingContext> newCommentSharing =note.getSelectedPortalApp();
        List<Long> deletingIds = new ArrayList<>();
        List<CommentsSharingContext> addSharingPortal = new ArrayList<>();
        if (props != null && !props.isEmpty()) {
            oldCommentsSharing = FieldUtil.getAsBeanListFromMapList(props, CommentsSharingContext.class);
        }
        if(oldCommentsSharing!=null && !oldCommentsSharing.isEmpty())
        {
            for(PortalCommentsSharingContext portalSharing : newCommentSharing)
            {
                Boolean hasNewApp = true;
                for(CommentsSharingContext oldSharing  :  oldCommentsSharing)
                {
                    if(oldSharing.getAppId() == portalSharing.getAppId() && !portalSharing.getAppSelected())
                    {
                        deletingIds.add(oldSharing.getId());
                    }
                    if(oldSharing.getAppId() == portalSharing.getAppId())
                    {
                        hasNewApp=false;
                    }
                }
                if(hasNewApp && portalSharing.getAppSelected())
                {
                    CommentsSharingContext commentsSharingContext = new CommentsSharingContext();
                    commentsSharingContext.setAppId(portalSharing.getAppId());
                    commentsSharingContext.setParentId(note.getId());
                    commentsSharingContext.setParentModuleId(notesModule.getModuleId());
                    commentsSharingContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    addSharingPortal.add(commentsSharingContext);
                }
            }
            if(addSharingPortal.size()>0) {
                InsertRecordBuilder<CommentsSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentsSharingContext>()
                        .module(module)
                        .fields(allFields);
                commentsSharingBuilder.addRecords(addSharingPortal);
                commentsSharingBuilder.save();
            }

            if(deletingIds.size()>0) {

                GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), deletingIds, NumberOperators.EQUALS));
                builder.delete();

            }
        }
        else
        {
            for(PortalCommentsSharingContext portalSharing : newCommentSharing) {
                if(portalSharing.getAppSelected())
                {
                    CommentsSharingContext commentsSharingContext = new CommentsSharingContext();
                    commentsSharingContext.setAppId(portalSharing.getAppId());
                    commentsSharingContext.setParentId(note.getId());
                    commentsSharingContext.setParentModuleId(notesModule.getModuleId());
                    commentsSharingContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    addSharingPortal.add(commentsSharingContext);
                }
            }
            InsertRecordBuilder<CommentsSharingContext> commentsSharingBuilder = new InsertRecordBuilder<CommentsSharingContext>()
                    .module(module)
                    .fields(allFields);
            commentsSharingBuilder.addRecords(addSharingPortal);
            commentsSharingBuilder.save();
        }
        return false;
    }
}
