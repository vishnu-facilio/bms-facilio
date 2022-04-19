package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class UpdateSystemButtonCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        SystemButtonRuleContext systemButtonRecord = (SystemButtonRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);

        if (StringUtils.isEmpty(moduleName)){
            throw new Exception("Module name cannot be null");
        }

        if (systemButtonRecord == null || systemButtonRecord.getId() < 0) {
            return false;
        }

        systemButtonRecord.setRuleType(WorkflowRuleContext.RuleType.SYSTEM_BUTTON);
        systemButtonRecord.setActivityType(EventType.CUSTOM_BUTTON);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Module name cannot be empty");
        }

        systemButtonRecord.setModule(module);
        systemButtonRecord.setButtonType(-1);
        systemButtonRecord.setIdentifier(null);
        systemButtonRecord.setPositionType(-1);
        FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
        FacilioContext workflowContext = chain.getContext();
        workflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, systemButtonRecord);
        chain.execute();

        return false;
    }
}
