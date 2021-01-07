package com.facilio.bmsconsole.commands;

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
import com.facilio.tasker.job.JobContext;
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

            FacilioChain allSLAChain = ReadOnlyChainFactory.getAllSLAChain();
            FacilioContext slaContext = allSLAChain.getContext();
            slaContext.put(FacilioConstants.ContextNames.SLA_POLICY_ID, slaPolicyId);
            allSLAChain.execute();
            List<SLAWorkflowCommitmentRuleContext> slaCommitments = (List<SLAWorkflowCommitmentRuleContext>) slaContext.get(FacilioConstants.ContextNames.SLA_RULE_MODULE_LIST);

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getSLAEscalationWorkflowRuleRelModule().getTableName())
                    .select(FieldFactory.getSLAEscalationWorkflowRuleRelFields())
                    .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaPolicyId), NumberOperators.EQUALS));
            List<Map<String, Object>> maps = builder.get();
            if (CollectionUtils.isNotEmpty(maps)) {
                List<Long> workflowRuleIds = new ArrayList<>();
                for (Map<String, Object> map : maps) {
                    Long workflowRuleId = (Long) map.get("workflowRuleId");
                    workflowRuleIds.add(workflowRuleId);
                }
                WorkflowRuleAPI.inActivateWorkFlowRules(workflowRuleIds);

                GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                        .table(ModuleFactory.getSLAEscalationWorkflowRuleRelModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaPolicyId), NumberOperators.EQUALS));
                deleteRecordBuilder.delete();
            }

            if (CollectionUtils.isNotEmpty(slaEscalations)) {
                Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap =
                        slaEscalations.stream().collect(Collectors.toMap(
                                SLAPolicyContext.SLAPolicyEntityEscalationContext::getSlaEntityId, Function.identity()));

                List<Map<String, Object>> slaEditJobDetails = new ArrayList<>();
                for (SLAPolicyContext.SLAPolicyEntityEscalationContext escalation : slaEscalations) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("slaEntityId", escalation.getSlaEntityId());
                    map.put("slaPolicyId", slaPolicyId);
                    slaEditJobDetails.add(map);
                }
                SLAWorkflowAPI.addSLAEditJobDetails(slaEditJobDetails);
                JobContext slaEditJob = FacilioTimer.getJob(slaPolicyId, "SLAEditJob");
                if (slaEditJob != null) {
                    if (slaEditJob.isActive()) {
                        throw new IllegalArgumentException("Previous edit process is still active. Please try after sometime");
                    }
                    FacilioTimer.deleteJob(slaPolicyId, "SLAEditJob");
                }
                FacilioTimer.scheduleOneTimeJobWithDelay(slaPolicyId, "SLAEditJob", 1, "priority");
            }
        }
        return false;
    }
}
