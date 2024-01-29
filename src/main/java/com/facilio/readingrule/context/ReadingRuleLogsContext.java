package com.facilio.readingrule.context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingRuleLogsContext extends V3Context {
    private NewReadingRuleContext rule;
    private Long ruleExecTime;
    private String resourceName;
    private Boolean isImpactLog;
    private Boolean ruleResult;
    private Double costImpact;
    private Double energyImpact;
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
