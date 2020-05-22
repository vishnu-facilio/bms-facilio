package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
import com.facilio.cb.command.AddOrUpdateIntentToMLCommand;
import com.facilio.cb.command.GetOrAddCurrentActiveModel;
import com.facilio.cb.command.PopulateDefaultChatBotIntentCommand;
import com.facilio.chain.FacilioChain;

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

        c.addCommand(new GetRecordIdsFromRecordMapCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        c.addCommand(new GenericGetModuleDataListCommand());
        c.addCommand(new ChangeVisitorInviteStateCommandV3());
        c.addCommand(new ExecuteWorkFlowsBusinessLogicInPostTransactionCommand());
        return c;
    }


}
