package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.telemetry.handler.*;
import lombok.Getter;

public enum AggregationType implements FacilioIntEnum {
    FIRST(FirstAggregationHandler.class),
    LAST(LastAggregationHandler.class),
    SUM(SumAggregationHandler.class),
    MAX(MaxAggregationHandler.class),
    MIN(MinAggregationHandler.class),
    AVG(AverageAggregationHandler.class),
    COUNT(CountAggregationHandler.class),
    LATEST(LastAggregationHandler.class),
    DISTINCT_COUNT(DistinctAggregationHandler.class);
    @Getter
    private Class<? extends AggregationHandler> handlerClass;

    AggregationType(Class<? extends AggregationHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public AggregationHandler getHandler() throws Exception {
        if (handlerClass != null) {
            return handlerClass.newInstance();
        }
        return null;
    }
    public static AggregationType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}
