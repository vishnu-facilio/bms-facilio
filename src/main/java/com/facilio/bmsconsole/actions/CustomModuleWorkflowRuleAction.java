package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class CustomModuleWorkflowRuleAction extends FacilioAction {

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String fetchWorkflowRules() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());

        Chain c = ReadOnlyChainFactory.getCustomModuleWorkflowRulesChain();
        c.execute(context);

        return SUCCESS;
    }
}
