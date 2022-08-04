package com.facilio.modules;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;

public class JoinContext {

    public JoinContext(FacilioModule joinModule, FacilioField parentTableField, FacilioField joinField, JoinType joinType) {
        this.joinModule = joinModule;
        this.parentTableField = parentTableField;
        this.joinField = joinField;
        this.joinType = joinType;
    }

    private FacilioModule joinModule;
    private FacilioField parentTableField;
    private FacilioField joinField;
    private JoinType joinType;
    private Criteria criteria;

    public FacilioModule getJoinModule() {
        return joinModule;
    }
    public void setJoinModule(FacilioModule joinModule) {
        this.joinModule = joinModule;
    }

    public FacilioField getParentTableField() {
        return parentTableField;
    }
    public void setParentTableField(FacilioField parentTableField) {
        this.parentTableField = parentTableField;
    }

    public FacilioField getJoinField() {
        return joinField;
    }
    public void setJoinField(FacilioField joinField) {
        this.joinField = joinField;
    }

    public JoinType getJoinType() {
        return joinType;
    }
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public Criteria getCriteria() {
        return criteria;
    }
     public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public static enum JoinType implements FacilioIntEnum {
        INNER_JOIN,
        LEFT_JOIN,
        RIGHT_JOIN;

        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

    }

}
