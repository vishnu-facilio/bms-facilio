package com.facilio.db.criteria.operators;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public enum TeamOperator implements Operator<String> {
    CURRENT_PEOPLE_IN_TEAM(142, "My Teams") {
        @Override
        public String getWhereClause(String fieldName, String value) {
            return getTeamOperatorWhereClause(fieldName, value);
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            try {
                if (fieldName != null && !fieldName.isEmpty()) {
                    FacilioField teamField = getField(fieldName);
                    if (teamField != null) {
                        return PickListOperators.IS.getPredicate(teamField.getName(), getAccessibleTeamsForPeople());
                    }
                }
            } catch (Exception e) {
                log.error("Error while constructing where clause for team operator", e);
            }
            return null;
        }
    };

    private static String getAccessibleTeamsForPeople() throws Exception {
        if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getPeopleId() > -1) {
            SelectRecordsBuilder<Group> groupBuilder = new SelectRecordsBuilder<Group>()
                    .module(Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP))
                    .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleGroup.PEOPLE_GROUP))
                    .innerJoin("FacilioGroupMembers")
                    .on("FacilioGroupMembers.GROUPID = FacilioGroups.ID")
                    .beanClass(Group.class)
                    .andCondition(CriteriaAPI.getCondition("FacilioGroupMembers.PEOPLE_ID","peopleId", String.valueOf(AccountUtil.getCurrentUser().getPeopleId()),NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("FacilioGroups.SYS_DELETED","deleted","",CommonOperators.IS_EMPTY));
            List<Group> groups = groupBuilder.get();
            if(CollectionUtils.isNotEmpty(groups)) {
                List<Long> ids = groups.stream().map(Group::getId).collect(Collectors.toList());
                return StringUtils.join(ids, ",");
            }
        }
        return null;
    }
    private static String getTeamOperatorWhereClause(String fieldName, String value) {
        try {
            if (fieldName != null && !fieldName.isEmpty()) {
                FacilioField teamField = getField(fieldName);
                if (teamField != null) {
                    if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getPeopleId() > -1) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(teamField.getCompleteColumnName());
                        builder.append(" IN (");
                        builder.append("SELECT FacilioGroups.ID FROM FacilioGroupMembers INNER JOIN FacilioGroups ON FacilioGroups.ID = FacilioGroupMembers.GROUPID WHERE FacilioGroups.SYS_DELETED IS NULL AND PEOPLE_ID = ");
                        builder.append(AccountUtil.getCurrentUser().getPeopleId());
                        builder.append(")");
                        return builder.toString();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while constructing where clause for team operator", e);
        }
        return null;
    }

    private static FacilioField getField(String fieldName) throws Exception {
        if (fieldName != null && !fieldName.isEmpty()) {
            String[] module = fieldName.split("\\.");
            if (module.length > 1) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField field = modBean.getField(module[1], module[0]);
                return field;
            }
        }
        return null;
    }

    private static Logger log = LogManager.getLogger(PeopleOperator.class.getName());

    private TeamOperator(int operatorId, String operator) {
        this.operatorId = operatorId;
        this.operator = operator;
    }

    private int operatorId;
    private String operator;

    @Override
    public int getOperatorId() {
        return this.operatorId;
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public boolean isDynamicOperator() {
        return true;
    }

    @Override
    public boolean isValueNeeded() {
        return false;
    }

    @Override
    public boolean useFieldName() {
        return true;
    }

    @Override
    public boolean updateFieldNameWithModule() {
        return true;
    }

    @Override
    public List<Object> computeValues(String s) {
        return null;
    }
}
