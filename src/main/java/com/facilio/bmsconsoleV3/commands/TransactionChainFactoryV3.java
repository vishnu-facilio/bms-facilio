package com.facilio.bmsconsoleV3.commands;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.quotation.InsertQuotationLineItemsAndActivitiesCommand;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationValidationAndCostCalculationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.ReviseQuotationCommand;
import com.facilio.bmsconsoleV3.commands.quotation.UpdateQuotationParentIdCommand;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantSpaceRelationCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.AddTenantUserCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.ValidateTenantSpaceCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddInsuranceVendorRollupCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddOrUpdateLocationForVendorCommandV3;
import com.facilio.bmsconsoleV3.commands.vendor.AddVendorContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.AddOrUpdateLocationForVisitorCommandV3;
import com.facilio.bmsconsoleV3.commands.visitor.CheckForVisitorDuplicationCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.*;
import com.facilio.bmsconsoleV3.commands.workpermit.LoadWorkPermitLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.workpermit.RollUpWorkOrderFieldOnWorkPermitApprovalCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

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

}
