package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.fms.message.Message;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class WorkFlowRuleLogHandler extends ImsHandler {

    public static final Logger LOGGER = LogManager.getLogger(WorkFlowRuleLogHandler.class.getName());

    public static final String KEY = "__workflow_rule_logs__";
    @Override
    public void processMessage(Message message) {
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

