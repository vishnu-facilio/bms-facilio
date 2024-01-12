package com.facilio.report.module.v2.context;

import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2ModuleTimeFilterContext
{
    private String fieldName;
    private String moduleName;
    private long startTime;
    private long endTime;
    public int operatorId;
    private DateOperators dateOperators;
    private String label;
    private Long offset;
    public int getDateOperators() {
        return dateOperators != null ? dateOperators.getOperatorId() : -1;
    }

    public void setDateOperators(int dateOperators) {
        this.dateOperators = (DateOperators) Operator.getOperator(dateOperators);
    }
}
