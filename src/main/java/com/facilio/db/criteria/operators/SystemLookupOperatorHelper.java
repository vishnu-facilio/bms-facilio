package com.facilio.db.criteria.operators;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.modules.fields.BaseSystemLookupField;
import com.facilio.modules.fields.FacilioField;

import java.util.Map;

public class SystemLookupOperatorHelper {
    private static void addDefaultCriteria (Criteria criteria, BaseSystemLookupField systemLookupField, Map<String, FacilioField> lookupRecordFieldMap) {
        criteria.addAndCondition(CriteriaAPI.getCondition(lookupRecordFieldMap.get("fieldId"), String.valueOf(systemLookupField.getFieldId()), NumberOperators.EQUALS));
    }

    public static String computeWhereClause (Criteria criteria, BaseSystemLookupField systemLookupField, Map<String, FacilioField> lookupRecordFieldMap) {
        addDefaultCriteria(criteria, systemLookupField, lookupRecordFieldMap);
        String lookupRecordTableName = systemLookupField.getLookupModule().getTableName();
        StringBuilder builder = new StringBuilder();
        builder.append(systemLookupField.getTableName()).append(".ID IN ( SELECT ")
                .append(lookupRecordFieldMap.get("parentId").getCompleteColumnName())
                .append(" FROM ")
                .append(lookupRecordTableName)
                .append(" WHERE ")
                .append(lookupRecordTableName).append(".ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND ")
                .append(lookupRecordTableName).append(".MODULEID = ").append(systemLookupField.getLookupModule().getModuleId()).append(" AND ")
                .append(criteria.computeWhereClause())
                .append(")");

        return builder.toString();
    }

    public static FacilioModulePredicate computePredicate (Criteria criteria, BaseSystemLookupField systemLookupField, Map<String, FacilioField> lookupRecordFieldMap) {
        return new FacilioModulePredicate(systemLookupField.getName(), criteria.computePredicate());
    }
}
