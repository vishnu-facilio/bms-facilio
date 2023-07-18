package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkflowRuleRecordRelationshipContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CreateOneTimeExecutableJobs extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(CreateOneTimeExecutableJobs.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);

            if(rule == null){
                return false;
            }

            List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            boolean isNotValid = CollectionUtils.isEmpty(records) || rule.getTime() != null;

            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SCHEDULED_WORKFLOW_RULE) || isNotValid) {
                updateRuleEndTime(rule);
                return false;
            }

            List<Long> recordIds = records.stream().map(rec -> rec.getId()).collect(Collectors.toList());
            List<WorkflowRuleRecordRelationshipContext> rels = WorkflowRuleAPI.getRuleFromRuleAndRecordRelationshipTable(
                    recordIds, rule.getModuleId(), Collections.singletonList(rule.getId()));

            List<Long> recordsToBeIgnored = rels.stream().map(rel -> rel.getRecordId()).collect(Collectors.toList());

            for (ModuleBaseWithCustomFields record : records) {
                if (recordsToBeIgnored.contains(record.getId())) {
                    continue;
                }

                if (rule.getDateField() != null) {
                    Long dateFieldValue = (Long) FieldUtil.getValue(record, rule.getDateField());

                    if (dateFieldValue == null) {
                        continue;
                    }

                    Long executionTime = WorkflowRuleAPI.getOneTimeRuleExecutionTime(rule, dateFieldValue);
                    executionTime = rule.validatedExecutionTime(executionTime);

                    Long id = WorkflowRuleAPI.addScheduledRuleRecordRelation(record.getId(), rule, dateFieldValue, executionTime);
                    if (id != null) {
                        LOGGER.debug("One Time Job is created for id: " + id);
                    }
                }
            }
            updateRuleEndTime(rule);
            context.put("executeWorkFlow", false);
        }catch (Exception e){
            LOGGER.error("Exception at CreateOneTimeExecutableJobs :",e);
        }
        return false;
    }
    public static void updateRuleEndTime(WorkflowRuleContext rule) throws Exception {
        WorkflowRuleContext updatedRule = new WorkflowRuleContext();
        updatedRule.setRuleEndTime(rule.getRuleEndTime() / 1000);
        updatedRule.setId(rule.getId());
        WorkflowRuleAPI.updateWorkflowRule(updatedRule);
    }
}
