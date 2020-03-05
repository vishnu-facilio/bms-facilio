package com.facilio.bmsconsole.workflow.rule;

import org.apache.commons.chain.Context;

import java.util.Map;

public class ApprovalStateFlowRuleContext extends AbstractStateFlowRuleContext {

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        super.executeTrueActions(record, context, placeHolders);
    }
}
