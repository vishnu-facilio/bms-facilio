package com.facilio.flowLog.moduleFlowLog.context;

import com.facilio.flowLog.moduleFlowLog.enums.FlowStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
@Getter@Setter
public class FlowLogContext extends V3Context {
    private long id=-1l;
    private long flowId;
    private long recordId;
    private long recordModuleId;
    private long blockId;
    private FlowStatus status;
    private long startTime;
    private long endTime;

    public int getStatus() {
        if(status!=null){
            return status.getIndex();
        }
        return -1;
    }

    public void setStatus(Integer status) {
        this.status = FlowStatus.valueOf(status);
    }

    public void setStatusEnum(FlowStatus status) {
        this.status = status;
    }

    public FlowLogContext(){

    }

    public void setLogMessage(String logMessage) {
        if(StringUtils.isNotEmpty(logMessage) && logMessage.length() >65535){
            logMessage = logMessage.substring(0,65534);
        }
        this.logMessage = logMessage;
    }

    private String threadName;
    private String logMessage;
    public FlowStatus getStatusEnum(){
        return status;
    }
    public JSONObject toJSON(){
        try {
            return FieldUtil.getAsJSON(this);
        }catch (Exception e){

        }
        return null;
    }
}