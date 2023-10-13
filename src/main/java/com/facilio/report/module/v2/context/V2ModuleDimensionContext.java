package com.facilio.report.module.v2.context;

import com.facilio.modules.AggregateOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class V2ModuleDimensionContext {

    private String fieldName;
    private String moduleName;
    private String moduleDisplayName;
    private AggregateOperator aggr;
    private boolean special;
    private List<Long> selected_lookup_values;
    private String lookupFieldName;
    private String lookupModuleName;

    public int getAggr() {
        return aggr != null ? aggr.getValue() : -1;
    }

    public void setAggr(int aggr) {
        this.aggr = AggregateOperator.getAggregateOperator(aggr);
    }
}
