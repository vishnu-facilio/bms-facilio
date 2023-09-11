package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.flowLog.moduleFlowLog.context.FlowLogContext;
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
        FlowLogContext flowLogContext = null;
        try {
            JSONObject content = message.getContent();
            flowLogContext = FieldUtil.getAsBeanFromJson(content, FlowLogContext.class);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            moduleCRUD.addFlowLog(flowLogContext);
        } catch (Exception e) {
            LOGGER.info("ERROR IN ADDING FLOW LOGS", e);
        }
    }
}
