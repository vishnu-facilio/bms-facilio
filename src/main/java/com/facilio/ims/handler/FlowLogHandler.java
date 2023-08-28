package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionLogContext;
import com.facilio.fms.message.Message;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
public class FlowLogHandler extends ImsHandler {
    public static final String KEY = "__flow_logs__";
    private static final Logger LOGGER = LogManager.getLogger(FlowLogHandler.class.getName());
    @Override
    public void processMessage(Message message) {
        FlowExecutionLogContext flowExecutionLog = null;
        try {
            JSONObject content = message.getContent();
            flowExecutionLog = FieldUtil.getAsBeanFromJson(content, FlowExecutionLogContext.class);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            moduleCRUD.addFlowExecutionLog(flowExecutionLog);
        } catch (Exception e) {
            LOGGER.error("Error in inserting flow log", e );
            if(flowExecutionLog!=null){
                LOGGER.info("Inserting Flow Execution log failed for flowExecutionId:"
                        +flowExecutionLog.getFlowExecutionId()
                        +"::content:"+message.getContent());
            }
        }
    }
}
