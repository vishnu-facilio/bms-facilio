package com.facilio.db.criteria.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class LookupCriteriaUtil {

    public static String constructLookupQuery(FacilioField lookupField, FacilioField idField, FacilioModule lookupModule, Criteria criteria) {
        StringBuilder builder = new StringBuilder();
        builder.append(lookupField.getCompleteColumnName())
                .append(" IN (SELECT ")
                .append(idField.getCompleteColumnName())
                .append(" FROM ")
                .append(lookupModule.getTableName());

        FacilioModule currentMod = lookupModule.getExtendModule();
        while (currentMod != null) {
            builder.append(" INNER JOIN ")
                    .append(currentMod.getTableName())
                    .append(" ON (")
                    .append(lookupModule.getTableName()).append(".ID = ")
                    .append(currentMod.getTableName()).append(".ID) ");
            currentMod = currentMod.getExtendModule();
        }

        builder.append(" WHERE ")
                .append(lookupModule.getTableName()).append(".ORGID = ")
                .append(AccountUtil.getCurrentOrg().getId())
                .append(" AND (")
                .append(criteria.computeWhereClause())
                .append("))");

        return builder.toString();
    }

    public static String constructMultiLookupQuery(FacilioModule currentModule, FacilioModule relModule, FacilioModule lookupModule, FacilioField parentField, FacilioField childField, Criteria criteria,String joinColumnName) {
        StringBuilder builder = new StringBuilder();
        builder.append(FieldFactory.getIdField(currentModule).getCompleteColumnName())
                .append(" IN (SELECT ")
                .append(parentField.getCompleteColumnName())
                .append(" FROM ")
                .append(relModule.getTableName());

        builder.append(" INNER JOIN ")
                .append(lookupModule.getTableName())
                .append(" ON (")
                .append(lookupModule.getTableName())
                .append(".")
                .append(joinColumnName)
                .append(" = ")
                .append(childField.getCompleteColumnName())
                .append(")");
        FacilioModule currentMod = lookupModule.getExtendModule();
        while (currentMod != null) {
            builder.append(" INNER JOIN ")
                    .append(currentMod.getTableName())
                    .append(" ON (")
                    .append(lookupModule.getTableName()).append(".ID = ")
                    .append(currentMod.getTableName()).append(".ID) ");
            currentMod = currentMod.getExtendModule();
        }

        builder.append(" WHERE ")
                .append(lookupModule.getTableName())
                .append(".ORGID = ")
                .append(AccountUtil.getCurrentOrg().getId())
                .append(" AND ")
                .append(relModule.getTableName())
                .append(".MODULEID = ")
                .append(relModule.getModuleId())
                .append(" AND (")
                .append(criteria.computeWhereClause())
                .append("))");

        return builder.toString();
    }
}
