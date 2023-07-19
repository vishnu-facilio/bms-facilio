package com.facilio.wmsv2.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.message.Message;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class WorkFlowRuleLogHandler extends BaseHandler {

    public static final Logger LOGGER = LogManager.getLogger(WorkFlowRuleLogHandler.class.getName());


    public void processOutgoingMessage(Message message) {
        try {
            JSONObject content = message.getContent();
            if(content==null)
                return;

            WorkflowRuleLogContext workflowRuleLogContext=FieldUtil.getAsBeanFromJson(content, WorkflowRuleLogContext.class);

            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            moduleCRUD.addWorkflowRuleLog(workflowRuleLogContext,workflowRuleLogContext.getActions());

        }catch (Exception e){
            LOGGER.error("Error in inserting log", e);
        }
    }

}

