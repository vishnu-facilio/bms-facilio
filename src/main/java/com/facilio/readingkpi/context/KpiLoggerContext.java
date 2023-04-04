package com.facilio.readingkpi.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class KpiLoggerContext extends V3Context {
    private ReadingKPIContext kpi ;
    private Integer kpiType;
    private Integer status;
    private Boolean isSysCreated;
    private Long startTime;
    private Long endTime;
    private Long execStartTime;
    private Long execEndTime;
    private Integer resourceCount;
    private Integer successCount;

    private List<Map<String,String>> resourceList;

    public KpiLoggerContext(ReadingKPIContext kpi, Integer kpiType, Integer status, Boolean isSysCreated, Long execStartTime, Integer resourceCount) {
        this.kpi = kpi;
        this.kpiType = kpiType;
        this.status = status;
        this.isSysCreated = isSysCreated;
        this.execStartTime = execStartTime;
        this.resourceCount = resourceCount;
    }

    public KpiLoggerContext() {

    }

    public KpiResourceLoggerContext.KpiLoggerStatus getStatusEnum(Integer index){
        return KpiResourceLoggerContext.KpiLoggerStatus.valueOf(index);
    }

    public KPIType getKpiTypeEnum(Integer index){
        return KPIType.valueOf(index);
    }
}
