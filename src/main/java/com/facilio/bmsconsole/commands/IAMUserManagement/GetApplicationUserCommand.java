package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import java.util.*;
import java.util.stream.Collectors;

public class GetApplicationUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<User> iamUserList = (List<User>) context.get(FacilioConstants.ContextNames.IAM_USERS);
        Boolean getCount = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
        Long vendorId = (Long) context.getOrDefault(FacilioConstants.ContextNames.VENDOR_ID,0L);
        Long tenantId = (Long) context.getOrDefault(FacilioConstants.ContextNames.TENANT_ID,0L);
        Long clientId = (Long) context.getOrDefault(FacilioConstants.ContextNames.CLIENT_ID,0L);
        List<Long> userIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(iamUserList)) {
            userIds = iamUserList.stream().map(User::getUid).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(userIds)) {
            long orgId = (long) context.get(FacilioConstants.ContextNames.ORGID);
            long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
            String linkName = null;
            if(appId > 0){
                ApplicationContext application = ApplicationApi.getApplicationForId(appId);
                linkName = application.getLinkName();
            }
            context.put(FacilioConstants.ContextNames.APP_LINKNAME,linkName);

            if (getCount) {
                Long count = getApplicationUserCount(orgId, appId, userIds,vendorId,tenantId,clientId,linkName);
                context.put(FacilioConstants.ContextNames.COUNT, count);
            }
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            context.put(FacilioConstants.ContextNames.USERS, getApplicationUserList(orgId, appId, userIds, pagination, iamUserList,vendorId,tenantId,clientId,linkName));

        }
        return false;
    }
    private static GenericSelectRecordBuilder getSelectBuilderForUser(Long orgId,Long appId,List<Long> userIds,Long vendorId,Long tenantId,Long clientId, String linkName) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table("ORG_Users")
                .innerJoin("ORG_User_Apps")
                .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                .innerJoin("People")
                .on("People.ID = ORG_Users.PEOPLE_ID");

        if(linkName.equals("vendor")){
            selectBuilder.innerJoin(ModuleFactory.getVendorContactModule().getTableName())
                    .on("Vendor_Contacts.ID=People.ID")
                    .innerJoin(ModuleFactory.getVendorsModule().getTableName())
                    .on("Vendors.ID=Vendor_Contacts.VENDOR_ID");
            if(vendorId>0){
                selectBuilder.andCondition(CriteriaAPI.getCondition("Vendor_Contacts.VENDOR_ID","vendorId",vendorId+"",NumberOperators.EQUALS));
            }
        }else if(linkName.equals("tenant")){
            selectBuilder.innerJoin(ModuleFactory.getTenantContactModule().getTableName())
                    .on("Tenant_Contacts.ID=People.ID")
                    .innerJoin(ModuleFactory.getTenantsModule().getTableName())
                    .on("Tenants.ID=Tenant_Contacts.TENANT_ID");
            if(tenantId>0){
                selectBuilder.andCondition(CriteriaAPI.getCondition("Tenant_Contacts.TENANT_ID","tenantId",tenantId+"",NumberOperators.EQUALS));
            }
        }else if(linkName.equals("client")){
            selectBuilder.innerJoin(ModuleFactory.getClientContactModule().getTableName())
                    .on("Client_Contacts.ID=People.ID")
                    .innerJoin(ModuleFactory.getClientModule().getTableName())
                    .on("Clients.ID=Client_Contacts.CLIENT_ID");
            if(clientId > 0){
                selectBuilder.andCondition(CriteriaAPI.getCondition("Client_Contacts.CLIENT_ID","clientId",clientId+"",NumberOperators.EQUALS));
            }
        }

        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "uid", StringUtils.join(userIds, ","), NumberOperators.EQUALS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

        if (appId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID","applicationId", String.valueOf(appId), NumberOperators.EQUALS));
        }

        return selectBuilder;
    }

    public static List<PeopleUserContext> getApplicationUserList(Long orgId,Long appId, List<Long> userIds, JSONObject pagination,List<User> iamuserList,Long vendorId,Long tenantId,Long clientId, String linkName) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForUser(orgId,appId,userIds,vendorId,tenantId,clientId,linkName);

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getAppOrgUserFields());
        fields.add(AccountConstants.getApplicationIdField());
        fields.add(AccountConstants.getRoleIdField());
        fields.add(FieldFactory.getIdField(AccountConstants.getOrgUserAppsModule()));

        if(linkName.equals("vendor")){
            fields.add(FieldFactory.getField("vendorName","Vendors.NAME",FieldType.STRING));
        }else if(linkName.equals("tenant")){
            fields.add(FieldFactory.getField("tenantName","Tenants.NAME",FieldType.STRING));
        }else if(linkName.equals("client")){
            fields.add(FieldFactory.getField("clientName","Clients.NAME",FieldType.STRING));
        }

        selectBuilder.select(fields);

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");
            if(page <= 0){
                page = 1;
            }
            if (perPage == -1) {
                perPage = 50;
            }
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            selectBuilder.offset(offset);
            selectBuilder.limit(perPage);

        }

        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<PeopleUserContext> users = new ArrayList<>();
            RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean", orgId);

            List<Role> roles = roleBean.getRolesForApps(appId > 0 ? Collections.singletonList(appId) : null);
            Map<Long, Role> roleMap = new HashMap<>();
            for (Role role : roles) {
                roleMap.put(role.getId(), role);
            }

            for (Map<String, Object> prop : props) {
                PeopleUserContext user = FieldUtil.getAsBeanFromMap(prop, PeopleUserContext.class);
                user.setOrgUserId((long) prop.get("ouid"));
                user.setVendorName((String)prop.get("vendorName"));
                user.setTenantName((String)prop.get("tenantName"));
                user.setClientName((String)prop.get("clientName"));

                if (user.getRoleId() > 0) {
                    user.setRole(roleMap.get(user.getRoleId()));
                }
                if (CollectionUtils.isNotEmpty(iamuserList)) {
                    for (User iamUser : iamuserList) {
                        if (iamUser != null && user.getUid() == iamUser.getUid()) {
                            user.setUser(iamUser);
                        }
                    }
                }
                users.add(user);
            }
            return users;
        }
        return null;
    }
    public static Long getApplicationUserCount(Long orgId,Long appId,List<Long> userIds,Long vendorId,Long tenantId,Long clientId,String linkName) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForUser(orgId,appId,userIds,vendorId,tenantId,clientId,linkName);
        FacilioField id = FieldFactory.getIdField(AccountConstants.getOrgUserAppsModule());
        selectBuilder
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, id);

        List<Map<String, Object>> props = selectBuilder.get();
        if (props.size() > 0) {
            return (long) props.get(0).get("id");
        }
        return null;
    }

}
