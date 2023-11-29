package com.facilio.flowLog.moduleFlowLog.service;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.flowLog.FlowLogService;
import com.facilio.flowLog.moduleFlowLog.context.FlowLogContext;
import com.facilio.flowLog.moduleFlowLog.enums.FlowStatus;
import com.facilio.flowengine.executor.FlowEngineInterFace;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.FlowLogHandler;
import org.json.simple.JSONObject;

public class ModuleFlowLogServiceImpl extends FlowLogService {
    private FlowLogContext flowLogContext = new FlowLogContext();
    public ModuleFlowLogServiceImpl(FlowEngineInterFace flowEngineInterFace) {
        super(flowEngineInterFace);
    }
    @Override
    public void log(String message) {
        flowLogContext.setLogMessage(message);
        flowLogContext.setFlowId(flowEngineInterFace.getFlow().getId());
        long recordId =-1l,blockId=-1l;
        JSONObject record = flowEngineInterFace.getCurrentRecord();
        if(record!=null && record.containsKey("id")){
            recordId = (Long) record.get("id");
        }
        if(flowEngineInterFace.getCurrentExecutionBlock()!=null){
            blockId = flowEngineInterFace.getCurrentExecutionBlock().getId();
        }
        flowLogContext.setRecordId(recordId);
        flowLogContext.setModuleId(flowEngineInterFace.getFlow().getModuleId());
        flowLogContext.setBlockId(blockId);
        flowLogContext.setThreadName(Thread.currentThread().getName());
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(FlowLogHandler.KEY+"/"+orgId+"/"+flowEngineInterFace.getFlow().getId())
                .setOrgId(orgId)
                .setContent(flowLogContext.toJSON())
        );
    }
    @Override
    public void onFlowError() {
        flowLogContext.setEndTime(System.currentTimeMillis());
        flowLogContext.setStatusEnum(FlowStatus.FAILURE);
    }

    @Override
    public void onFlowStart() {
        flowLogContext.setStartTime(System.currentTimeMillis());
    }

    @Override
    public void onFlowEnd() {
        flowLogContext.setEndTime(System.currentTimeMillis());
        flowLogContext.setStatusEnum(FlowStatus.SUCCESS);
    }
}
