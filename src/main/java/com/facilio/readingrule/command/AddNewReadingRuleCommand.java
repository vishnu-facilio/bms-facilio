package com.facilio.readingrule.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

public class AddNewReadingRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

        User currentUser = AccountUtil.getCurrentUser();
        readingRule.setOrgId(currentUser.getOrgId());
        readingRule.setCreatedBy(currentUser.getId());
        readingRule.setCreatedTime(System.currentTimeMillis());
        readingRule.setModuleId((Long)context.get(FacilioConstants.ContextNames.MODULE_ID));
        readingRule.setWorkflowId(readingRule.getWorkflowContext().getId());

        NewReadingRuleAPI.addReadingRule(readingRule);

        context.put(NamespaceConstants.NAMESPACE, readingRule.getNs());
        context.put(NamespaceConstants.PARENT_RULE_ID, readingRule.getId());
        context.put(NamespaceConstants.NAMESPACE_FIELDS, readingRule.getNs().getFields());
        context.put(FacilioConstants.ContextNames.ASSETS, readingRule.getMatchedResources());

        return Boolean.FALSE;
    }

}
