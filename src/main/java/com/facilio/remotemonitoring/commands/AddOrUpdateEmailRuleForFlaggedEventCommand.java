package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.context.FlaggedEventWorkorderFieldMappingContext;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AddOrUpdateEmailRuleForFlaggedEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                WorkflowRuleContext emailRule = flaggedEventRule.getEmailRule();
                WorkflowRuleContext delayedEmailRule = flaggedEventRule.getDelayedEmailRule();
                FlaggedEventRuleContext updateFlaggedEventRule = new FlaggedEventRuleContext();
                updateFlaggedEventRule.setId(flaggedEventRule.getId());
                if(emailRule != null && emailRule.getActions() != null) {
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME,FlaggedEventModule.MODULE_NAME),String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));
                    updateFlaggedEventRule.setEmailNotificationRuleId(FlaggedEventUtil.addEmailRule(emailRule,"Email Notification Rule for Flagged Event from Rule- " + flaggedEventRule.getId(),criteria));
                }
                if(delayedEmailRule != null) {
                    if(flaggedEventRule.getFollowUpEmailDelayTimeOne() != null) {
                        Criteria criteria = new Criteria();
                        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME, FlaggedEventModule.MODULE_NAME), String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));
                        updateFlaggedEventRule.setDelayedEmailRuleOneId(FlaggedEventUtil.addScheduledEmailRule(delayedEmailRule, "Email Scheduled One Notification Rule for Flagged Event from Rule- " + flaggedEventRule.getId(), criteria,flaggedEventRule.getFollowUpEmailDelayTimeOne()));

                        if(flaggedEventRule.getFollowUpEmailDelayTimeTwo() != null) {
                            if(flaggedEventRule.getFollowUpEmailDelayTimeOne() >= flaggedEventRule.getFollowUpEmailDelayTimeTwo()) {
                                FacilioUtil.throwIllegalArgumentException(true,"Invalid follow up email delay time");
                            }
                            Criteria criteria2 = new Criteria();
                            criteria2.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME, FlaggedEventModule.MODULE_NAME), String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));
                            updateFlaggedEventRule.setDelayedEmailRuleTwoId(FlaggedEventUtil.addScheduledEmailRule(delayedEmailRule, "Email Scheduled Two Notification Rule for Flagged Event from Rule- " + flaggedEventRule.getId(), criteria2,flaggedEventRule.getFollowUpEmailDelayTimeTwo()));
                        }
                    }
                }
                Long workflowId = addWorkflowCriteria(flaggedEventRule);
                if(workflowId != null) {
                    updateFlaggedEventRule.setWorkflowId(workflowId);
                }
                List<FacilioField> fieldsList = Arrays.asList(modBean.getField("workflowId",FlaggedEventRuleModule.MODULE_NAME),modBean.getField("emailNotificationRuleId",FlaggedEventRuleModule.MODULE_NAME),modBean.getField("delayedEmailRuleOneId",FlaggedEventRuleModule.MODULE_NAME),modBean.getField("delayedEmailRuleTwoId",FlaggedEventRuleModule.MODULE_NAME));
                V3RecordAPI.updateRecord(updateFlaggedEventRule, modBean.getModule(FlaggedEventRuleModule.MODULE_NAME), fieldsList);
            }
        }
        return false;
    }

    private static Long addWorkflowCriteria(FlaggedEventRuleContext rule) throws Exception {
        WorkflowContext workflow = rule.getWorkflowContext();
        if(workflow != null) {
            workflow.setIsLogNeeded(true);
            FacilioChain chain = TransactionChainFactory.getAddWorkflowChain();
            if(workflow.getId() > 0) {
                chain = TransactionChainFactory.getUpdateWorkflowChain();
            }
            FacilioContext context = chain.getContext();
            context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
            try {
                chain.execute();
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            WorkflowContext addedWorkflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
            if(addedWorkflow != null && addedWorkflow.getId() > 0) {
                return addedWorkflow.getId();
            }
        }
        return null;
    }
}