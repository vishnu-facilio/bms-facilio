package com.facilio.readingkpi.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiResourceLoggerContext extends V3Context {
    private Long kpiId;
    private Long vmId;
    private Long parentLoggerId;
    private Long resourceId;
    // startTime is either the history start time or normal execution's start time (example last monday morning 00:00 for weekly exec)
    // this is to know for which time range the kpi is being executed
    private Long startTime;
    private Long endTime;
    private Long calculationStartTime;
    private Long calculationEndTime;
    private Integer status;
    private String message;
    private Boolean isHistorical;

    public KpiResourceLoggerContext(Long kpiId, Long parentLoggerId, Long resourceId, Long startTime, Long endTime, Long calculationStartTime, Integer status, Boolean isHistorical) {
        this.kpiId = kpiId;
        this.parentLoggerId = parentLoggerId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.calculationStartTime = calculationStartTime;
        this.status = status;
        this.isHistorical = isHistorical;
    }

    public KpiResourceLoggerContext() {

    }

    @Getter
    public enum KpiLoggerStatus implements FacilioIntEnum {
        IN_PROGRESS("In Progress"),
        SUCCESS("Success"),
        FAILED("Failed"),
        COMPLETED("Completed");

        private String name;

        KpiLoggerStatus(String name) {
            this.name = name;
        }

        public static KpiLoggerStatus valueOf(int idx) {
            if (idx > 0 && idx <= values().length) {
                return values()[idx - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return this.name;
        }

    }
}
