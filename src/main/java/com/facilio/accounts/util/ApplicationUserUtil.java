package com.facilio.accounts.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.bean.SandboxBean;
import com.facilio.identity.client.bean.UserBean;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.*;


public class ApplicationUserUtil {

    public static void  addAppUser(long orgId,boolean isPortal, PeopleUserContext peopleUser, boolean sendInvitation, String password) throws Exception{
       User iamUser;

        if(sendInvitation)
        {
            iamUser = IdentityClient.getDefaultInstance().getUserBean().inviteUser(orgId,peopleUser.getUser());
        }
        else {
            iamUser = IdentityClient.getDefaultInstance().getUserBean().addUser(orgId, peopleUser.getUser(), password);
        }
        peopleUser.setIamOrgUserId(iamUser.getOuid());
        peopleUser.setUid(iamUser.getUid());
        //will remove this once ORG_User deprecated
        addOrgUser(peopleUser,isPortal);
        addOrgUserApps(peopleUser);
    }

    public static PeopleContext checkIfExistsinPeople(String email) throws Exception{
        PeopleContext existingPeople = PeopleAPI.getPeople(email);
        if(existingPeople!=null){
            return existingPeople;
        }
        return null;
    }

    public static User checkIfAlreadyExistsInApp(long orgId,String email,String identifier,long applicationId) throws Exception{
        User user = IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, email, identifier);
        if(user != null) {
            List<Map<String, Object>> records = getOrgAppUsers(user.getUid(),applicationId);
            if(CollectionUtils.isNotEmpty(records)){
                return user;
            }
        }
        return null;
    }

    @Deprecated
    private static void addOrgUser(PeopleUserContext user,boolean isPortal) throws Exception {
        long orgUserId = checkIfExistsInOrgUsers(user.getUid(), user.getUser().getOrgId());
        if (orgUserId <0) {
            int userType=1;
            if(isPortal){
                userType = 2;
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            InsertRecordBuilder<ResourceContext> insertRecordBuilder = new InsertRecordBuilder<ResourceContext>()
                    .moduleName(FacilioConstants.ContextNames.RESOURCE)
                    .fields(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE));
            ResourceContext resource = new ResourceContext();
            resource.setName(user.getUser().getEmail());
            resource.setResourceType(ResourceContext.ResourceType.USER);

            long id = insertRecordBuilder.insert(resource);

            List<FacilioField> fields = new ArrayList<FacilioField>();
            fields.addAll(AccountConstants.getAppOrgUserFields());
            fields.add(AccountConstants.getOrgIdField(AccountConstants.getAppOrgUserModule()));
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(AccountConstants.getAppOrgUserModule().getTableName()).fields(fields);

            Map<String, Object> props = FieldUtil.getAsProperties(user);
            props.put("orgId", user.getUser().getOrgId());
            props.put("ouid", id);
            props.put("inviteAcceptStatus",true);
            props.put("userType",userType);
            insertBuilder.addRecord(props);
            insertBuilder.save();
            user.setOrgUserId(id);
        } else {
            user.setOrgUserId(orgUserId);
        }
    }

    @Deprecated
    private static long checkIfExistsInOrgUsers(long uId, long orgId) throws Exception {
        List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(orgUserFields)
                .table("ORG_Users");
        selectBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));


        List<Map<String , Object>> mapList = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(mapList)) {
            for(Map<String, Object> map : mapList) {
                return (long) map.get("ouid");
            }
        }

        return -1;
    }



    public static void addOrgUserApps(PeopleUserContext user) throws Exception {
        List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(AccountConstants.getOrgUserAppsModule().getTableName()).fields(fields);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("ouid", user.getOrgUserId());
        props.put("applicationId", user.getApplicationId());
        props.put("orgId", user.getUser().getOrgId());
        props.put("roleId", user.getRoleId());
        //need to add new column in org_user_apps
//        props.put("peopleId",user.getPeopleId());
//        props.put("uid",user.getUid());


        insertBuilder.addRecord(props);
        insertBuilder.save();

    }

    public static void updateAppUser(PeopleUserContext user) throws Exception{
        IdentityClient.getDefaultInstance().getUserBean().updateUser(user.getUid(),user.getUser());
        long orgUserId = 0;

            List<Map<String, Object>> records = getOrgAppUsers(user.getUid(),user.getApplicationId());
            if(CollectionUtils.isNotEmpty(records)){

                for (Map<String, Object> record : records) {
                    orgUserId = (long) record.get("ouid");
                }
                user.setOrgUserId(orgUserId);
                updateOrgUser(user);
                updateOrgAppUser(user);

            } else if(AccountUtil.getCurrentOrg() != null) {

                user.getUser().setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                orgUserId = checkIfExistsInOrgUsers(user.getUid(), user.getUser().getOrgId());
                if(orgUserId > 0){
                    user.setOrgUserId(orgUserId);
                    addOrgUserApps(user);
                }

            }
    }

    private static void updateOrgUser(PeopleUserContext user) throws Exception{
        FacilioField people = FieldFactory.getNumberField("peopleId","PEOPLE_ID",AccountConstants.getAppOrgUserModule());
            GenericUpdateRecordBuilder updatOrgUserBuilder = new GenericUpdateRecordBuilder()
                    .table(AccountConstants.getAppOrgUserModule().getTableName())
                    .fields(Collections.singletonList(people))
                    .andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouid", String.valueOf(user.getOrgUserId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("USERID", "uid", String.valueOf(user.getUid()), NumberOperators.EQUALS));

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("peopleId", user.getPeopleId());
            updatOrgUserBuilder.update(updateMap);

    }

    private static void updateOrgAppUser(PeopleUserContext user) throws Exception{
        if(user.getApplicationId() > 0) {
            GenericUpdateRecordBuilder updateAppUserBuilder = new GenericUpdateRecordBuilder()
                    .table(AccountConstants.getOrgUserAppsModule().getTableName())
                    .fields(Collections.singletonList(AccountConstants.getRoleIdField()))
                    .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(user.getApplicationId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(user.getOrgUserId()), NumberOperators.EQUALS));
//            Criteria criteria = new Criteria();
//            criteria.addAndCondition(CriteriaAPI.getCondition("IAM_USERID", "uid", String.valueOf(user.getUid()), NumberOperators.EQUALS));
//            criteria.addOrCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserId", String.valueOf(user.getOrgUserId()), NumberOperators.EQUALS));
//            updateAppUserBuilder.andCriteria(criteria);
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("roleId", user.getRoleId());
            updateAppUserBuilder.update(updateMap);
        }
    }

    public static void deleteUser(long orgId, long userId) throws Exception {
        User existingUser = IdentityClient.getDefaultInstance().getUserBean().getUser(orgId,userId);
        if(existingUser != null) {
            IdentityClient.getDefaultInstance().getUserBean().deleteUser(orgId, userId);
            List<Long> orgUserIds = new ArrayList<>();
            List<Map<String, Object>> records = getOrgAppUsers(userId, null);
            if (CollectionUtils.isNotEmpty(records)) {

                for (Map<String, Object> record : records) {
                    orgUserIds.add((Long) record.get("ouid"));
                }

            }
            if (CollectionUtils.isNotEmpty(orgUserIds)) {
                deleteOrgAppUsers(userId, orgUserIds, null);
            }
        }
    }

    public static void revokeAppAccess( long userId,long appId) throws Exception{
        List<Map<String, Object>> records = getOrgAppUsers(userId,appId);
        List<Long> orgUserIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)){

            for (Map<String, Object> record : records) {
                orgUserIds.add((Long) record.get("ouid"));
            }

        }
        if(CollectionUtils.isNotEmpty(orgUserIds)){
            deleteOrgAppUsers(userId,orgUserIds,appId);
        }
    }

    private static void deleteOrgAppUsers(long userId, List<Long> orgUserIds,Long applicationId) throws Exception {

        GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                .table(AccountConstants.getOrgUserAppsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", StringUtils.join(orgUserIds, ","), NumberOperators.EQUALS));
        if (applicationId != null && applicationId > 0) {
            deleteBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID","applicationId", String.valueOf(applicationId), NumberOperators.EQUALS));
        }
//        Criteria criteria = new Criteria();
//        criteria.addAndCondition(CriteriaAPI.getCondition("IAM_USERID", "uid", String.valueOf(userId), NumberOperators.EQUALS));
//        criteria.addOrCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(orgUserId), NumberOperators.EQUALS));
//        deleteBuilder.andCriteria(criteria);
//        //need to check if support for application specific
        deleteBuilder.delete();
    }

    public static List<Map<String, Object>> getOrgAppUsers( long uid,Long appId)throws Exception{

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getOrgUserAppsFields());
        fields.add(AccountConstants.getApplicationIdField());
        fields.add(AccountConstants.getRoleIdField());

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_Users")
                .innerJoin("ORG_User_Apps")
                .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID");



        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "uid", String.valueOf(uid), NumberOperators.EQUALS));

        if (appId != null && appId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
        }
        List<Map<String, Object>> props = selectBuilder.get();

        return props;
    }

    public static void deletePortalAccess(V3PeopleContext people) throws Exception {

        FacilioField isOccupantPortalAccess = Constants.getModBean().getField("isOccupantPortalAccess",FacilioConstants.ContextNames.PEOPLE);
        FacilioField employeePortalAccess = Constants.getModBean().getField("employeePortalAccess",FacilioConstants.ContextNames.PEOPLE);
        people.setIsOccupantPortalAccess(false);
        people.setIsEmployeePortalAccess(false);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(isOccupantPortalAccess);
        fields.add(employeePortalAccess);
        ModuleBean modBean = Constants.getModBean();
        V3RecordAPI.updateRecord(people, modBean.getModule(FacilioConstants.ContextNames.PEOPLE), fields);

        switch (people.getPeopleTypeEnum()){
            case TENANT_CONTACT:
                FacilioField isTenantPortalAccess = Constants.getModBean().getField("isTenantPortalAccess",FacilioConstants.ContextNames.TENANT_CONTACT);
                V3TenantContactContext tc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3TenantContactContext.class);
                tc.setIsTenantPortalAccess(false);
                V3RecordAPI.updateRecord(tc, modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT), Collections.singletonList(isTenantPortalAccess));
                break;
            case VENDOR_CONTACT:
                FacilioField isVendorPortalAccess = Constants.getModBean().getField("isVendorPortalAccess",FacilioConstants.ContextNames.VENDOR_CONTACT);
                V3VendorContactContext vc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3VendorContactContext.class);
                vc.setIsVendorPortalAccess(false);
                V3RecordAPI.updateRecord(vc, modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT), Collections.singletonList(isVendorPortalAccess));
                break;
            case CLIENT_CONTACT:
                FacilioField isClientPortalAccess = Constants.getModBean().getField("isClientPortalAccess",FacilioConstants.ContextNames.CLIENT_CONTACT);
                V3ClientContactContext cc = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(people),V3ClientContactContext.class);
                cc.setIsClientPortalAccess(false);
                V3RecordAPI.updateRecord(cc, modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT), Collections.singletonList(isClientPortalAccess));
                break;
        }
    }

    public static void addSandboxUser(long orgId, boolean isPortal, boolean addAppAccess, PeopleUserContext peopleUser) throws Exception{
        SandboxBean sandboxBean = IdentityClient.getDefaultInstance().getSandboxBean();
        User iamUser = sandboxBean.addSandboxUser(orgId, peopleUser.getUser());
        peopleUser.setIamOrgUserId(iamUser.getOuid());
        peopleUser.setUid(iamUser.getUid());

        addOrgUser(peopleUser, isPortal);

        if (addAppAccess) {
            addOrgUserApps(peopleUser);
        }
    }

}
