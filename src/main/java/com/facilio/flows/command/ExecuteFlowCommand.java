package com.facilio.flows.command;

import com.facilio.blockfactory.BlockFactory;
import com.facilio.blockfactory.blocks.BaseBlock;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.executor.FlowEngine;
import com.facilio.flows.context.FlowContext;
import com.facilio.flows.context.FlowTransitionContext;
import com.facilio.flows.util.FlowUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ExecuteFlowCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long flowId = (long) context.get(FacilioConstants.ContextNames.FLOW_ID);
        long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);

        FlowContext flow = FlowUtil.getFlow(flowId);

        FacilioUtil.throwIllegalArgumentException(flow==null,"Invalid flow id");
        FacilioUtil.throwIllegalArgumentException(recordId==-1l,"recordId id cannot be empty");
        FacilioUtil.throwIllegalArgumentException(moduleName==null,"moduleName cannot be empty");



        List<FlowTransitionContext> flowTransitions = FlowUtil.getFlowTransitionListWithExtendedConfig(flowId);

        Object record = V3Util.getRecord(moduleName, recordId, null);

        BaseBlock startBlock = BlockFactory.buildFlowGraph(flowTransitions);

        JSONObject currentRecord = FieldUtil.getAsJSON(record);
        HashMap<String, Object> memory = new HashMap<>();
        FlowEngine flowEngine = new FlowEngine(flow,currentRecord);
        memory.put(moduleName,currentRecord);
        flowEngine.execute(startBlock,memory);
        context.put(FacilioConstants.ContextNames.MEMORY, memory);
        return false;
    }
}
