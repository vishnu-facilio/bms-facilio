package com.facilio.flowLog.moduleFlowLog.context;

import com.facilio.flowLog.moduleFlowLog.enums.FlowLogType;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
@Getter@Setter
public class FlowExecutionLogContext extends V3Context {
    private long id=-1l;
    private long flowExecutionId=-1l;
    private long blockId=-1l;
    private FlowLogType logType;
    private String logMessage;
    private long createdTime;
    public void setLogType(int logType) {this.logType = FlowLogType.valueOf(logType);}
    public void setLogType(FlowLogType logType) {this.logType = logType;}
    public Integer getLogType() {
        if(logType!=null){
            return logType.getIndex();
        }
        return -1;
    }
    public FlowLogType getLogTypeEnum() {return logType;}
    public JSONObject toJSON(){
        try{
            return FieldUtil.getAsJSON(this);
        }
        catch (Exception e){
            return null;
        }
    }
    public FlowExecutionLogContext(long blockId,FlowLogType logType, String logMessage){
        this.blockId = blockId;
        this.logType = logType;
        this.logMessage = logMessage;
    }
    public FlowExecutionLogContext(){
    }
}