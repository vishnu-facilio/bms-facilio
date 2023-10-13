package com.facilio.report.module.v2.context;

import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.AggregateOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2ModuleGroupByContext {

    private String fieldName;
    private String moduleName;
    private String moduleDisplayName;
    private boolean special;
    private String lookupFieldName;
    private String lookupModuleName;
    private AggregateOperator aggr;

    public int getAggr() {
        return aggr != null ? aggr.getValue() : -1;
    }

    public void setAggr(int aggr) {
        this.aggr = (AggregateOperator) Operator.getOperator(aggr);
    }
}
