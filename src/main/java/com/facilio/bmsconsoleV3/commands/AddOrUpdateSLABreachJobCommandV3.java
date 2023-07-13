package com.facilio.bmsconsoleV3.commands;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddOrUpdateSLABreachJobCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.SLAWorkflowAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;
import io.opentelemetry.extension.annotations.WithSpan;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class AddOrUpdateSLABreachJobCommandV3 extends FacilioCommand {
    private boolean addMode;

    public AddOrUpdateSLABreachJobCommandV3(boolean addMode) {
        this.addMode = addMode;
    }

    @Override
    @WithSpan
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(recordMap)) {
            for (String moduleName : recordMap.keySet()) {
                List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);


                FacilioChain slaEntityChain = ReadOnlyChainFactory.getAllSLAEntityChain();
                FacilioContext slaEntityContext = slaEntityChain.getContext();
                slaEntityContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                slaEntityContext.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
                slaEntityChain.execute();


                List<SLAEntityContext> slaEntityList = (List<SLAEntityContext>) slaEntityContext.get(FacilioConstants.ContextNames.SLA_ENTITY_LIST);
                if (CollectionUtils.isNotEmpty(slaEntityList)) {
                    long currentTime = System.currentTimeMillis();

                    if (!addMode){
                        deleteAllExistingSLASingleRecordJob(records, "_Breach", StringOperators.ENDS_WITH, module);
                    }

                    for (ModuleBaseWithCustomFields record : records) {
                        for (SLAEntityContext entity : slaEntityList) {
                            long dueFieldId = entity.getDueFieldId();


                            FacilioField field = modBean.getField(dueFieldId, moduleName);
                            Object value = FieldUtil.getValue(record, field);


                            Criteria criteria = new Criteria();
                            criteria.addAndCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(record.getId()), NumberOperators.EQUALS));
                            criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
                            criteria.addAndCondition(CriteriaAPI.getCondition("SLA_ENTITY_ID", "slaEntityId", String.valueOf(entity.getId()), NumberOperators.EQUALS));
                            SLABreachJobExecution existingSLABreachJobExecution = SLAWorkflowAPI.getSLABreachJobExecution(criteria);

                            if (!(value instanceof Long)) {
                                // delete existing sla breach entry
                                if (existingSLABreachJobExecution != null) {
                                    deleteBreachEntryAndJob(existingSLABreachJobExecution.getId());
                                }
                                continue;
                            }

                            Long dateValue = (Long) value;

                            long breachJobId = -1l;

                            boolean deleteExistingEscalation = false;

                            SLABreachJobExecution slaBreachJobExecution = new SLABreachJobExecution();
                            slaBreachJobExecution.setSlaPolicyId(record.getSlaPolicyId());
                            slaBreachJobExecution.setModuleId(module.getModuleId());
                            slaBreachJobExecution.setRecordId(record.getId());
                            slaBreachJobExecution.setDueDateValue(dateValue);
                            slaBreachJobExecution.setSlaEntityId(entity.getId());


                            if (existingSLABreachJobExecution != null &&
                                    convertToSecond(existingSLABreachJobExecution.getDueDateValue()).equals(convertToSecond(dateValue))) {
                                continue;
                            }

                            if (existingSLABreachJobExecution != null &&
                                    !(convertToSecond(existingSLABreachJobExecution.getDueDateValue()).equals(convertToSecond(dateValue)))){
                                deleteBreachEntryAndJob(existingSLABreachJobExecution.getId());
                            }

                            if (dateValue > currentTime) {
                                deleteExistingEscalation = !addMode;
                                breachJobId = SLAWorkflowAPI.addSLABreachJobExecution(slaBreachJobExecution);
                                addSLAEntityBreachJob(breachJobId, dateValue);
                            }

                            try {
                                LOGGER.debug("Adding or updating the record ---------- \n" + "Breach Job relation table Entry " + existingSLABreachJobExecution + "due date value in record " + dateValue);
                            }catch (Exception e){

                            }

                            if (deleteExistingEscalation) {
                                AddOrUpdateSLABreachJobCommand.deleteAllExistingSLASingleRecordJob(Collections.singletonList(record), "SLAEntity_" + entity.getId() + "_Escalation_", StringOperators.STARTS_WITH, module);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private Long convertToSecond(Long dateValue){
        Long convertedValue = null;
        if (dateValue != null && dateValue > 0){
            convertedValue = dateValue/1000;
        }
        return convertedValue;
    }

    private void deleteBreachEntryAndJob(long breachJobId) throws Exception{
        SLAWorkflowAPI.deleteSLABreachJobExecution(breachJobId);
        FacilioTimer.deleteJob(breachJobId, FacilioConstants.Job.BREACH_JOB);
    }

    private void addSLAEntityBreachJob(long breachJobId, Long dueFieldValue) throws Exception {
        if (breachJobId > 0 && dueFieldValue > 0) {
            long executionTime = dueFieldValue / 1000;
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(breachJobId, FacilioConstants.Job.BREACH_JOB, executionTime, "priority");
        }
    }

    public static void deleteAllExistingSLASingleRecordJob(List<ModuleBaseWithCustomFields> records,
                                                           String ruleName, Operator operator, FacilioModule module) throws Exception {
        for (ModuleBaseWithCustomFields record : records) {
            long parentId = record.getId();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", ruleName, operator));
            criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE, false, criteria, null, null);
            if (CollectionUtils.isNotEmpty(workflowRules)) {
                List<Long> workFlowIds = workflowRules.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList());
                WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
            }
        }
    }

}
