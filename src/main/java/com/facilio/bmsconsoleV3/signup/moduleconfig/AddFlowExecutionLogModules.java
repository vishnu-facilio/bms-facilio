package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.List;

public class AddFlowExecutionLogModules extends BaseModuleConfig{
    @Override
    public void addData() throws Exception {
        FacilioModule flowExecutionModule = constructFlowExecutionModule();
        FacilioModule flowExecutionLogModule = constructFlowExecutionLogModule();
        List<FacilioModule> modules = new ArrayList<>();
        modules.add(flowExecutionModule);
        modules.add(flowExecutionLogModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
    }
    private FacilioModule constructFlowExecutionModule() throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Flow.FLOW_EXECUTION,
                "Flow Execution",
                "FlowExecution",
                FacilioModule.ModuleType.BASE_ENTITY,
                false
        );
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("flowId","Flow ID","FLOW_ID",module, FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("recordId","Record ID","RECORD_ID",module, FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("recordModuleId","Record ModuleID","recordModuleId",module, FieldType.NUMBER));
        SystemEnumField status = FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
        status.setEnumName("FlowStatus");
        fields.add(status);

        fields.add(FieldFactory.getDefaultField("startTime","Start Time","START_TIME",module, FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("endTime","End Time","END_TIME",module, FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("threadName","Thread Name","THREAD_NAME",module, FieldType.STRING));
        module.setFields(fields);
        return module;
    }
    private FacilioModule constructFlowExecutionLogModule() throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Flow.FLOW_EXECUTION_LOG,
                "Flow Execution Log",
                "FlowExecutionLog",
                FacilioModule.ModuleType.SUB_ENTITY,
                false
        );
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField("flowExecutionId","Flow Execution ID","FLOW_EXECUTION_ID",module, FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("blockId","Block ID","BLOCK_ID",module, FieldType.NUMBER));

        SystemEnumField status = FieldFactory.getDefaultField("logType","Log Type","LOG_TYPE",FieldType.SYSTEM_ENUM);
        status.setEnumName("FlowLogType");
        fields.add(status);

        fields.add(FieldFactory.getDefaultField("logMessage","Log Message","LOG_MESSAGE",module, FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("createdTime","Created Time","CREATED_TIME",module, FieldType.DATE_TIME));
        module.setFields(fields);

        return module;
    }
}
