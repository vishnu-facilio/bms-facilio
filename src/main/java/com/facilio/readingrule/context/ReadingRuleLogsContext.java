package com.facilio.readingrule.context;

import com.facilio.ns.context.AggregationType;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingRuleLogsContext extends V3Context {
    private String ruleName;
    private Long ruleExecTime;
    private String resourceName;
    private Boolean ruleResult;
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
