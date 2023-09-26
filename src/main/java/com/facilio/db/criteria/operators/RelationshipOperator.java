package com.facilio.db.criteria.operators;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RelationshipOperator implements Operator<String>{
    CONTAINS_RELATION(109, "contains relation") {

        @Override @SneakyThrows
        public String getWhereClause(String relationName, String value) {
            return getCondition(relationName, true, true, value);
        }

        @Override
        public FacilioModulePredicate getPredicate(String s, String value) {
            return null;
        }

        @Override
        public boolean isDynamicOperator() {
            return false;
        }

        public boolean isValueNeeded() {
            return true;
        }

        @Override
        public List<Object> computeValues(String value) {
            return null;
        }
    },
    NOT_CONTAINS_RELATION(110, "not contains relation") {

        @Override @SneakyThrows
        public String getWhereClause(String relationName, String value) {
            return getCondition(relationName, false, true, value);
        }

        @Override
        public FacilioModulePredicate getPredicate(String s, String value) {
            return null;
        }

        @Override
        public boolean isDynamicOperator() {
            return false;
        }

        public boolean isValueNeeded() {
            return true;
        }

        @Override
        public List<Object> computeValues(String value) {
            return null;
        }
    },
    RELATED(111, "related") {

        @Override @SneakyThrows
        public String getWhereClause(String relationName, String value) {
            return getCondition(relationName, true, false, null);
        }

        @Override
        public FacilioModulePredicate getPredicate(String s, String value) {
            return null;
        }

        @Override
        public boolean isDynamicOperator() {
            return false;
        }

        public boolean isValueNeeded() {
            return false;
        }

        @Override
        public List<Object> computeValues(String value) {
            return null;
        }
    },
    NOT_RELATED(112, "not related") {

        @Override @SneakyThrows
        public String getWhereClause(String relationName, String value) {
            return getCondition(relationName, false, false, null);
        }

        @Override
        public FacilioModulePredicate getPredicate(String s, String value) {
            return null;
        }

        @Override
        public boolean isDynamicOperator() {
            return false;
        }

        public boolean isValueNeeded() {
            return false;
        }

        @Override
        public List<Object> computeValues(String value) {
            return null;
        }
    };

    private static String getCondition(String relationName, boolean havingRelation, boolean withValue, String value) throws Exception{
        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationName);
        RelationContext relation = RelationUtil.getRelation(relationMapping.getRelationId(), false);

        FacilioField idField = FieldFactory.getIdField(relationMapping.getFromModule());

        StringBuilder builder = new StringBuilder();
        builder.append(idField.getCompleteColumnName());
        if (!havingRelation) {
            builder.append(" NOT ");
        }
        builder.append(" IN (SELECT ")
                .append(relationMapping.getPositionEnum().getColumnName()).append(" FROM ")
                .append(relation.getRelationModule().getTableName())
                .append(" WHERE ")
                .append(relation.getRelationModule().getTableName()).append(".ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND ")
                .append(relation.getRelationModule().getTableName()).append(".MODULEID = ").append(relation.getRelationModuleId());
        if(withValue) {
            builder.append(" AND ").append(relationMapping.getReversePosition().getColumnName()).append(" IN (").append(value).append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public boolean useFieldName() {
        return true;
    }

    private static Logger log = LogManager.getLogger(RelationshipOperator.class.getName());

    private RelationshipOperator(int operatorId, String operator) {
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

    private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
    private static Map<String, Operator> initOperatorMap() {
        Map<String, Operator> operatorMap = new HashMap<>();
        for(Operator operator : values()) {
            operatorMap.put(operator.getOperator(), operator);
        }
        return operatorMap;
    }
    public static Map<String, Operator> getAllOperators() {
        return operatorMap;
    }
}
