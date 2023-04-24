package com.facilio.bmsconsole;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NotifyCommentMentions extends FacilioCommand implements Serializable {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean sendNotificationByDefault = true; // should be replaced with a global variable or Organization preferences
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);

        if (sendNotificationByDefault) {
            List<NoteContext> notes = (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
            if (notes == null) {
                NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
                if (note != null) {
                    notes = Collections.singletonList(note);
                    context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
                }
            }
            if(notes == null || notes.isEmpty()){
                return false;
            }
            for (NoteContext note : notes) {
                List<CommentMentionContext> mentions = note.getMentions();
                if(mentions == null || mentions.isEmpty()) {
                    continue;
                }
                ArrayList<Map<String, Object>> usersToSendNotification = getUsersToSendNotification(mentions);
                if(usersToSendNotification.isEmpty()){
                    continue;
                }
                publishNotification(modBean, parentModuleName, note, usersToSendNotification);

            }
        }

        return false;
    }

    private static ArrayList<Map<String, Object>> getUsersToSendNotification(List<CommentMentionContext> mentions) throws Exception {
        ArrayList<Map<String,Object>> usersToSendNotification = new ArrayList<>();
        for (CommentMentionContext mention: mentions) {
            if(mention.getMentionTypeEnum() == CommentMentionContext.MentionType.PEOPLE){
                long peopleID = mention.getMentionedRecordId();
                if(peopleID != -1){
                    List<Map<String, Object>> orgUsers = PeopleAPI.getOrgUserAndApplicationMap(peopleID);
                    if(orgUsers != null && !orgUsers.isEmpty()){
                        usersToSendNotification.addAll(orgUsers);
                    }
                }
            }
        }
        return usersToSendNotification;
    }

    private static void publishNotification(ModuleBean modBean, String parentModuleName, NoteContext note, ArrayList<Map<String, Object>> usersToSendNotification) throws Exception {
        FacilioModule parentModule = modBean.getModule(parentModuleName);
        String title = "You have been mentioned in a comment";
        String Message = "#" + note.getParentId() + ", " +parentModule.getDisplayName() + " Comment : \n" +
                 note.getBodyText();
        List<UserNotificationContext> recordList = new ArrayList<>();
        for (Map<String,Object> OrgUserDetails : usersToSendNotification) {
            Long ouid = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.OUID);
            Long uid = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.USER);
            Long appId = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.APPLICATION_ID);
            if(!activeUser(ouid)){
                continue;
            }
            UserNotificationContext userNotification = new UserNotificationContext();
            userNotification.setSubject(Message);
            userNotification.setTitle(title);
            User user = new User();
            user.setId(uid);
            userNotification.setUser(user);
            userNotification._setSysCreatedTime(note.getSysCreatedTime());
            if(parentModule != null){
                userNotification.setParentModule(modBean.getModule(parentModuleName).getModuleId());
                userNotification.setParentId(note.getParentId());
                userNotification.setActionType(UserNotificationContext.ActionType.SUMMARY);
            }
            userNotification.setApplication(appId);
            recordList.add(userNotification);
        }

        FacilioModule userNotificationModule = modBean.getModule(FacilioConstants.ContextNames.USER_NOTIFICATION);
        JSONObject pushNotificationObj = new JSONObject();
        pushNotificationObj.put("isPushNotification",false);
        V3Util.createRecordList(userNotificationModule,FieldUtil.getAsMapList(recordList,UserNotificationContext.class),pushNotificationObj,null);
    }

    private static boolean activeUser(long ouid) throws Exception {
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        User user = userBean.getUser(ouid, false);
        return user != null && user.getUserStatus();
    }
}
