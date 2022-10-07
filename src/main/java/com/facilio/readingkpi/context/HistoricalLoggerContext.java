package com.facilio.readingkpi.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoricalLoggerContext extends V3Context {
    private Long kpiId;
    private Long resourceId;
    private Long startTime;
    private Long endTime;
    private Long calculationStartTime;
    private Long calculationEndTime;
    private Long createdBy;
    private Long createdTime;
    private Integer status;
    private String message;
    private Boolean isHistorical;

    public enum LoggerStatus implements FacilioIntEnum {
        IN_PROGRESS,
        SUCCESS,
        FAILED;

        public static LoggerStatus valueOf(int idx) {
            if (idx > 0 && idx <= values().length) {
                return values()[idx - 1];
            }
            return null;
        }
    }
}
