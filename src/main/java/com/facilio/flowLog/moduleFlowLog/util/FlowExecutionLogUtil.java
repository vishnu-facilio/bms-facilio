package com.facilio.flowLog.moduleFlowLog.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionContext;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionLogContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;

public class FlowExecutionLogUtil {
    public static void createFlowExecution(FlowExecutionContext flowExecution) throws Exception{
        if(flowExecution == null){
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Flow.FLOW_EXECUTION);
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()));
        builder.insert(flowExecution);
    }
    public static void updateFlowExecution(FlowExecutionContext flowExecution) throws Exception{
        if(flowExecution==null ||flowExecution.getId()==-1l){
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Flow.FLOW_EXECUTION);
        UpdateRecordBuilder builder = new UpdateRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(flowExecution.getId(),module));

        builder.update(flowExecution);
    }
    public static void insertFlowExecutionLog(FlowExecutionLogContext flowExecutionLog) throws Exception{
        if (flowExecutionLog == null) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Flow.FLOW_EXECUTION_LOG);
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()));
        builder.insert(flowExecutionLog);
    }
}
