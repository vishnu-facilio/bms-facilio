package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommentMentionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.PeopleNotificationSettings;
import com.facilio.bmsconsoleV3.context.UserNotificationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class NotifyCommentMentions extends FacilioCommand implements Serializable {
    private static final String TITLE = "title";
    private static final String BODY = "body";


    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
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
        FacilioModule parentModule = modBean.getModule(parentModuleName);
        String name = parentModuleName == FacilioConstants.ContextNames.WORK_ORDER ? FacilioConstants.ContextNames.COMMENT : FacilioConstants.ContextNames.NOTE;

        for (NoteContext note : notes) {
            List<CommentMentionContext> mentions = note.getMentions();
            if(note.getBody() == null || mentions == null || mentions.isEmpty()) {
                continue;
            }
            Map<String, String> notificationContent = constructMessage(parentModule, name, note);
            Set<Long> peopleIds = new HashSet<>();
            ArrayList<Map<String,Object>> users = new ArrayList<>();
            for (CommentMentionContext mention: mentions) {
                if(mention.getMentionTypeEnum() == CommentMentionContext.MentionType.PEOPLE){
                    long peopleID = mention.getMentionedRecordId();
                    if(peopleID != -1){
                        peopleIds.add(peopleID);
                        List<Map<String, Object>> orgUsers = PeopleAPI.getOrgUserAndApplicationMap(peopleID);
                        if(orgUsers != null && !orgUsers.isEmpty()){
                            users.addAll(orgUsers);
                        }
                    }
                }
            }
            if(peopleIds.isEmpty() || users.isEmpty()){
                continue;
            }
            List<PeopleNotificationSettings> disabledNotifications = PeopleAPI.getDisabledNotificationSettingsForPeople(peopleIds);
            List<Map<String, Object>> usersToSendInAppNotification = filterUsersToSendInAppNotification(users,disabledNotifications);
            if(usersToSendInAppNotification != null && !usersToSendInAppNotification.isEmpty()){
                sendInAppNotification(modBean, parentModule, note, usersToSendInAppNotification,notificationContent);
            }
            Set<Long> pplIdsToSendEmail = getFilteredPeopleToSendEmail(peopleIds,disabledNotifications);
            if(pplIdsToSendEmail != null && !pplIdsToSendEmail.isEmpty()){
                sendEmailNotification(pplIdsToSendEmail, notificationContent,note,parentModule);
            }
        }
        return false;
    }

    private Set<Long> getFilteredPeopleToSendEmail(Set<Long> peopleIds, List<PeopleNotificationSettings> disabledNotifications) {
        if(disabledNotifications != null && !disabledNotifications.isEmpty()){
            Set<Long> restrictPeopleToSendEmail = disabledNotifications.stream().filter(n -> n.getNotificationType() == PeopleNotificationSettings.Notification_Types.COMMENT_MENTION_EMAIL).map(s -> s.getPeopleId()).collect(Collectors.toSet());
            if(restrictPeopleToSendEmail != null && !restrictPeopleToSendEmail.isEmpty()){
                return peopleIds.stream().filter(p -> !restrictPeopleToSendEmail.contains(p)).collect(Collectors.toSet());
            }
        }
        return peopleIds;
    }

    private List<Map<String, Object>> filterUsersToSendInAppNotification(ArrayList<Map<String, Object>> users, List<PeopleNotificationSettings> disabledNotifications) {
        if(disabledNotifications != null && !disabledNotifications.isEmpty()){
            Set<Long> restrictPeopleToSendNotification = disabledNotifications.stream().filter(n -> n.getNotificationType() == PeopleNotificationSettings.Notification_Types.COMMENT_MENTION_IN_APP
                    && n.getDisabled()).map(n -> n.getPeopleId()).collect(Collectors.toSet());
            if(restrictPeopleToSendNotification != null && !restrictPeopleToSendNotification.isEmpty()){
                return users.stream().filter(u -> !restrictPeopleToSendNotification.contains(u.get(FacilioConstants.ContextNames.PEOPLE))).collect(Collectors.toList());
            }
        }
        return users;
    }


    private static Map<String, String> constructMessage(FacilioModule parentModule, String name, NoteContext note) {
        String title = "You have been mentioned in a "+ name;
        String body = "#" + note.getParentId() + ", " + parentModule.getDisplayName() + " " +name+ " : \n" +
                note.getBody();
        Map<String,String> notificationContent = new HashMap<>();
        notificationContent.put(TITLE, title);
        notificationContent.put(BODY,body);
        return notificationContent;
    }


    private void sendEmailNotification(Set<Long> pplIds, Map<String, String> notificationContent, NoteContext note, FacilioModule parentModule) throws Exception {
        List<V3PeopleContext> ppl = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.PEOPLE, pplIds, V3PeopleContext.class);
        for (V3PeopleContext person: ppl) {
            String message = getEmailHtml(parentModule.getDisplayName(),note,person.getName());
            if (message == null){
                continue;
            }
            JSONObject mailJson = new JSONObject();
            mailJson.put(EmailClient.SENDER, EmailFactory.getEmailClient().getNoReplyFromEmail());
            mailJson.put(EmailClient.TO, person.getEmail());
            mailJson.put(EmailClient.SUBJECT, notificationContent.get(TITLE));
            mailJson.put(EmailClient.MESSAGE, message);
            mailJson.put(FacilioConstants.ContextNames.SOURCE_TYPE, MailSourceType.COMMENTS.name());
            mailJson.put(EmailClient.MAIL_TYPE, EmailClient.HTML);
            mailJson.put(FacilioConstants.ContextNames.MODULE_ID,parentModule.getModuleId());
            mailJson.put(FacilioConstants.ContextNames.RECORD_ID,note.getParentId());
            FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(mailJson);
        }
    }




    private static void sendInAppNotification(ModuleBean modBean, FacilioModule parentModule, NoteContext note, List<Map<String, Object>> usersToSendNotification, Map<String, String> notificationContent) throws Exception {
        List<UserNotificationContext> recordList = new ArrayList<>();
        for (Map<String,Object> OrgUserDetails : usersToSendNotification) {
            Long ouid = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.OUID);
            Long uid = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.USER);
            Long appId = (Long) OrgUserDetails.get(FacilioConstants.ContextNames.APPLICATION_ID);
            if(!activeUser(ouid)){
                continue;
            }
            UserNotificationContext userNotification = new UserNotificationContext();
            userNotification.setSubject(notificationContent.get(BODY));
            userNotification.setTitle(notificationContent.get(TITLE));
            User user = new User();
            user.setId(uid);
            userNotification.setUser(user);
            userNotification._setSysCreatedTime(note.getSysCreatedTime());
            if(parentModule != null){
                userNotification.setParentModule(parentModule.getModuleId());
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

    private static String getEmailHtml(String moduleName, NoteContext note, String recipientName){
        Long recordId = note.getParentId();
        String sender = note.getCreatedBy().getName();
        String noteContent = note.getBodyHTML() != null && note.getBodyHTML().isEmpty() ? note.getBodyHTML() : note.getBody();
        String redirectUrl = "https://app.facilio.com";
        if(isNull(recordId,sender,noteContent,redirectUrl)){
            return null;
        }
        String message = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>Document</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div>\n" +
                "      <div\n" +
                "        class=\"\"\n" +
                "        style=\"\n" +
                "    margin-top: 25px;\n" +
                "    font-size: 15px;\n" +
                "    font-weight: bold;\n" +
                "  \"\n" +
                "      >\n" +
                "        Hi "+recipientName+",\n" +
                "      </div>\n" +
                "\n" +
                "      <div\n" +
                "        style=\"\n" +
                "    padding-top: 20px;\n" +
                "    /* font-size: 14px; */\n" +
                "  \"\n" +
                "      >\n" +
                "        <span\n" +
                "          style=\"\n" +
                "    /* font-weight: bold; */\n" +
                "  \"\n" +
                "          >"+sender+" </span\n" +
                "        >mentioned you in the\n" +
                "        <span\n" +
                "          style=\"\n" +
                "    /* font-weight: bold; */\n" +
                "  \"\n" +
                "          >"+moduleName+"</span\n" +
                "        >\n" +
                "        Module note for record ID\n" +
                "        <span\n" +
                "          >#"+ recordId+"\n" +
                "         </span\n" +
                "        >\n" +
                "      </div>\n" +
                "      <div\n" +
                "        class=\"notes\"\n" +
                "        style=\"\n" +
                "    padding: 10px;\n" +
                "    background: #f1f1f1;\n" +
                "    width: fit-content;\n" +
                "    /* font-size: 13px; */\n" +
                "    margin: 10px;\n" +
                "    margin-left: 0;\n" +
                "    border-radius: 0.2rem;\n" +
                "  \"\n" +
                "      >\n" +
                "        "+noteContent+"\n" +
                "      </div>\n" +
                "      <div class=\"links\"><a href=\""+redirectUrl+"\"></a></div>\n" +
                "      <div class=\"yj6qo\"></div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
        return message;
    }

    private static Boolean isNull(Object... values) {
        for (Object value : values) {
            if(value == null){
                return true;
            }
        }
        return false;
    }
}
