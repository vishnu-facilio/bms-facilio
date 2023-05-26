package com.facilio.bmsconsole.commands.IAMUserManagement;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.context.PeopleUserContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.identity.client.dto.User;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
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
        List<Long> userIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(iamUserList)) {
            userIds = iamUserList.stream().map(User::getUid).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(userIds)) {
            long orgId = (long) context.get(FacilioConstants.ContextNames.ORGID);
            long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);

            if (getCount) {
                Long count = getApplicationUserCount(orgId, appId, userIds);
                context.put(FacilioConstants.ContextNames.COUNT, count);
            }
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            context.put(FacilioConstants.ContextNames.USERS, getApplicationUserList(orgId, appId, userIds, pagination, iamUserList));

        }
        return false;
    }
    private static GenericSelectRecordBuilder getSelectBuilderForUser(Long orgId,Long appId,List<Long> userIds) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table("ORG_Users")
                .innerJoin("ORG_User_Apps")
                .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                .innerJoin("People")
                .on("People.ID = ORG_Users.PEOPLE_ID");

        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "uid", StringUtils.join(userIds, ","), NumberOperators.EQUALS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
        if (appId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID","applicationId", String.valueOf(appId), NumberOperators.EQUALS));
        }
        return selectBuilder;
    }

    public static List<PeopleUserContext> getApplicationUserList(Long orgId,Long appId, List<Long> userIds, JSONObject pagination,List<User> iamuserList) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForUser(orgId,appId,userIds);

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(AccountConstants.getAppOrgUserFields());
        fields.add(AccountConstants.getApplicationIdField());
        fields.add(AccountConstants.getRoleIdField());
        fields.add(FieldFactory.getIdField(AccountConstants.getOrgUserAppsModule()));

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
    public static Long getApplicationUserCount(Long orgId,Long appId,List<Long> userIds) throws Exception {
        GenericSelectRecordBuilder selectBuilder = getSelectBuilderForUser(orgId,appId,userIds);
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
