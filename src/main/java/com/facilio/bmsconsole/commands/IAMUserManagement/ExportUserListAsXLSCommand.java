package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.command.FacilioCommand;
import com.facilio.fs.FileInfo;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.formatter.DateFormatter;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.identity.client.dto.User;
import com.facilio.bmsconsole.util.ExportUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ExportUserListAsXLSCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<PeopleUserContext> userList = (List<PeopleUserContext>) context.get(FacilioConstants.ContextNames.USERS);
        if(CollectionUtils.isEmpty(userList)){
            return false;
        }

        String linkName = (String)context.get(FacilioConstants.ContextNames.APP_LINKNAME);
        String finalLinkName  = null;

        switch(linkName){
            case"vendor":
                finalLinkName = FacilioConstants.UserPeopleKeys.VENDOR;
                break;
            case "tenant":
                finalLinkName = FacilioConstants.UserPeopleKeys.TENANT;
                break;
            case "client":
                finalLinkName = FacilioConstants.UserPeopleKeys.CLIENT;
                break;
            default:
                finalLinkName = null;
                break;
        }

        String finalLinkNameHeader = finalLinkName;

        FileInfo.FileFormat fileFormat = FileInfo.FileFormat.XLS;
        String fileName = (String) context.getOrDefault(FacilioConstants.ContextNames.FILE_NAME,"User_Data");
        String tabName = (String) context.get(FacilioConstants.UserPeopleKeys.TAB_NAME);

        List<String> headers =new ArrayList<>();
        switch (tabName){
            case "allUsers":
                headers = new ArrayList<String>(){{
                    add(FacilioConstants.UserPeopleKeys.NAME);
                    add(FacilioConstants.UserPeopleKeys.EMAIL);
                    if(finalLinkNameHeader !=null){
                        add(finalLinkNameHeader);
                    }
                    add(FacilioConstants.UserPeopleKeys.ROLE);
                    add(FacilioConstants.UserPeopleKeys.LAST_LOGIN_TIME);
                    add(FacilioConstants.UserPeopleKeys.USER_STATUS);
                    add(FacilioConstants.UserPeopleKeys.INVITE_STATUS);
                }};
                break;

            case "inviteAcceptedUsers":
                headers = new ArrayList<String>(){{
                    add(FacilioConstants.UserPeopleKeys.NAME);
                    add(FacilioConstants.UserPeopleKeys.EMAIL);
                    if(finalLinkNameHeader !=null){
                        add(finalLinkNameHeader);
                    }
                    add(FacilioConstants.UserPeopleKeys.ROLE);
                    add(FacilioConstants.UserPeopleKeys.LAST_LOGIN_TIME);
                    add(FacilioConstants.UserPeopleKeys.USER_STATUS);
                }};
                break;

            case "pendingAcceptance":
                headers = new ArrayList<String>(){{
                    add(FacilioConstants.UserPeopleKeys.NAME);
                    add(FacilioConstants.UserPeopleKeys.EMAIL);
                    if(finalLinkNameHeader !=null){
                        add(finalLinkNameHeader);
                    }
                    add(FacilioConstants.UserPeopleKeys.ROLE);
                    add(FacilioConstants.UserPeopleKeys.INVITED_TIME);
                    add(FacilioConstants.UserPeopleKeys.USER_STATUS);
                    add(FacilioConstants.UserPeopleKeys.INVITE_STATUS);
                }};
                break;
        }



        List<Map<String,Object>> userTableData = new ArrayList<>();
        for(PeopleUserContext people: userList){
            if(people!= null){
                User user = people.getUser();
                if(user != null){
                    Map<String,Object> userData = new HashMap<>();
                    userData.put(FacilioConstants.UserPeopleKeys.NAME,user.getName());
                    userData.put(FacilioConstants.UserPeopleKeys.EMAIL,user.getEmail());
                    if(finalLinkName != null){
                        userData.put(finalLinkName,getPortalUserName(finalLinkName,people));
                    }
                    if(people.getRole() != null){
                        userData.put(FacilioConstants.UserPeopleKeys.ROLE,people.getRole().getName());
                    }
                    FacilioField invitedTimeField = new FacilioField(IAMAccountConstants.getAccountsUserModule(),"invitedTime","Invited Time", FacilioField.FieldDisplayType.DATETIME,"INVITED_TIME", FieldType.DATE_TIME,true,false,true,false);
                    DateFormatter formatter = new DateFormatter(invitedTimeField);
                    String invitedTime = user.getInvitedTime() > 0 ? (String) formatter.format(user.getInvitedTime()) : "";
                    String lastLoginTime = user.getLastLoginTime() > 0 ? (String) formatter.format(user.getLastLoginTime()) : "";
                    userData.put(FacilioConstants.UserPeopleKeys.INVITED_TIME,invitedTime);
                    userData.put(FacilioConstants.UserPeopleKeys.LAST_LOGIN_TIME,lastLoginTime);
                    userData.put(FacilioConstants.UserPeopleKeys.USER_STATUS,user.getUserStatus());
                    userData.put(FacilioConstants.UserPeopleKeys.INVITE_STATUS,user.getInviteStatusEnum());
                    userTableData.add(userData);
                }
            }
        }
        Map<String, Object> table = new HashMap<>();
        table.put("headers",headers);
        table.put("records",userTableData);
        String url = ExportUtil.exportData(fileFormat,fileName,table,false);
        context.put(FacilioConstants.ContextNames.FILE_URL,url);
        return false;
    }

    private static String getPortalUserName(String linkName , PeopleUserContext people){
        if(linkName.equals(FacilioConstants.UserPeopleKeys.VENDOR)){
            return people.getVendorName();
        }else if (linkName.equals(FacilioConstants.UserPeopleKeys.TENANT)){
            return people.getTenantName();
        }else if(linkName.equals(FacilioConstants.UserPeopleKeys.CLIENT)){
            return people.getClientName();
        }
        return null;
    }
}
