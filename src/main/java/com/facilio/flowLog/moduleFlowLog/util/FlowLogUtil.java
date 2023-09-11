package com.facilio.flowLog.moduleFlowLog.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowLog.moduleFlowLog.context.FlowLogContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;

public class FlowLogUtil {
    public static void insertFlowLog(FlowLogContext flowLogContext) throws Exception{
        if (flowLogContext == null) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Flow.FLOW_LOG);
        InsertRecordBuilder builder = new InsertRecordBuilder()
                .module(module)
                .fields(modBean.getAllFields(module.getName()));
        builder.insert(flowLogContext);
    }
}
