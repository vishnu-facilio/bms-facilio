package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetRecommendedUsersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long woId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String roleName = (String) context.get(FacilioConstants.ContextNames.ROLE);
        Integer numberOfRecommendedUsers = (Integer) context.get(FacilioConstants.ContextNames.RECOMMENDED_COUNT);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        WorkOrderContext workOrder = null;
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        if (woId != null && woId > 0) {
            SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
                    .module(workOrderModule)
                    .select(modBean.getAllFields(workOrderModule.getName()))
                    .beanClass(WorkOrderContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(woId, workOrderModule));
            workOrder = builder.fetchFirst();
        }

        long assignedGroupId = -1;
        if (workOrder != null) {
            if (workOrder.getAssignmentGroup() != null) {
                assignedGroupId = workOrder.getAssignmentGroup().getId();
            }
        }

        if (numberOfRecommendedUsers == null || numberOfRecommendedUsers < 0) {
            numberOfRecommendedUsers = 5;
        }

        if (StringUtils.isEmpty(roleName)) {
            roleName = "Technician";
        }

        Role role = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), roleName, false);

        List<User> usersWithRole = getUserList(role, assignedGroupId);

        List<RecommendedUser> recommendedUsers = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(usersWithRole)) {
            Map<Long, User> usersMap = new HashMap<>();
            List<Long> userIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(usersWithRole)) {
                for (User user : usersWithRole) {
                    usersMap.put(user.getId(), user);
                    userIds.add(user.getId());
                }

                List<Map<String, Object>> props = getOpenWorkOrderCount(userIds);

                if (CollectionUtils.isNotEmpty(props)) {
                    int count = 0;
                    for (Map<String, Object> prop : props) {
                        if ((count++) >= numberOfRecommendedUsers) {
                            break;
                        }
                        RecommendedUser recommendedUser = new RecommendedUser(usersMap.get(prop.get("assignedTo")), Type.TICKET_COUNT);
                        recommendedUser.addReason("no_of_tickets", prop.get("count"));
                        recommendedUsers.add(recommendedUser);
                    }
                }
            }
        }

        context.put(FacilioConstants.ContextNames.RECOMMENDED_USERS, recommendedUsers);
        return false;
    }

    private List<Map<String, Object>> getOpenWorkOrderCount(List<Long> userIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS_TYPE", "statusType", String.valueOf("1"), NumberOperators.EQUALS));

        LookupField statusField = new LookupField();
        statusField.setName("status");
        statusField.setColumnName("STATUS_ID");
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        statusField.setModule(workOrderModule);
        statusField.setDataType(FieldType.LOOKUP);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("assignedTo", "ASSIGNED_TO_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getField("count", "COUNT(*)", FieldType.NUMBER));
        SelectRecordsBuilder builder = new SelectRecordsBuilder()
                .module(workOrderModule)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ASSIGNED_TO_ID", "assignedTo", StringUtils.join(userIds, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(statusField, statusCriteria, LookupOperator.LOOKUP))
                .groupBy("ASSIGNED_TO_ID")
                .orderBy("COUNT(*) ASC");

        return builder.getAsProps();
    }

    private List<User> getUserList(Role role, long groupId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(role.getRoleId()), PickListOperators.IS));
        
        long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        
        GenericSelectRecordBuilder selectRecordBuilder = UserBeanImpl.fetchUserSelectBuilder(appId, criteria, AccountUtil.getCurrentOrg().getOrgId());

        if (groupId > 0) {
            selectRecordBuilder.innerJoin(AccountConstants.getGroupMemberModule().getTableName())
                    .on("GroupMembers.ORG_USERID = ORG_Users.ORG_USERID")
            ;
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("GROUPID", "group", String.valueOf(groupId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> userProps = selectRecordBuilder.get();
        List<User> usersWithRole = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userProps)) {
        	IAMUserUtil.setIAMUserPropsv3(userProps, AccountUtil.getCurrentOrg().getOrgId(), false);
            for (Map<String, Object> prop : userProps) {
                usersWithRole.add(UserBeanImpl.createUserFromProps(prop, false, false, null));
            }
        }
        return usersWithRole;
    }

    public enum Type {
        TICKET_COUNT
        ;
    }

    public static class RecommendedUser {
        private User user;
        private Map<String, Object> reason;
        private Type type;

        public RecommendedUser(User user, Type type) {
            this(user, null, type);
        }

        public RecommendedUser(User user, Map<String, Object> reason, Type type) {
            this.user = user;
            if (reason == null) {
                this.reason = new HashMap<>();
            }
            else {
                this.reason = reason;
            }
            this.type = type;
        }

        public void addReason(String key, Object value) {
            this.reason.put(key, value);
        }

        public User getUser() {
            return user;
        }

        public Map<String, Object> getReason() {
            return reason;
        }

        public Type getType() {
            return type;
        }
    }
}
