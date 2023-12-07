package com.facilio.telemetry.beans;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.remotemonitoring.context.*;
import com.facilio.telemetry.context.TelemetryCriteriaCacheContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import lombok.NonNull;

import java.util.List;

public interface TelemetryCriteriaBean {
    TelemetryCriteriaCacheContext fetchTelemetryCriteria(long telemetryCriteriaId) throws Exception;
}