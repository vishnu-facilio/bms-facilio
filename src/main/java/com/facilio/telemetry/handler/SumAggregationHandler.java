package com.facilio.telemetry.handler;

import com.facilio.modules.*;
import com.facilio.ns.context.NameSpaceField;

public class SumAggregationHandler implements AggregationHandler {
    @Override
    public Double aggregate(NameSpaceField nameSpaceField) throws Exception {
        return TelemetryAggregationUtil.getAggregatedValue(nameSpaceField, BmsAggregateOperators.NumberAggregateOperator.SUM);
    }
}