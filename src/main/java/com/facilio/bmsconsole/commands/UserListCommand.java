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
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
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
            List<Long> defaultIds = new ArrayList<>();
            Criteria filterCriteria = new Criteria();
            if(context.get(FacilioConstants.ContextNames.DEFAULT_IDS) !=null) {
                defaultIds = (List<Long>) context.get(FacilioConstants.ContextNames.DEFAULT_IDS);
            }
            if(context.get(FacilioConstants.ContextNames.FILTER_CRITERIA) !=null) {
                filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
            }
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
            if (filterCriteria != null){
            selectBuilder.andCriteria(filterCriteria);
            }

            selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
            selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("applicationId"), String.valueOf(appId), NumberOperators.EQUALS));

            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");
                if (perPage == -1) {
                    perPage = 50;
                }
                    int offset = ((page - 1) * perPage);
                    if (offset < 0) {
                        offset = 0;

                selectBuilder.offset(offset);
                selectBuilder.limit(perPage);
            }
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
                List<ScopingContext> scopingList = ApplicationApi.getScopingForApp(appId);

                Map<Long, Role> roleMap = new HashMap<>();
                for(Role role : roles){
                    roleMap.put(role.getId(), role);
                }

                Map<Long, ScopingContext> scopingMap = new HashMap<>();
                for(ScopingContext scoping : scopingList){
                    scopingMap.put(scoping.getId(), scoping);
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
                        if(user.getScopingId() > 0){
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
}
