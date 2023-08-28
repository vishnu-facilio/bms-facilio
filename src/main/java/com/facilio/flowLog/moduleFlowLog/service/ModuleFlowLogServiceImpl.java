package com.facilio.flowLog.moduleFlowLog.service;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.flowLog.FlowLogService;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionContext;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionLogContext;
import com.facilio.flowLog.moduleFlowLog.enums.FlowLogType;
import com.facilio.flowLog.moduleFlowLog.enums.FlowStatus;
import com.facilio.flowLog.moduleFlowLog.util.FlowExecutionLogUtil;
import com.facilio.flowengine.executor.FlowEngineInterFace;
import com.facilio.flows.context.FlowContext;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.FlowLogHandler;
import lombok.NonNull;
import org.json.simple.JSONObject;
import com.facilio.fms.message.Message;

public class ModuleFlowLogServiceImpl extends FlowLogService {
    private FlowExecutionContext flowExecution;

    public void setFlowExecution(@NonNull FlowExecutionContext flowExecution) {
        this.flowExecution = flowExecution;
    }

    public ModuleFlowLogServiceImpl(FlowEngineInterFace flowEngineInterFace) {
        super(flowEngineInterFace);
    }
    public ModuleFlowLogServiceImpl(@NonNull FlowEngineInterFace flowEngineInterFace,@NonNull FlowExecutionContext flowExecution){
        super(flowEngineInterFace);
        this.flowExecution = flowExecution;
    }

    @Override
    public void log(String message) {
        FlowExecutionLogContext log = new FlowExecutionLogContext(flowEngineInterFace.getCurrentExecutionBlock().getId(), FlowLogType.CUSTOM,message);
        long flowExecutionId = flowExecution.getId();
        log.setFlowExecutionId(flowExecutionId);
        log.setCreatedTime(System.currentTimeMillis());
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(FlowLogHandler.KEY+"/"+orgId+"/"+flowExecutionId)
                .setOrgId(orgId)
                .setContent(log.toJSON())
        );
    }

    @Override
    public Long createUniqueIdentifier() throws Exception {
        if(flowExecution!=null){
           return flowExecution.getId();
        }
        FlowContext flow = flowEngineInterFace.getFlow();

        long recordModuleId = flow.getModuleId();
        JSONObject currentRecord = flowEngineInterFace.getCurrentRecord();
        long recordId = -1L;
        if(currentRecord!=null){
            recordId = (long) currentRecord.get("id");
        }

        FlowExecutionContext newFlowExecution = new FlowExecutionContext();
        newFlowExecution.setStartTime(System.currentTimeMillis());
        newFlowExecution.setFlowId(flow.getId());
        newFlowExecution.setRecordModuleId(recordModuleId);
        newFlowExecution.setRecordId(recordId);
        newFlowExecution.setThreadName(Thread.currentThread().getName());

        NewTransactionService.newTransaction(() -> FlowExecutionLogUtil.createFlowExecution(newFlowExecution));
        setFlowExecution(newFlowExecution);
        return newFlowExecution.getId();
    }

    @Override
    public void onFlowError() throws Exception{
        flowExecution.setStatus(FlowStatus.FAILURE);
        flowExecution.setEndTime(System.currentTimeMillis());
        NewTransactionService.newTransaction(() -> FlowExecutionLogUtil.updateFlowExecution(flowExecution));
    }

    @Override
    public void onFlowEnd() throws Exception{
        flowExecution.setStatus(FlowStatus.SUCCESS);
        flowExecution.setEndTime(System.currentTimeMillis());
        NewTransactionService.newTransaction(()-> FlowExecutionLogUtil.updateFlowExecution(flowExecution));
    }
}
