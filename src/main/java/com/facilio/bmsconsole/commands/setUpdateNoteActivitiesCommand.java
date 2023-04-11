package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AlarmActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.CommentSharingContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class setUpdateNoteActivitiesCommand extends FacilioCommand {

    private String parentModuleName;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub

        List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
        if (notes == null) {
            NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
            if (note != null) {
                notes = Collections.singletonList(note);
                context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
            }
        }

        if(notes != null && !notes.isEmpty()) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            this.parentModuleName =  (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
            if (moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
                if (parentModuleName == null || parentModuleName.isEmpty()) {
                    throw new IllegalArgumentException("Module name for ticket notes should be specified");
                }
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Set<Long> parentIds = new HashSet<>();
            boolean isNotifyRequester = false;
            for (NoteContext note : notes) {
                if (StringUtils.isEmpty(note.getBody())) {
                    throw new IllegalArgumentException("Comment cannot be null/ empty");
                }
                note.setEditAvailable(true);
                if (note.getCreatedTime() == -1) {
                    note.setCreatedTime(System.currentTimeMillis());
                }
                note.setCreatedBy(AccountUtil.getCurrentUser());
                ApplicationContext currentApp = AccountUtil.getCurrentApp();
                if (currentApp != null && currentApp.getAppCategoryEnum().equals(ApplicationContext.AppCategory.PORTALS)) {
                    CommentSharingContext commentSharing = new CommentSharingContext();
                    commentSharing.setAppId(AccountUtil.getCurrentApp().getId());
                    List<CommentSharingContext> commentSharingContexts = new ArrayList<>();
                    commentSharingContexts.add(commentSharing);
                    note.setCommentSharing(commentSharingContexts);
                }

                parentIds.add(note.getParentId());
                JSONObject info = new JSONObject();
                info.put(FacilioConstants.ContextNames.NOTES_COMMENT, note.getBody());
                info.put("notifyRequester", note.getNotifyRequester());
                JSONArray sahringJson= FieldUtil.getAsJSONArray(note.getCommentSharing(), CommentSharingContext.class);
                info.put("commentSharing",sahringJson);
                isNotifyRequester = (note.getCommentSharing()!=null && note.getCommentSharing().size() > 0) || note.getNotifyRequester();

                info.put("updatedBy", note.getCreatedBy().getOuid());
                if(moduleName.equals(FacilioConstants.ContextNames.TICKET_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, WorkOrderActivityType.UPDATE_COMMENT, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.ITEM_TYPES_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, ItemActivityType.ITEM_NOTES_UPDATED, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.TOOL_TYPES_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }//
                else if(moduleName.equals(FacilioConstants.ContextNames.RECEIVABLE_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.CONTRACT_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.STORE_ROOM_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.ASSET_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, AssetActivityType.ASSET_NOTES_UPDATE, info, (FacilioContext) context);
                } else if (moduleName.equals(FacilioConstants.ContextNames.BASE_ALARM_NOTES)) {
                    long occurrenceId = (long) context.get(FacilioConstants.ContextNames.ALARM_OCCURRENCE_ID);
                    CommonCommandUtil.addAlarmActivityToContext(note.getParentId(), -1, AlarmActivityType.UPDATE_COMMENT, info, (FacilioContext) context, occurrenceId);
                } else if(Arrays.asList(FacilioConstants.ContextNames.QUOTE_NOTES,FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_NOTES).contains(moduleName)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                } else if(moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                } else if(Arrays.asList(FacilioConstants.ContextNames.VENDOR_NOTES,FacilioConstants.ContextNames.TENANT_NOTES,FacilioConstants.ContextNames.DELIVERY_NOTES).contains(moduleName)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                } else if(moduleName.equals(FacilioConstants.ContextNames.SERVICE_REQUEST_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.PURCHASE_REQUEST_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else if(moduleName.equals(FacilioConstants.ContextNames.PURCHASE_ORDER_NOTES)) {
                    CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                }
                else  {
                    FacilioModule parentModule = modBean.getModule(parentModuleName);
                    if (parentModule.isCustom()) {
                        CommonCommandUtil.addActivityToContext(note.getParentId(), -1, CommonActivityType.UPDATE_NOTES, info, (FacilioContext) context);
                    }
                }

            }
            if (isNotifyRequester) {
                context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_NOTE_REQUESTER);
            }
            else {
                context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ADD_TICKET_NOTE);
            }

        }
        return false;
    }
}
