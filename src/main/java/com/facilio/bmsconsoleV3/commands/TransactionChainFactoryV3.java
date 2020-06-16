package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.quotation.*;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantSpaceRelationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantUserCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddInsuranceVendorRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddVendorContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class TransactionChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getWorkPermitAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3());
        return c;
    }

    public static FacilioChain getWorkPermitAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new LoadWorkPermitLookUpsCommandV3());
        c.addCommand(new GenericGetModuleDataListCommand());
        c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        c.addCommand(new VerifyApprovalCommand());
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        c.addCommand(new RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLoggingBeforeSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInContextCommandV3());
        c.addCommand(new AddNewVisitorWhileLoggingCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromVisitsCommandV3());
        c.addCommand(new CheckForWatchListRecordCommandV3());

        return c;
    }

    public static FacilioChain getVisitorLoggingAfterSaveOnCreateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new UpdateVisitorInviteRelArrivedStateCommandV3());
        c.addCommand(new ChangeVisitorInviteStateCommandV3());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new AddNdaForVisitorLogCommandV3())
                .addCommand(new GenerateQrInviteUrlCommandV3())
                .addCommand(new VisitorFaceRecognitionCommandV3()));
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());

        return c;
    }

    public static FacilioChain getVisitorLoggingBeforeSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new PutOldVisitRecordsInContextCommandV3());
        c.addCommand(new AddOrUpdateVisitorFromVisitsCommandV3());
        return c;
    }

    public static FacilioChain getVisitorLoggingAfterSaveOnUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ChangeVisitorInviteStateCommandV3());
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        return c;
    }

    public static FacilioChain getVisitorBeforeSaveOnAddChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new AddOrUpdateLocationForVisitorCommandV3());
        c.addCommand(new CheckForVisitorDuplicationCommandV3());
        return c;
    }

    public static FacilioChain getVisitorAfterSaveOnAddChain() {
        FacilioChain c = getDefaultChain();

        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new VisitorFaceRecognitionCommand()));
        return c;
    }

    public static FacilioChain getQuotationAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateQuotationParentIdCommand());
        c.addCommand(new AddQuotationRelatedRecordsDuringReviseCommandV3());
        c.addCommand(new InsertQuotationLineItemsAndActivitiesCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTATION_ACTIVITY));
        return c;
    }

    public static FacilioChain getQuotationAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InsertQuotationLineItemsAndActivitiesCommand());
        c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.QUOTATION_ACTIVITY));
        return c;
    }

    public static FacilioChain getQuotationBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ReviseQuotationCommand());
        c.addCommand(new QuotationValidationAndCostCalculationCommand());
        return c;
    }

    public static FacilioChain getVendorsAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddVendorContactsCommandV3());
        c.addCommand(new AddInsuranceVendorRollupCommandV3());
        return c;
    }

    public static FacilioChain getTenantAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddTenantUserCommandV3());
        c.addCommand(new AddTenantSpaceRelationCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PMSettingsCommandV3());
        c.addCommand(new AddRequesterCommandV3());
        c.addCommand(new WorkOrderPreAdditionHandlingCommandV3());
        c.addCommand(new ValidateWorkOrderFieldsCommandV3());
        c.addCommand(new UpdateEventListForStateFlowCommandV3());
        c.addCommand(new AddWorkOrderCommandV3());

        return c;
    }

    public static FacilioChain getWorkOrderWorkflowsChainV3(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_CUSTOM_CHANGE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteSLAWorkFlowsCommand());
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE, WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));

        if (sendNotification) {
            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getOrgId() == 218L) {
                c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
            } else {
                c.addCommand(new ForkChainToInstantJobCommand()
                        .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION)));
            }
        }
        return c;
    }

    public static FacilioChain getWorkorderAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderAddCommandV3());
        c.addCommand(new AddWorkorderHazardsFromSafetyPlanCommandV3());
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new AddTicketActivityCommandV3());
        //c.addCommand(getAddTasksChainV3());
        c.addCommand(new AddPrerequisiteApproversCommandV3());
        c.addCommand(new FacilioCommand() {
            @Override
            public boolean executeCommand(Context context) throws Exception {
                context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(context.get(FacilioConstants.ContextNames.WORK_ORDER)));
                return false;
            }
        });
        c.addCommand(getWorkOrderWorkflowsChainV3(true));
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(true));
        c.addCommand(new AddActivitiesCommandV3());

        return c;
    }

    public static FacilioChain getWorkorderBeforeUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new FetchOldWorkordersCommandV3());
        c.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        c.addCommand(new VerifyApprovalCommandV3());
        c.addCommand(new UpdateEventListForStateFlowCommandV3());
        c.addCommand(new UpdateWorkorderFieldsForUpdateCommandV3());
        c.addCommand(new BackwardCompatibleStateFlowUpdateCommandV3());
        c.addCommand(new ToVerifyStateFlowtransitionForStartCommandV3());


        return c;
    }

    public static FacilioChain getWorkorderAfterUpdateChain(boolean sendNotification) {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FillContextAfterWorkorderUpdateCommandV3());
        c.addCommand(new VerifyQrCommand());
        c.addCommand(new SendNotificationCommandV3());
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.ASSIGNMENT_RULE));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_RULE, WorkflowRuleContext.RuleType.CHILD_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_APPROVAL_RULE, WorkflowRuleContext.RuleType.REQUEST_REJECT_RULE));
        c.addCommand(new AddOrUpdateSLABreachJobCommandV3(false));
        c.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
        c.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.WORKORDER_AGENT_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.WORKORDER_REQUESTER_NOTIFICATION_RULE, WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION))
                .addCommand(new ClearAlarmOnWOCloseCommand())
                .addCommand(new ExecuteTaskFailureActionCommand())
        );
        c.addCommand(new ConstructTicketNotesCommand());
        c.addCommand(TransactionChainFactory.getAddNotesChain());
        c.addCommand(new AddActivitiesCommand());

        return c;
    }



}
