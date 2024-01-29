package com.facilio.report.module.v2.context;

import com.facilio.modules.AggregateOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V2ModuleMeasureContext {

    public long id;
    private String fieldName;
    private String moduleName;
    private String moduleDisplayName;
    private int sortOrder;
    private int limit;
    private boolean special;
    private AggregateOperator aggr;
    public String criteria;
    public long criteriaId;

    public int getAggr() {
        return aggr!= null ? aggr.getValue() : -1;
    }
    public void setAggr(int aggr) {
        this.aggr = AggregateOperator.getAggregateOperator(aggr);
    }
}
