package com.facilio.telemetry.handler;

import com.facilio.ns.context.NameSpaceField;
import com.facilio.remotemonitoring.context.RawAlarmContext;

public interface AggregationHandler {
    Double aggregate(NameSpaceField nameSpaceField) throws Exception;
}
