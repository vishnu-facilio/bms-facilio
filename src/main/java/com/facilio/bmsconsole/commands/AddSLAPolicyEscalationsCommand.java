package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddSLAPolicyEscalationsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> slaEscalations = (List<SLAPolicyContext.SLAPolicyEntityEscalationContext>) context.get(FacilioConstants.ContextNames.SLA_POLICY_ESCALATION_LIST);
        Long slaPolicyId = (Long) context.get(FacilioConstants.ContextNames.SLA_POLICY_ID);
        if (slaEscalations != null && (slaPolicyId != null && slaPolicyId > 0)) {
            SLAPolicyContext slaPolicy = (SLAPolicyContext) WorkflowRuleAPI.getWorkflowRule(slaPolicyId);
            SLAWorkflowAPI.deleteSLAPolicyEscalation(slaPolicy);
            slaPolicy.setEscalations(slaEscalations);
            SLAWorkflowAPI.addEscalations(slaPolicy);
        }
        return false;
    }
}
