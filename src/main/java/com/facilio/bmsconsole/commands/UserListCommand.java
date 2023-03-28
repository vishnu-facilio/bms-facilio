package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.*;

public class UserListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

            User currentUser = AccountUtil.getCurrentAccount().getUser();
            if(currentUser == null){
//                return null;
                return false;
            }
            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
             List<Long> defaultIds = (List<Long>) context.get(FacilioConstants.ContextNames.DEFAULT_IDS);
            Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
            String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Boolean inviteAcceptStatus = (Boolean)context.get(FacilioConstants.ContextNames.INVITE_ACCEPT_STATUS);

        List<FacilioField> fields = new ArrayList<>();
            fields.addAll(AccountConstants.getAppOrgUserFields());
            fields.add(AccountConstants.getApplicationIdField());
            fields.add(AccountConstants.getRoleIdField());

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table("ORG_Users")
                    .innerJoin("ORG_User_Apps")
                    .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                    .innerJoin("People")
                    .on("People.ID = ORG_Users.PEOPLE_ID");

            if(CollectionUtils.isNotEmpty(defaultIds)){
                String defaultIdAndOrderBy = MessageFormat.format("FIELD ( ORG_Users.ORG_USERID, {0} ) DESC", StringUtils.join(defaultIds,","));
                selectBuilder.orderBy(defaultIdAndOrderBy);
            }
            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));

            if (filterCriteria != null && !filterCriteria.isEmpty()) {
                selectBuilder.andCriteria(filterCriteria);
            }
            if(!StringUtils.isEmpty(search))
            {
                selectBuilder.andCriteria(getUserSearchCriteria(search));
            }

            if(inviteAcceptStatus != null)
            {
                selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.INVITATION_ACCEPT_STATUS","inviteAcceptStatus", String.valueOf(inviteAcceptStatus), BooleanOperators.IS));
            }

        if(appId > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));
        }

            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
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
            if (props != null && !props.isEmpty()) {
                List<User> users = new ArrayList<>();
                IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
                AppDomain appDomain = null;
                if(appId > 0) {
                    appDomain = ApplicationApi.getAppDomainForApplication((long) props.get(0).get("applicationId"));
                }
                RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean", orgId);

                List<Role> roles = roleBean.getRolesForApps(appId > 0 ? Collections.singletonList(appId) : null);
                List<ScopingContext> scopingList = ApplicationApi.getAllScoping();

                Map<Long, Role> roleMap = new HashMap<>();
                for(Role role : roles){
                    roleMap.put(role.getId(), role);
                }

                Map<Long, ScopingContext> scopingMap = new HashMap<>();
                if(CollectionUtils.isNotEmpty(scopingList)) {
                    for (ScopingContext scoping : scopingList) {
                        scopingMap.put(scoping.getId(), scoping);
                    }
                }

                Map<Long, List<Long>> accessibleSpaceListMap = UserBeanImpl.getAllUsersAccessibleSpaceList();
                Map<Long, List<Long>> accessibleGroupListMap = UserBeanImpl.getAllUsersAccessibleGroupList();

                for(Map<String, Object> prop : props) {
                    User user = FieldUtil.getAsBeanFromMap(prop, User.class);
                    user.setId((long)prop.get("ouid"));
                    if(prop.get("applicationId") != null){
                        if(appDomain != null) {
                            user.setAppType(appDomain.getAppType());
                            user.setAppDomain(appDomain);
                            user.setAppType(appDomain.getAppType());
                        }
                        user.setApplicationId((long)prop.get("applicationId"));
                        if(user.getScopingId() != null && user.getScopingId() > 0){
                            user.setScoping(scopingMap.get(user.getScopingId()));
                        }
                    }
                    if(user.getRoleId() > 0){
                        user.setRole(roleMap.get(user.getRoleId()));
                    }
                    if(accessibleSpaceListMap != null) {
                        user.setAccessibleSpace(accessibleSpaceListMap.get(user.getOuid()));
                    }
                    if(accessibleGroupListMap != null) {
                        user.setGroups(accessibleGroupListMap.get(user.getOuid()));
                    }
                    users.add(user);
                }
                context.put(FacilioConstants.ContextNames.USERS,users);
            }


        return false;
    }
    private Criteria getUserSearchCriteria(String search)
    {
        Criteria criteria = new Criteria();
        Condition condition_name = new Condition();
        condition_name.setColumnName("People.Name");
        condition_name.setFieldName("name");
        condition_name.setOperator(StringOperators.CONTAINS);
        condition_name.setValue(search);
        criteria.addOrCondition(condition_name);

        Condition condition_email = new Condition();
        condition_email.setColumnName("People.EMAIL");
        condition_email.setFieldName("email");
        condition_email.setOperator(StringOperators.CONTAINS);
        condition_email.setValue(search);
        criteria.addOrCondition(condition_email);


        return criteria;
    }
}
