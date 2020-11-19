package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SLAEditJob extends FacilioJob {

    private final int LIMIT = 100;

    @Override
    public void execute(JobContext jc) throws Exception {
        long jobId = jc.getJobId();

        List<Map<String, Object>> slaEditJobDetails = SLAWorkflowAPI.getSLAEditJobDetails(jobId);
        if (CollectionUtils.isEmpty(slaEditJobDetails)) {
            // invalid job
            return;
        }

        Map<Long, Map<String, Object>> slaEditJobDetailsMap = new HashMap<>();
        for (Map<String, Object> map : slaEditJobDetails) {
            slaEditJobDetailsMap.put((Long) map.get("slaEntityId"), map);
        }

        FacilioChain chain = ReadOnlyChainFactory.getSLAPolicyWithChildrenChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SLA_POLICY_ID, jobId);
        chain.execute();
        SLAPolicyContext slaPolicyContext = (SLAPolicyContext) context.get(FacilioConstants.ContextNames.SLA_POLICY);

        if (slaPolicyContext == null) {
            return;
        }

        List<SLAPolicyContext.SLAPolicyEntityEscalationContext> escalations = slaPolicyContext.getEscalations();
        if (CollectionUtils.isNotEmpty(escalations)) {
            Map<Long, SLAPolicyContext.SLAPolicyEntityEscalationContext> escalationMap =
                    escalations.stream().collect(Collectors.toMap(
                            SLAPolicyContext.SLAPolicyEntityEscalationContext::getSlaEntityId, Function.identity()));

            for (Long slaEntityId : escalationMap.keySet()) {
                if (!(slaEditJobDetailsMap.containsKey(slaEntityId))) {
                    // if not found, then it is already completed..
                    continue;
                }

                Map<String, Object> jobDetails = slaEditJobDetailsMap.get(slaEntityId);
                long lastRecordId = (long) jobDetails.getOrDefault("lastRecordId", -1l);
                long slaPolicyId = (long) jobDetails.get("slaPolicyId");

                SLAEntityContext slaEntity = SLAWorkflowAPI.getSLAEntity(slaEntityId);
                SLAPolicyContext.SLAPolicyEntityEscalationContext slaPolicyEntityEscalationContext = escalationMap.get(slaEntityId);
                List<SLAWorkflowEscalationContext> levels = slaPolicyEntityEscalationContext.getLevels();

                if (CollectionUtils.isNotEmpty(levels)) {
                    long maxInterval = 0;
                    for (SLAWorkflowEscalationContext level : levels) {
                        if (maxInterval < level.getInterval()) {
                            maxInterval = level.getInterval();
                        }
                    }

                    maxInterval = maxInterval * 1000;
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = slaPolicyContext.getModule();
                    List<FacilioField> allFields = modBean.getAllFields(module.getName());

                    FacilioField dueField = modBean.getField(slaEntity.getDueFieldId(), module.getModuleId());

                    SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> moduleRecordBuilder = new SelectRecordsBuilder<>()
                            .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                            .module(module)
                            .select(allFields)
                            .andCondition(CriteriaAPI.getCondition(dueField, CommonOperators.IS_NOT_EMPTY))
                            .andCondition(CriteriaAPI.getCondition(dueField, String.valueOf(System.currentTimeMillis() - maxInterval), NumberOperators.GREATER_THAN))
                            .andCondition(CriteriaAPI.getCondition("SLA_POLICY_ID", "slaPolicyId", String.valueOf(slaPolicyId), NumberOperators.EQUALS))
                            .limit(LIMIT)
                            .orderBy("ID ASC");

                    if (lastRecordId > 0) {
                        moduleRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(lastRecordId), NumberOperators.GREATER_THAN));
                    }
                    List<? extends ModuleBaseWithCustomFields> moduleRecords = moduleRecordBuilder.get();
                    if (CollectionUtils.isEmpty(moduleRecords) || moduleRecords.size() < LIMIT) {
                        // all records for this particular sla entity is completed..
                        SLAWorkflowAPI.deleteEditJobDetails(jobId, slaEntityId);
                    }
                    else {
                        for (ModuleBaseWithCustomFields moduleRecord : moduleRecords) {
                            SLAWorkflowCommitmentRuleContext.addEscalationJobs(slaPolicyId, levels,
                                    module, dueField, slaEntity.getCriteria(), moduleRecord, slaEntity);
                        }
                        SLAWorkflowAPI.updateSLAEditJobDetails(jobId, slaEntityId, moduleRecords.get(moduleRecords.size() - 1).getId());
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(SLAWorkflowAPI.getSLAEditJobDetails(jobId))) {
            // schedule another job
            jc.setNextExecutionTime((System.currentTimeMillis() + (10 * 1000)) / 1000);
        }
    }
}
