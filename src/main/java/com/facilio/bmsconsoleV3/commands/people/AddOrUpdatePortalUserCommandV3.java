package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.AppDomain;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddOrUpdatePortalUserCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> rawInput = Constants.getRawInput(context);
        long securityPolicyId = (long) rawInput.getOrDefault("selectedSecuritypolicy",-1L);
        boolean sendInvitation = (boolean) rawInput.getOrDefault("sendInvitation",true);
        String password = (String) rawInput.getOrDefault("password",null);
        Map<Long, V3PeopleContext> oldPeopleRecordMap = (Map<Long, V3PeopleContext>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<V3PeopleContext> pplList = recordMap.get(moduleName);

        for(V3PeopleContext people : pplList){
            if(people != null){
                if(oldPeopleRecordMap != null && !oldPeopleRecordMap.isEmpty()){
                    V3PeopleContext oldPeopleRecord = oldPeopleRecordMap.get(people.getId());
                    Map<String,Long> rolemaps = people.getRolesMap();
                    if(rolemaps != null && !rolemaps.isEmpty()){
                        updateAccess(oldPeopleRecord,people,rolemaps,securityPolicyId,sendInvitation,password);
                    }
                }
            }
        }

        return false;
    }

    private static void updateAppAccess(Map<String,Long> rolemaps,V3PeopleContext people,Long orgId,Long securityPolicyId,boolean sendInvitation,String password) throws Exception{
        PeopleUserContext user = new PeopleUserContext();
        if(rolemaps != null && !rolemaps.isEmpty()){
            for (Map.Entry<String,Long> entryset : rolemaps.entrySet()){
                String appLinkName = entryset.getKey();
                Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                AppDomain appDomainObj = null;
                if(appId != null){
                    appDomainObj = ApplicationApi.getAppDomainForApp(appId);
                }
                if(appDomainObj == null) {
                    throw new IllegalArgumentException("Invalid App Domain");
                }
                Long roleId = entryset.getValue();
                if(roleId > 0){
                    PeopleContext existingPeople = PeopleAPI.getPeopleForId(people.getId());
                    if(StringUtils.isEmpty(existingPeople.getEmail())){
                        throw new IllegalArgumentException("EmailId cannot be null");
                    }
                    User existingUser = ApplicationUserUtil.getUser(existingPeople.getEmail(), appDomainObj.getIdentifier());
                    if(existingUser == null){

                        User newUser = new User();
                        newUser.setOrgId(orgId);
                        newUser.setName(existingPeople.getName());
                        newUser.setEmail(existingPeople.getEmail());
                        newUser.setUsername(existingPeople.getEmail());
                        newUser.setIdentifier(appDomainObj.getIdentifier());
                        newUser.setPhone(existingPeople.getPhone());
                        newUser.setLanguage(existingPeople.getLanguage());
                        newUser.setInvitedBy(AccountUtil.getCurrentUser().getUid());
                        newUser.setAppDomain(appDomainObj);
                        newUser.setTimezone(existingPeople.getTimezone());
                        newUser.setMobile(existingPeople.getMobile());
                        newUser.setSecurityPolicyId(securityPolicyId);
                        user.setPeopleId(people.getId());
                        user.setRoleId(roleId);
                        user.setApplicationId(appId);
                        user.setUser(newUser);

                        ApplicationUserUtil.addAppUser(user.getUser().getOrgId(),true, user, sendInvitation, password);
                    }else {
                        existingUser.setSecurityPolicyId(securityPolicyId);
                        user.setPeopleId(people.getId());
                        user.setRoleId(roleId);
                        user.setApplicationId(appId);
                        user.setUser(existingUser);
                        user.setUid(existingUser.getUid());
                        ApplicationUserUtil.updateAppUser(user);
                    }
                    V3PeopleAPI.updatePortalAccess(people, appLinkName, true);
                }
            }

        }
    }

    private static void updateAccess(V3PeopleContext oldPeopleRecord,V3PeopleContext people,Map<String,Long> rolemaps,Long securityPolicyId,boolean sendInvitation,String password) throws Exception{
        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Map<String,Long> rolemap = new HashMap<>();
        if(oldPeopleRecord instanceof V3TenantContactContext){
            String appLinkName = FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP;
            if(((V3TenantContactContext)people).isTenantPortalAccess()){
                Long roleId = rolemaps.get(appLinkName);
                if(roleId != null && roleId > 0){
                    rolemap.put(appLinkName, roleId);
                    updateAppAccess(rolemap,people,orgId,securityPolicyId,sendInvitation,password);
                }
            }else if(!((V3TenantContactContext)people).isTenantPortalAccess()){
                if(StringUtils.isNotEmpty(people.getEmail())){
                    rolemaps.remove(appLinkName);
                    Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                    User user = getUser(people.getEmail(),appId);
                    if(isUserHasAppAccess(user,appId)){
                        deleteOrRevokeAppAccess(appLinkName,orgId,people);
                    }
                }
            }
            checkForOccupantPortalAccess(people,oldPeopleRecord,rolemaps,securityPolicyId,sendInvitation,password);

        }
        if(oldPeopleRecord instanceof V3VendorContactContext){
            String appLinkName = FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP;
            if(((V3VendorContactContext) people).isVendorPortalAccess()){
                Long roleId = rolemaps.get(appLinkName);
                if(roleId != null && roleId > 0){
                    rolemap.put(appLinkName, roleId);
                    updateAppAccess(rolemap,people,orgId,securityPolicyId,sendInvitation,password);
                }
            }else if(!((V3VendorContactContext) people).isVendorPortalAccess()){
                if(StringUtils.isNotEmpty(people.getEmail())){
                    rolemaps.remove(appLinkName);
                    Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                    User user = getUser(people.getEmail(),appId);
                    if(isUserHasAppAccess(user,appId)){
                        deleteOrRevokeAppAccess(appLinkName,orgId,people);
                    }
                }
            }
            checkForOccupantPortalAccess(people,oldPeopleRecord,rolemaps,securityPolicyId,sendInvitation,password);
        }
        if(oldPeopleRecord instanceof V3ClientContactContext){
            String appLinkName = FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP;
            if(((V3ClientContactContext) people).isClientPortalAccess()){
                Long roleId = rolemaps.get(appLinkName);
                if(roleId != null && roleId > 0){
                    rolemap.put(appLinkName, roleId);
                    updateAppAccess(rolemap,people,orgId,securityPolicyId,sendInvitation,password);
                }
            }else if(!((V3ClientContactContext) people).isClientPortalAccess()){
                if(StringUtils.isNotEmpty(people.getEmail())){
                    rolemaps.remove(appLinkName);
                    Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                    User user = getUser(people.getEmail(),appId);
                    if(isUserHasAppAccess(user,appId)){
                        deleteOrRevokeAppAccess(appLinkName,orgId,people);
                    }
                }
            }
            checkForOccupantPortalAccess(people,oldPeopleRecord,rolemaps,securityPolicyId,sendInvitation,password);
        }
        if(oldPeopleRecord instanceof V3EmployeeContext){
            String appLinkName = FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP;
            if(people.isEmployeePortalAccess()){
                Long roleId = rolemaps.get(appLinkName);
                if(roleId != null && roleId > 0){
                    rolemap.put(appLinkName, roleId);
                    updateAppAccess(rolemap,people,orgId,securityPolicyId,sendInvitation,password);
                }
            }else if(!people.isEmployeePortalAccess()){
                if(StringUtils.isNotEmpty(people.getEmail())){
                    rolemaps.remove(appLinkName);
                    Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                    User user = getUser(people.getEmail(),appId);
                    if(isUserHasAppAccess(user,appId)){
                        deleteOrRevokeAppAccess(appLinkName,orgId,people);
                    }
                }
            }
            checkForOccupantPortalAccess(people,oldPeopleRecord,rolemaps,securityPolicyId,sendInvitation,password);
        }
    }

    private static boolean isUserPresentInAnotherApp(User user) throws Exception{
        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if(user != null){
            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(AccountConstants.getOrgUserAppsFields());

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table("ORG_Users")
                    .innerJoin("ORG_User_Apps")
                    .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");

            selectBuilder.andCondition(CriteriaAPI.getCondition("USERID","userId",user.getUid()+"", NumberOperators.EQUALS));
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID","orgId",orgId+"",NumberOperators.EQUALS));

            List<Map<String,Object>> props = selectBuilder.get();
            if(props.size()>1){
                return true;
            }
        }
       return false;
    }

    private static boolean isUserHasAppAccess(User user,Long appId) throws Exception{
        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if(user != null){
            List<FacilioField> fields = new ArrayList<>();
            fields.addAll(AccountConstants.getOrgUserAppsFields());

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table("ORG_Users")
                    .innerJoin("ORG_User_Apps")
                    .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");

            selectBuilder.andCondition(CriteriaAPI.getCondition("USERID","userId",user.getUid()+"", NumberOperators.EQUALS));
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID","orgId",orgId+"",NumberOperators.EQUALS));
            selectBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID","appId",appId+"",NumberOperators.EQUALS));

            List<Map<String,Object>> props = selectBuilder.get();
            if(CollectionUtils.isNotEmpty(props)){
                return true;
            }
        }
        return false;
    }

        private static void deleteOrRevokeAppAccess(String appLinkName,Long orgId,V3PeopleContext people) throws Exception{
        Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
        User user = getUser(people.getEmail(),appId);
        if(isUserPresentInAnotherApp(user)){
            ApplicationUserUtil.revokeAppAccess(user.getUid(),appId);
        }else{
            ApplicationUserUtil.revokeAppAccess(user.getUid(),appId);
            IdentityClient.getDefaultInstance().getUserBean().deleteUser(orgId,user.getUid());
        }
    }

    private static void checkForOccupantPortalAccess(V3PeopleContext people,V3PeopleContext oldPeopleRecord,Map<String,Long> rolemaps,Long securityPolicyId,boolean sendInvitation,String password) throws Exception{
        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Map<String,Long> rolemap = new HashMap<>();
        String appLinkName = FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP;
        if(people.isOccupantPortalAccess()){
            Long roleId = rolemaps.get(appLinkName);
            if(roleId != null && roleId > 0){
                rolemap.put(appLinkName, roleId);
                updateAppAccess(rolemap,people,orgId,securityPolicyId,sendInvitation,password);
            }
        }else if(!people.isOccupantPortalAccess()){
            if(StringUtils.isNotEmpty(people.getEmail())){
                rolemaps.remove(appLinkName);
                Long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
                User user = getUser(people.getEmail(),appId);
                if(isUserHasAppAccess(user,appId)){
                    deleteOrRevokeAppAccess(appLinkName,orgId,people);
                }
            }
        }
    }

    private static User getUser(String userName,Long appId) throws Exception {
        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        AppDomain appDomainObj = null;
        if(appId != null){
            appDomainObj = ApplicationApi.getAppDomainForApp(appId);
        }
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        User user = IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, userName, appDomainObj.getIdentifier());
        return user;
    }

}
