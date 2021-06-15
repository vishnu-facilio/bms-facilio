package com.facilio.qa.rules.pojo;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.Operator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RuleCondition {
    private Long id;
    private Long ruleId;
    private Long criteriaId;
    private Criteria criteria;
    private Integer sequence;

    public Integer getOperator() {
        return operatorEnum == null ? null : operatorEnum.getOperatorId();
    }
    public void setOperator(Integer operator) {
        this.operatorEnum = operator == null ? null : Operator.getOperator(operator);
    }
    private String value;

    public boolean hasCriteria() {
        return criteriaId != null;
    }

    @JsonIgnore
    private Operator operatorEnum;

    public abstract boolean hasAction();
    public boolean actionIsEmpty() {
        return !hasAction();
    }
}
