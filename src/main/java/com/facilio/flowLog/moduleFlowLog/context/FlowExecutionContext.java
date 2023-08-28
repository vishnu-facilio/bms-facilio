package com.facilio.flowLog.moduleFlowLog.context;

import com.facilio.flowLog.moduleFlowLog.enums.FlowStatus;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
@Getter@Setter
public class FlowExecutionContext extends V3Context {
    private long id=-1l;
    private long flowId;
    private long recordId;
    private long recordModuleId;
    private FlowStatus status;
    private long startTime;
    private long endTime;
    private String threadName;
    public void setStatus(FlowStatus status) {
        this.status = status;
    }
    public void setStatus(Integer status) {
        this.status = FlowStatus.valueOf(status);
    }
    public Integer getStatus() {
        if(status!=null){
            return status.getIndex();
        }
        return -1;
    }
    public FlowStatus getStatusEnum(){
        return status;
    }
}