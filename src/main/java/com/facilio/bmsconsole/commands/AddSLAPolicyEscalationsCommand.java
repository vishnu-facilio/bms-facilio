package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
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
                WorkflowRuleAPI.deleteWorkFlowRules(workflowRuleIds);

                GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                        .table(ModuleFactory.getSLAEscalationWorkflowRuleRelModule().getTableName())
                        .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaPolicyId), NumberOperators.EQUALS));
                deleteRecordBuilder.delete();
            }

            if (CollectionUtils.isNotEmpty(slaEscalations)) {
                Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap =
                        slaEscalations.stream().collect(Collectors.toMap(
                                SLAPolicyContext.SLAPolicyEntityEscalationContext::getSlaEntityId, Function.identity()));

                for (Long slaEntityId : escalationMap.keySet()) {
                    SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(slaEntityId);

                    SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(slaEntityId);
                    List<SLAWorkflowEscalationContext> levels = slaPolicyEntityEscalationContext.getLevels();
                    if (CollectionUtils.isNotEmpty(levels)) {
//                        long maxInterval = 0;
//                        for (SLAWorkflowEscalationContext level : levels) {
//                            if (maxInterval < level.getInterval()) {
//                                maxInterval = level.getInterval();
//                            }
//                        }
//
//                        maxInterval = maxInterval * 1000;

                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = slaPolicy.getModule();
                        List<FacilioField> allFields = modBean.getAllFields(module.getName());

                        FacilioField dueField = modBean.getField(slaEntity.getDueFieldId(), module.getModuleId());

                        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> moduleRecordBuilder = new SelectRecordsBuilder<>()
                                .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                                .module(module)
                                .select(allFields)
                                .andCondition(CriteriaAPI.getCondition(dueField, CommonOperators.IS_NOT_EMPTY))
                                .andCondition(CriteriaAPI.getCondition(dueField, String.valueOf(System.currentTimeMillis()), NumberOperators.GREATER_THAN))
                                .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaPolicyId), NumberOperators.EQUALS));
                        moduleRecordBuilder.andCriteria(slaEntity.getCriteria());
                        SelectRecordsBuilder.BatchResult batchResult = moduleRecordBuilder.getInBatches("ID", 5000);
                        while (batchResult.hasNext()) {
                            List<? extends ModuleBaseWithCustomFields> list = batchResult.get();
                            for (ModuleBaseWithCustomFields moduleRecord : list) {
                                SLAWorkflowCommitmentRuleContext.addEscalationJobs("name", slaPolicyId, levels,
                                        module, dueField, slaEntity.getCriteria(), moduleRecord, slaEntity);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
