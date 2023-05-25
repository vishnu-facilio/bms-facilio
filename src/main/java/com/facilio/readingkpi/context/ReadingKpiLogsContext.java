package com.facilio.readingkpi.context;

import com.facilio.readingrule.context.ExecutionStatus;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingKpiLogsContext extends V3Context {
    private String kpiName;
    private Long kpiExecTime;
    private String resourceName;
    private Double kpiResult;
    private ExecutionStatus status;
    private Long duration;
    private String varInfo;
    private String errorCode;
    public String getExecutionStatus() {
        if(status != null) {
            return status.getValue();
        }
        return null;
    }
    public void setStatus(int executionStatus) {
        this.status = ExecutionStatus.valueOf(executionStatus);
    }
    public int getStatus() {
        if(status != null) {
            return status.getIndex();
        }
        return -1;
    }
}