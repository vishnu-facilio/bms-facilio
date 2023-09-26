package com.facilio.analytics.v2.context;

import com.facilio.modules.AggregateOperator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V2AnalyticsGroupByContext {

    private AggregateOperator time_aggr;

    public void setTime_aggr(int time_aggr) {
        this.time_aggr = AggregateOperator.getAggregateOperator(time_aggr);
    }
    public int getTime_aggr() {
        return this.time_aggr != null ? this.time_aggr.getValue() : -1 ;
    }
    public AggregateOperator getTime_aggrEnum() {
        return this.time_aggr != null ? this.time_aggr : null ;
    }
}
