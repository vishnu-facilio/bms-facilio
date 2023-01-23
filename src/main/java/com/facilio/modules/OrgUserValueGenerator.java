package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public class OrgUserValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if(AccountUtil.getCurrentUser() != null) {
                return String.valueOf(AccountUtil.getCurrentUser().getOuid());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.CURRENT_ORG_USER_USER;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.OrgUserValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.USERS;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getOrgUserFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("orgUserId"),String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
        return criteria;
    }
}
