package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

public class AddSupportEmailAndWorkFlowRuleCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddSupportEmailAndWorkFlowRuleCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);

        if (agent.getAgentTypeEnum() != AgentType.EMAIL) return false;

        if (!context.containsKey(AgentConstants.SUPPORT_EMAIL_CONTEXT))
            throw new Exception("SupportEmailContext missing from context " + context);

        SupportEmailContext supportEmailContext = (SupportEmailContext) context.get(AgentConstants.SUPPORT_EMAIL_CONTEXT);

        long supportMailId = getSupportMailId(supportEmailContext);
        agent.setSupportEmailId(supportMailId);

        return false;
    }

    private long getSupportMailId(SupportEmailContext supportEmail) throws Exception {
        Long ruleId = createAndGetWorkflowRuleId();
        supportEmail.setSupportRuleId(ruleId);
        supportEmail.setEmailSourceType(SupportEmailContext.EmailSourceType.AGENT.getIndex());

        LOGGER.info("Adding Support Email: " + supportEmail);

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportEmail);

        FacilioChain addSupportEmail = FacilioChainFactory.getAddSupportEmailChain();
        addSupportEmail.execute(context);

        long supportEmailId = supportEmail.getId();
        if (supportEmailId > 0) {
            LOGGER.info("Support Email Id: " + supportEmailId);
            return supportEmailId;
        }
        throw new Exception("Exception while adding support email");
    }

    private static Long createAndGetWorkflowRuleId() throws Exception {
        ActionContext actionContext = new ActionContext();
        actionContext.setActionType(ActionType.PUSH_TO_KAFKA);

        FacilioModule customMailMessagesModule = Constants.getModBean().getModule("customMailMessages");

        WorkflowRuleContext rule = new WorkflowRuleContext();
        rule.setModule(customMailMessagesModule);
        rule.setActivityType(EventType.CREATE);
        rule.setName("Agent Support Mail Configuration");
        rule.setActions(Collections.singletonList(actionContext));

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext ruleContext = chain.getContext();
        ruleContext.put(FacilioConstants.ContextNames.MODULE_NAME, customMailMessagesModule.getName());
        ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
        chain.execute();

        return rule.getId();
    }
}
