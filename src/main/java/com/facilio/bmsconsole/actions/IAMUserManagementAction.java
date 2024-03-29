package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.ApplicationUserUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.identity.client.IdentityClient;
import com.facilio.identity.client.dto.User;
import com.facilio.identity.client.dto.UserMFA;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.utils.SandboxAPI;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Getter @Setter
public class IAMUserManagementAction extends FacilioAction{
    private int orgId;
    private PeopleUserContext user;
    private String password;
    private long securityPolicyId;
    private boolean sendInvitation;
    private boolean withMFA;
    private UserMFA userMFA;
    private String email;
    private User.InviteStatus inviteStatus;
    private Long appId;
    private String appLinkName;
    private Boolean userStatus=null;
    private String search;
    private String orderBy;
    private boolean isAscending;
    private int page;
    private int perPage = -1;
    private int offset;
    private List<PeopleUserContext> users = null;
    private Boolean fetchCount;
    private Boolean isUserVerified=null;
    private long uid;
    public Boolean getFetchCount() {
        if (fetchCount == null) {
            return false;
        }
        return fetchCount;
    }

    private int inviteStatusValue;
    public void setInviteStatusValue(int val) {
        this.inviteStatusValue = val;
        this.inviteStatus = User.InviteStatus.valueOf(val);
    }

    public void setFetchCount(Boolean fetchCount) {
        this.fetchCount = fetchCount;
    }
    private List<Long> peopleIds;
    private long peopleId;
    private List<Long> peopleTypeList;
    private String filters;
    private List<Long> userIds;

    public Boolean getPortal() {
        return isPortal;
    }

    public void setPortal(Boolean portal) {
        isPortal = portal;
    }

    private Boolean isPortal;
    private long vendorId;
    private long tenantId;
    private long clientId;
    private String fileName;

    private String activeTab;

    public String addUser() throws Exception{

        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(user != null && user.getUser() != null) {

            if (StringUtils.isEmpty(user.getUser().getEmail())) {
                addFieldError("error", "Please enter a valid email");
                return ERROR;
            }
            AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
            if (appDomainObj == null) {
                throw new IllegalArgumentException("Invalid App Domain");
            }
            com.facilio.identity.client.dto.AppDomain appDomain = IdentityClient.getDefaultInstance().getAppDomainBean().getAppDomain(appDomainObj.getDomain());
            user.getUser().setOrgId(orgId);
            user.setApplicationId(appId);
            user.getUser().setUsername(user.getUser().getEmail());
            user.getUser().setAppDomain(appDomain);
            user.getUser().setInvitedBy(AccountUtil.getCurrentUser().getUid());
            if (user.getUser().getSecurityPolicyId() == -1) {
                user.getUser().setSecurityPolicyId(-99);
            }

            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.USER, user);
            context.put(FacilioConstants.ContextNames.SEND_INVITE, sendInvitation);
            context.put(FacilioConstants.ContextNames.PASSWORD, password);

            FacilioChain addUserChain = FacilioChainFactory.addUserChain();
            addUserChain.execute(context);
        }
        return SUCCESS;
    }

    public String updateUser() throws Exception{
        if(user != null && user.getUser() != null ) {
            user.setUid(user.getUser().getUid());
            user.setApplicationId(appId);
            if (user.getUser().getSecurityPolicyId() == -1) {
                user.getUser().setSecurityPolicyId(-99);
            }
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.USER, user);

            FacilioChain updateUserChain = FacilioChainFactory.updateUserChain();
            updateUserChain.execute(context);

            setResult(FacilioConstants.ContextNames.EMPLOYEES, context.get(FacilioConstants.ContextNames.RECORD_LIST));
        }
        return SUCCESS;
    }

    public String deleteUser() throws Exception {

        if(user != null) {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.APP_LINKNAME, appLinkName);
            context.put(FacilioConstants.ContextNames.USER_IDS, Collections.singletonList(user.getUid()));
            context.put(FacilioConstants.ContextNames.PEOPLE_IDS, Collections.singletonList(user.getPeopleId()));
            context.put(FacilioConstants.ContextNames.IS_PORTAL, getPortal());
            context.put(FacilioConstants.ContextNames.IS_PORTAL_ACCESS, false);

            FacilioChain deleteUserChain = FacilioChainFactory.deleteUserChain();
            deleteUserChain.execute(context);
        }
        return SUCCESS;

    }

    public String getUserList() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();

        AppDomain appDomainObj = null;
        if (SandboxAPI.isSandboxSubDomain(request.getServerName())) {
            appDomainObj = SandboxAPI.getProductionAppDomain(org, appId);
        } else {
            appDomainObj = ApplicationApi.getAppDomainForApplication(appId);;
        }
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        List<User>  iamUserList = IdentityClient.getDefaultInstance().getUserBean().getUserList(orgId,appDomainObj.getIdentifier(), search,  userStatus, inviteStatus,isUserVerified,withMFA,orderBy,  isAscending,  offset,5000);

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.IAM_USERS,iamUserList);
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
        context.put(FacilioConstants.ContextNames.ORGID,orgId);
        if (getFetchCount()) {
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
        } else {
            JSONObject pagination = new JSONObject();
            pagination.put("page", getPage());
            pagination.put("perPage", getPerPage());
            if (getPerPage() < 0) {
                pagination.put("perPage", 50);
            }
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }
        if(vendorId>0){
            context.put(FacilioConstants.ContextNames.VENDOR_ID,vendorId);
        }else if(tenantId > 0){
            context.put(FacilioConstants.ContextNames.TENANT_ID,tenantId);
        }else if(clientId>0){
            context.put(FacilioConstants.ContextNames.CLIENT_ID,clientId);
        }

        FacilioChain userListChain = ReadOnlyChainFactory.getUserListChain();
        userListChain.execute(context);

        if(!getFetchCount()) {
            setResult(FacilioConstants.ContextNames.USERS, context.get(FacilioConstants.ContextNames.USERS));
        }
        else {
            setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        }

        return SUCCESS;
    }

    public String addOrUpdatePortalUser() throws Exception{

        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(user != null && user.getUser() != null) {
            if (CollectionUtils.isNotEmpty(peopleIds)) {
                for (Long peopleId : peopleIds) {
                    user.getUser().setOrgId(orgId);
                    user.setApplicationId(appId);
                    user.setPeopleId(peopleId);
                    PeopleContext people = user.getPeople();
                    if(people!= null){
                        if((people.getId()<0)) {
                            people.setId(peopleId);
                        }
                    }
                    FacilioContext context = new FacilioContext();
                    context.put(FacilioConstants.ContextNames.USER, user);
                    context.put(FacilioConstants.ContextNames.SEND_INVITE, sendInvitation);
                    context.put(FacilioConstants.ContextNames.APP_LINKNAME, appLinkName);

                    FacilioChain addOrUpdatePortalUserChain = FacilioChainFactory.addOrUpdatePortalUserChain();
                    addOrUpdatePortalUserChain.execute(context);
                }
            }
        }
        return SUCCESS;
    }

    public String bulkDeleteUsers() throws Exception{
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ORGID,orgId);
        context.put(FacilioConstants.ContextNames.APP_LINKNAME,appLinkName);
        context.put(FacilioConstants.ContextNames.USER_IDS,userIds);
        context.put(FacilioConstants.ContextNames.PEOPLE_IDS,peopleIds);
        context.put(FacilioConstants.ContextNames.IS_PORTAL,getPortal());
        context.put(FacilioConstants.ContextNames.IS_PORTAL_ACCESS,false);
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            List<V3PeopleContext> peopleList = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.PEOPLE, peopleIds),PeopleContext.class),V3PeopleContext.class);
            context.put(FacilioConstants.ContextNames.RECORD_LIST,peopleList);
        }
        FacilioChain bulKDeleteUserChain = FacilioChainFactory.bulkUserDeleteChain();
        bulKDeleteUserChain.execute(context);

        return SUCCESS;

    }

    public String bulkInvite() throws Exception{

        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(user != null ) {
        if(CollectionUtils.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                user.setUser(IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, userId));
                com.facilio.identity.client.dto.AppDomain appDomainObj = ApplicationApi.getAppDomainForApp(user.getApplicationId());
                if (appDomainObj == null) {
                    throw new IllegalArgumentException("Invalid App Domain");
                }
                user.getUser().setAppDomain(appDomainObj);
                user.getUser().setInvitedBy(AccountUtil.getCurrentUser().getUid());

                IdentityClient.getDefaultInstance().getUserBean().inviteUser(orgId, user.getUser());
            }
        }
        }
        return SUCCESS;
    }

    public String revokeAppAccess() throws Exception {
        if(user != null ) {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
            context.put(FacilioConstants.ContextNames.APP_LINKNAME, appLinkName);
            context.put(FacilioConstants.ContextNames.USER_IDS, Collections.singletonList(user.getUid()));
            context.put(FacilioConstants.ContextNames.PEOPLE_IDS, Collections.singletonList(user.getPeopleId()));
            context.put(FacilioConstants.ContextNames.IS_PORTAL, getPortal());
            context.put(FacilioConstants.ContextNames.IS_PORTAL_ACCESS, false);

            FacilioChain revokeAppAccess = FacilioChainFactory.revokeAppAccessChain();
            revokeAppAccess.execute(context);
        }
        return SUCCESS;


    }


    public String bulkRevokeAppAccess() throws Exception{

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
        context.put(FacilioConstants.ContextNames.APP_LINKNAME,appLinkName);
        context.put(FacilioConstants.ContextNames.USER_IDS,userIds);
        context.put(FacilioConstants.ContextNames.PEOPLE_IDS,peopleIds);
        context.put(FacilioConstants.ContextNames.IS_PORTAL,getPortal());
        context.put(FacilioConstants.ContextNames.IS_PORTAL_ACCESS,false);

        FacilioChain bulkRevokeAppAccess = FacilioChainFactory.revokeAppAccessChain();
        bulkRevokeAppAccess.execute(context);
        return SUCCESS;

    }

    public String updateUserStatus() throws Exception {
        if(user != null ) {
            Organization org = AccountUtil.getCurrentOrg();
            long orgId = org.getOrgId();
            IdentityClient.getDefaultInstance().getUserBean().updateStatus(orgId, user.getUid(), userStatus);
        }
            return SUCCESS;
    }

    public String resendInvite() throws Exception {
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(user != null ) {
            user.setUser(IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, user.getUid()));
            Long appId = user.getApplicationId();
            if((appId == null || appId <= 0) && StringUtils.isNotEmpty(appLinkName)){
                 appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
            }

            com.facilio.identity.client.dto.AppDomain appDomainObj = ApplicationApi.getAppDomainForApp(appId);
            if (appDomainObj == null) {
                throw new IllegalArgumentException("Invalid App Domain");
            }

            user.getUser().setAppDomain(appDomainObj);
            user.getUser().setInvitedBy(AccountUtil.getCurrentUser().getUid());

            IdentityClient.getDefaultInstance().getUserBean().inviteUser(orgId, user.getUser());
        }
        return SUCCESS;
    }

    public String sendResetPasswordLink() throws Exception{
        if(user != null ) {
            IdentityClient.getDefaultInstance().getUserBean().sendResetPasswordLink(user.getUid());
        }
        return SUCCESS;
    }

    public String sendResetPasswordLinkUid() throws Exception{
        if(uid > 0) {
            IdentityClient.getDefaultInstance().getUserBean().sendResetPasswordLink(uid);
        }
        return SUCCESS;
    }
    public String getPeople() throws Exception{

        PeopleContext people = ApplicationUserUtil.checkIfExistsinPeople(email);
        Long ouid = null;
        if(user != null && user.getOrgUserId() >0){
            ouid = user.getOrgUserId();
        }
        if(people != null){
            PeopleAPI.setPeopleRelatedData(people,ouid);
        }
        setResult(FacilioConstants.ContextNames.PEOPLE, people);
        return SUCCESS;
    }

    public String getExistingUser() throws Exception{
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        User user = ApplicationUserUtil.checkIfAlreadyExistsInApp(orgId, email, appDomainObj.getIdentifier(),appId);
        setResult(FacilioConstants.ContextNames.USER, user);
        return SUCCESS;
    }

    public String getExistingIamUser() throws Exception{
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if((appId == null || appId <= 0) && StringUtils.isNotEmpty(appLinkName)){
            appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
        }
        AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        User user = IdentityClient.getDefaultInstance().getUserBean().getUser(orgId, email, appDomainObj.getIdentifier());
        setResult(FacilioConstants.ContextNames.USER, user);
        return SUCCESS;
    }

    public String activateOrDeactivateUsers() throws Exception{
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(CollectionUtils.isNotEmpty(userIds)) {
            for(Long userId : userIds){
                IdentityClient.getDefaultInstance().getUserBean().updateStatus(orgId, userId, userStatus);

            }
        }
        return SUCCESS;
    }

    public String updateSecurityPolicy() throws Exception{
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        if(CollectionUtils.isNotEmpty(userIds)) {
            for(Long userId : userIds) {
                IdentityClient.getDefaultInstance().getUserBean().updateSecurityPolicy(orgId,userId,securityPolicyId);
            }
        }
        return SUCCESS;

    }
    public String resetMFA() throws Exception{
        if( user != null) {
            IdentityClient.getDefaultInstance().getUserBean().deleteUserMFA(user.getUid());
        }
        return SUCCESS;
    }

    public String bulkResetMFA() throws Exception{
        if(CollectionUtils.isNotEmpty(userIds)){
            for(Long userId : userIds){
                IdentityClient.getDefaultInstance().getUserBean().deleteUserMFA(userId);
            }
        }
        return SUCCESS;
    }

    public String bulkResetPassword() throws Exception{
        if(CollectionUtils.isNotEmpty(userIds)) {
            for (Long userId : userIds) {
                IdentityClient.getDefaultInstance().getUserBean().sendResetPasswordLink(userId);
            }
        }

        return SUCCESS;
    }

    public String exportUserData() throws Exception {
        Organization org = AccountUtil.getCurrentOrg();
        long orgId = org.getOrgId();
        AppDomain appDomainObj = null;
        if(appId != null){
            appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
        }
        if(appDomainObj == null) {
            throw new IllegalArgumentException("Invalid App Domain");
        }
        List<User>  iamUserList = IdentityClient.getDefaultInstance().getUserBean().getUserList(orgId,appDomainObj.getIdentifier(), search,  userStatus, inviteStatus,isUserVerified,withMFA,orderBy,  isAscending,  offset,5000);
        List<Long> userIds = new ArrayList<>();
        for(User user: iamUserList){
            userIds.add(user.getUid());
        }

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.ORGID,orgId);
        context.put(FacilioConstants.ContextNames.APPLICATION_ID,appId);
        context.put(FacilioConstants.ContextNames.IAM_USERS,iamUserList);
        context.put(FacilioConstants.ContextNames.FILE_NAME,fileName);
        context.put(FacilioConstants.UserPeopleKeys.TAB_NAME,activeTab);
        if(vendorId>0){
            context.put(FacilioConstants.ContextNames.VENDOR_ID,vendorId);
        }else if(tenantId > 0){
            context.put(FacilioConstants.ContextNames.TENANT_ID,tenantId);
        }else if(clientId>0){
            context.put(FacilioConstants.ContextNames.CLIENT_ID,clientId);
        }

        FacilioChain exportUserListChain = ReadOnlyChainFactory.exportUserListChain();
        exportUserListChain.execute(context);

        setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

        return SUCCESS;
    }

    public String getUserFromPeopleId() throws Exception{
        FacilioModule module = ModuleFactory.getOrgUserModule();
        FacilioField userIdField = FieldFactory.getField("userId", "USERID", module, FieldType.NUMBER);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(userIdField);

        GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName());

        selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID","peopleId",String.valueOf(peopleId), NumberOperators.EQUALS));
        List<Map<String,Object>> props = selectRecordsBuilder.get();
        User user = null;
        if(CollectionUtils.isNotEmpty(props)){
            for(Map<String,Object> prop : props){
                long uid = (long)prop.get("userId");
                user =  ApplicationUserUtil.getUser(uid);
                if(user != null){
                    break;
                }
            }
        }
        setResult("user", user);
        if(user!=null){
            com.facilio.accounts.dto.User invitedBy = AccountUtil.getUserBean().getUser(-1, user.getOrgId(), user.getInvitedBy(), null);
            setResult("invitedBy",invitedBy);
        }
        return SUCCESS;
    }

}
