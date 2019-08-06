package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRecommendedUsersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long woId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String roleName = (String) context.get(FacilioConstants.ContextNames.ROLE);

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
            assignedGroupId = workOrder.getAssignmentGroup().getId();
        }

        int numberOfRecommendedUsers = 3;

        if (StringUtils.isEmpty(roleName)) {
            roleName = "Technician";
        }

        Role role = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), roleName, false);
        List<User> usersWithRole = AccountUtil.getUserBean().getUsersWithRole(role.getRoleId());

        List<RecommendedUser> recommendedUsers = new ArrayList<>();

        Map<Long, User> usersMap = new HashMap<>();
        List<Long> userIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(usersWithRole)) {
            for (User user : usersWithRole) {
                usersMap.put(user.getId(), user);
                userIds.add(user.getId());
            }

            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getField("assignedTo", "ASSIGNED_TO_ID", FieldType.NUMBER));
            fields.add(FieldFactory.getField("count", "COUNT(*)", FieldType.NUMBER));
            SelectRecordsBuilder builder = new SelectRecordsBuilder()
                    .module(workOrderModule)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("ASSIGNED_TO_ID", "assignedTo", StringUtils.join(userIds, ","), NumberOperators.EQUALS))
                    .groupBy("ASSIGNED_TO_ID")
                    .orderBy("COUNT(*) ASC")
                    ;

            if (assignedGroupId > 0) {
                builder.andCondition(CriteriaAPI.getCondition("ASSIGNMENT_GROUP_ID", "assignedGroup", String.valueOf(assignedGroupId), NumberOperators.EQUALS));
            }

            List<Map<String, Object>> props = builder.getAsProps();

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

        context.put(FacilioConstants.ContextNames.RECOMMENDED_USERS, recommendedUsers);
        return false;
    }

    public static enum Type {
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
